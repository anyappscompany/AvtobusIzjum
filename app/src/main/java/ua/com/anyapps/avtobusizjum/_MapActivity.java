package ua.com.anyapps.avtobusizjum;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class _MapActivity extends FragmentActivity implements OnMapReadyCallback {
    private final String TAG = "debapp";

    private GoogleMap mMap;

    public static final long UPDATE_INTERVAL = 3 * 1000;
    // run on another Thread to avoid crash
    private Handler mHandler = new Handler();
    // timer handling
    private Timer mTimer = null;
    private boolean gpsStatusMessageShow = false;

    private LocationManager locationManager;
    private ArrayList<Route> routes = new ArrayList<Route>();
    private Context context;

    private LatLng lastSelfLocation;

    public static final int REQUEST_LOCATION_PERMISSION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout._activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        context = this;
    }

    class UpdateByTimerTask extends TimerTask {

        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {

                @Override
                public void run() {


                    //long time= System.currentTimeMillis();
                    long currentTime;
                    int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                    int currentMinute = Calendar.getInstance().get(Calendar.MINUTE);
                    int currentSecond = Calendar.getInstance().get(Calendar.SECOND);

                    currentTime = currentHour * 3600000 + currentMinute * 60000 + currentSecond * 1000;

                    //Log.d(TAG, "currentTime " + currentTime);

                    // 1 min = 60 000

                    //Log.d(TAG, "Всего путей " + routes.size());
                    //AIzaSyAPQll3q9b2gh1jr8w4FizJ5XNtbMZvJVs
                    for (int i = 0; i < routes.size(); i++) {
                        int pointIndex = 0;
                        if (routes.get(i).direction == Directions.FORWARD_MOVEMENT) {
                            pointIndex = (int) Math.round((float) ((float) (currentTime - routes.get(i).busDeparture) * (float) routes.get(i).getAverageSpeed()) / (float) (routes.get(i).getDistance() / routes.get(i).getTotalCoordinatsInRoute()));
                        } else {
                            pointIndex = routes.get(i).getTotalCoordinatsInRoute() - (1 + (int) Math.round((float) ((float) (currentTime - routes.get(i).busDeparture) * (float) routes.get(i).getAverageSpeed()) / (float) (routes.get(i).getDistance() / routes.get(i).getTotalCoordinatsInRoute())));
                        }

//ожидание на станции
                        if ((routes.get(i).busDeparture - currentTime) <= 60000 && !routes.get(i).busExists() && (routes.get(i).busDeparture - currentTime) > 0) {

                            routes.get(i).setBusStatus(BusStatuses.BUS_PREPARING_FOR_DEPARTURE);
                            routes.get(i).createBus(routes.get(i).getRouteNum());

                            if (routes.get(i).direction == Directions.FORWARD_MOVEMENT) {
                                routes.get(i).markerMove(0, currentTime);
                            } else {
                                routes.get(i).markerMove(routes.get(i).getTotalCoordinatsInRoute() - 1, currentTime);
                            }
                        }

                        // время отправления и прибытия
                        //Log.d(TAG, "Отправление в " + routes.get(i).busDeparture + " Текущее время " + currentTime + " Прибытие в " + routes.get(i).busArrival);
                        if (currentTime >= routes.get(i).busDeparture && currentTime <= routes.get(i).busArrival) {
                            //Log.d(TAG, "Автобус в пути " + routes.get(i).busNumber + " скорость in ms " + routes.get(i).getAverageSpeed());
                            //Log.d(TAG, routes.get(i).getAverageSpeed() * 3600 + "km/h");
                            //Log.d(TAG, "current distance in metres " + (currentTime - routes.get(i).busDeparture) * routes.get(i).getAverageSpeed());

                            //Log.d(TAG, "total points " + routes.get(i).getTotalCoordinatsInRoute());

                            //Log.d(TAG, "IndexCoord " + pointIndex);

                            //Log.d(TAG, "D-" + routes.get(i).busDeparture + " C-" + currentTime);
                            if (!routes.get(i).busExists()) {
                                routes.get(i).createBus(routes.get(i).getRouteNum());
                            }

                            routes.get(i).setBusStatus(BusStatuses.BUS_IN_TRANSIT);
                            routes.get(i).markerMove(pointIndex, currentTime);

                            //создать маркер и перемещать
                        }
                    }
                }

            });
        }
    }

    private PolylineOptions busPathOptions;
    private Polyline busPath = null;
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        myPosition = new MyPosition(mMap);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker m) {
                String routeNum;
                routeNum  = m.getTag().toString();
                Log.d(TAG, "EEEEEEEEEEEEEEEEE" + m.getTag());
                //Log.d(TAG, "Рисуется путь с точками " + busPathOptions.getPoints().size() + " " + routeCoordinatesLat.size() + " номер пути " + routeNum + " " + m.getTag());
                // задание настроек траектории пути
                busPathOptions = new PolylineOptions();
                busPathOptions.color(context.getResources().getColor(R.color.colorPrimaryDark) );
                busPathOptions.width( 10 );
                busPathOptions.visible( true );

                List<String> coordinatesLat = new ArrayList<>();
                List<String> coordinatesLon = new ArrayList<>();

                if(routeNum=="1"){
                    coordinatesLat = route1CoordinatesLat;
                    coordinatesLon = route1CoordinatesLon;
                }else if(routeNum=="2"){
                    coordinatesLat = route2CoordinatesLat;
                    coordinatesLon = route2CoordinatesLon;
                }


                for(int h=0; h<route1CoordinatesLat.size(); h++)
                {
                    busPathOptions.add( new LatLng( Float.parseFloat(coordinatesLat.get(h)), Float.parseFloat(coordinatesLon.get(h)) ) );

                }
                if(busPath==null) {
                    busPath = mMap.addPolyline(busPathOptions);
                }else{
                    busPath.remove();
                    busPath = null;
                    busPath = mMap.addPolyline(busPathOptions);
                }

                return false;
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener()
        {
            @Override
            public void onMapClick(LatLng arg0)
            {
                if(busPath!=null) {
                    busPath.remove();
                    busPath = null;
                }
            }
        });


        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                LinearLayout info = new LinearLayout(context);
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(context);
                title.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(context);
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });



        // добаввлений путей
        initRoutes();


        if (mTimer != null) {
            mTimer.cancel();
        } else {
            mTimer = new Timer();
        }
        // запуск обновления маркеров
        mTimer.scheduleAtFixedRate(new UpdateByTimerTask(), 0, UPDATE_INTERVAL);

    }

    List<String> route1CoordinatesLat = new ArrayList<>();
    List<String> route1CoordinatesLon = new ArrayList<>();

    List<String> route2CoordinatesLat = new ArrayList<>();
    List<String> route2CoordinatesLon = new ArrayList<>();
    private void initRoutes() {
        /*List<String> route1CoordinatesLat = Arrays.asList(getResources().getStringArray(R.array.route1_lat));
        List<String> route1CoordinatesLon = Arrays.asList(getResources().getStringArray(R.array.route1_lon));
        Route busRoute1 = new Route("Автобус №1", timeToMillis(5, 20), timeToMillis(5, 45), 9100, Directions.FORWARD_MOVEMENT, this);
        busRoute1.setGeoCoordinatesAlongAnEntireRoute(route1CoordinatesLat, route1CoordinatesLon);
        busRoute1.setMap(mMap);
        routes.add(busRoute1);*/
        route1CoordinatesLat = Arrays.asList(getResources().getStringArray(R.array.route1_lat));
        route1CoordinatesLon = Arrays.asList(getResources().getStringArray(R.array.route1_lon));

        route2CoordinatesLat = Arrays.asList(getResources().getStringArray(R.array.route2_lat));
        route2CoordinatesLon = Arrays.asList(getResources().getStringArray(R.array.route2_lon));

        int[] timeWay1ForwardStart = getResources().getIntArray(R.array.way1_forward_start);
        int[] timeWay1ForwardEnd = getResources().getIntArray(R.array.way1_forward_end);
        int[] timeWay1BackStart = getResources().getIntArray(R.array.way1_back_start);
        int[] timeWay1BackEnd = getResources().getIntArray(R.array.way1_back_end);

        int[] timeWay2ForwardStart = getResources().getIntArray(R.array.way2_forward_start);
        int[] timeWay2ForwardEnd = getResources().getIntArray(R.array.way2_forward_end);

        // МАРШРУТ 1
        for (int t = 0; t < timeWay1ForwardStart.length; t++) {
            //Log.d(TAG, "Отправление в " + timeWayForwardStart[t] + " прибытие в " + timeWayForwardEnd[t]);
            Route bus = new Route("По маршруту №1.", timeWay1ForwardStart[t], timeWay1ForwardEnd[t], 9100, Directions.FORWARD_MOVEMENT, this);
            bus.setGeoCoordinatesAlongAnEntireRoute(route1CoordinatesLat, route1CoordinatesLon, "1");
            bus.setMap(mMap);
            routes.add(bus);
        }
        for (int t = 0; t < timeWay1BackStart.length; t++) {
            Route bus = new Route("По маршруту №1", timeWay1BackStart[t], timeWay1BackEnd[t], 9100, Directions.BACK_MOVEMENT, this);
            bus.setGeoCoordinatesAlongAnEntireRoute(route1CoordinatesLat, route1CoordinatesLon, "1");
            bus.setMap(mMap);
            routes.add(bus);
        }

        // МАРШРУТ 2
        for (int t = 0; t < timeWay2ForwardStart.length; t++) {
            //Log.d(TAG, "Отправление в " + timeWayForwardStart[t] + " прибытие в " + timeWayForwardEnd[t]);
            Route bus = new Route("По маршруту №2", timeWay2ForwardStart[t], timeWay2ForwardEnd[t], 13300, Directions.FORWARD_MOVEMENT, this);
            bus.setGeoCoordinatesAlongAnEntireRoute(route2CoordinatesLat, route2CoordinatesLon, "2");
            bus.setMap(mMap);
            routes.add(bus);
        }

    }

    private long timeToMillis(int h, int m) {
        return (h * 60 + m) * 60000;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Log.d(TAG, "RESUM");

        if (mMap != null) {
            // очистка карты
            // удаление отметки текущего положения
            // пересоздание автобусов
            mMap.clear();
            myPosition.deletePosition();
            initRoutes();
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000 * 3, 10, locationListener);
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 1000 * 3, 10,
                locationListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
    }





    MyPosition myPosition;

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            //Log.d(TAG, "Location lat: " + location.getLatitude() + " lon: " + location.getLongitude());
            lastSelfLocation = new LatLng(location.getLatitude(), location.getLongitude());
            if(!myPosition.created){
                myPosition.createPosition(lastSelfLocation);

                //LatLng voloh = new LatLng(location.getLatitude(), location.getLongitude());
                //mMap.addMarker(new MarkerOptions().position(voloh).title("Marker in Sydney"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(lastSelfLocation));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);
            }else{
                Log.d(TAG, "Позиция изменена");
                //myPosition.movePosition(lastSelfLocation);
                myPosition.deletePosition();
                myPosition.createPosition(lastSelfLocation);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {


            if (provider.equals(LocationManager.GPS_PROVIDER)) {
                Log.d(TAG, "Status GPS_PROVIDER: " + String.valueOf(status));
            } else if (provider.equals(LocationManager.NETWORK_PROVIDER)) {
                Log.d(TAG,"Status NETWORK_PROVIDER: " + String.valueOf(status));
            }
            Log.d(TAG, "SSS"+String.valueOf(status));
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d(TAG, "Enab");
        }


        @Override
        public void onProviderDisabled(String provider) {
            Log.d(TAG, "Disa");
        }
    };
}
