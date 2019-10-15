package ua.com.anyapps.avtobusizjum;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private final String TAG = "debapp";

    // координаты из предыдущей активити
    private double startLat;
    private double startLon;
    private GoogleMap mMap;
    private MyPosition myPosition;
    private LatLng lastSelfLocation;

    private LocationManager locationManager;
    public static final int REQUEST_LOCATION_PERMISSION = 0;

    public static final long UPDATE_INTERVAL = 3 * 1000;
    private Handler mHandler = new Handler();
    private Timer mTimer = null;
    private ArrayList<Route> routes = new ArrayList<Route>();
    private Context context;
    private PolylineOptions busPathOptions;
    private Polyline busPath = null;

    List<String> route1CoordinatesLat = new ArrayList<>();
    List<String> route1CoordinatesLon = new ArrayList<>();

    List<String> route2CoordinatesLat = new ArrayList<>();
    List<String> route2CoordinatesLon = new ArrayList<>();

    List<String> route3CoordinatesLat = new ArrayList<>();
    List<String> route3CoordinatesLon = new ArrayList<>();

    List<String> route4CoordinatesLat = new ArrayList<>();
    List<String> route4CoordinatesLon = new ArrayList<>();

    List<String> route5CoordinatesLat = new ArrayList<>();
    List<String> route5CoordinatesLon = new ArrayList<>();

    List<String> route6CoordinatesLat = new ArrayList<>();
    List<String> route6CoordinatesLon = new ArrayList<>();

    List<String> route61CoordinatesLat = new ArrayList<>();
    List<String> route61CoordinatesLon = new ArrayList<>();

    List<String> route7CoordinatesLat = new ArrayList<>();
    List<String> route7CoordinatesLon = new ArrayList<>();

    List<String> route8CoordinatesLat = new ArrayList<>();
    List<String> route8CoordinatesLon = new ArrayList<>();

    List<String> route9CoordinatesLat = new ArrayList<>();
    List<String> route9CoordinatesLon = new ArrayList<>();

    List<String> route10CoordinatesLat = new ArrayList<>();
    List<String> route10CoordinatesLon = new ArrayList<>();

    List<String> route11CoordinatesLat = new ArrayList<>();
    List<String> route11CoordinatesLon = new ArrayList<>();

    List<String> route13CoordinatesLat = new ArrayList<>();
    List<String> route13CoordinatesLon = new ArrayList<>();

    List<String> route14CoordinatesLat = new ArrayList<>();
    List<String> route14CoordinatesLon = new ArrayList<>();

    List<String> route15CoordinatesLat = new ArrayList<>();
    List<String> route15CoordinatesLon = new ArrayList<>();

    List<String> route16CoordinatesLat = new ArrayList<>();
    List<String> route16CoordinatesLon = new ArrayList<>();

    List<String> route17CoordinatesLat = new ArrayList<>();
    List<String> route17CoordinatesLon = new ArrayList<>();

    /*List<String> route21CoordinatesLat = new ArrayList<>();
    List<String> route21CoordinatesLon = new ArrayList<>();*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        FragmentManager fm = getSupportFragmentManager();

        /*MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);*/

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

    @Override
    protected void onResume() {
        super.onResume();

        // проверка прав при каждом показе активити
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            // есть разрешения
            setLocationListener();
        }else{
            // разрешения отсутствуют. запрос
            if (Build.VERSION.SDK_INT >= 23 && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    // показать объяснение зачем включать и запросить разрешение
                    explanationAndPermissionRequest();
                }else{
                    // запросить разрешение
                    requestLocationPermissions();
                }
            }
        }

        if (mMap != null) {
            // очистка карты
            // удаление отметки текущего положения
            // пересоздание автобусов
            mMap.clear();
            myPosition.deletePosition();
            initRoutes();
            if(lastSelfLocation!=null){
                // если нет отметки на карте, то создать и переместиться туда
                if(!myPosition.created){
                    myPosition.createPosition(lastSelfLocation);

                    //LatLng voloh = new LatLng(location.getLatitude(), location.getLongitude());
                    //mMap.addMarker(new MarkerOptions().position(voloh).title("Marker in Sydney"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(lastSelfLocation));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);
                }else {
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(lastSelfLocation));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
        if(myPosition.created){
            myPosition.deletePosition();
        }
    }

    // установка слушателя для получения координат из gps или спутника
    private void setLocationListener(){
        try{
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        1000 * 3, 10, locationListener);
            } else {
                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER, 1000 * 3, 10,
                        locationListener);
            }
        }catch (SecurityException ex){
            Log.e(TAG, ex.getMessage());
        }
    }

    // результат запроса на получение прав
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                if (grantResults.length == 2) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                        // права получены, установка слушателя
                        setLocationListener();
                    }

                } else {
                    // права не получены
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            // получены координаты
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
        }

        @Override
        public void onProviderEnabled(String provider) {
            // gps включен
        }


        @Override
        public void onProviderDisabled(String provider) {
            // gps выключен
            // показать окно с информацией, что gps отключен
            enableGpsMessageDialogBox();
        }
    };

    @Override
    public void onMapReady(GoogleMap googleMap) {

        LatLng curLocation;
        mMap = googleMap;
        myPosition = new MyPosition(mMap);
        // если переданы координаты со стартового автивити, то установить камеру на координаты
        startLat = getIntent().getDoubleExtra ("START_LATITUDE",-1);
        startLon = getIntent().getDoubleExtra ("START_LONGITUDE",-1);
        // последние данные о местонахождении телефона

        if(startLat>=0 && startLon>=0){
            curLocation = new LatLng(startLat, startLon);
            lastSelfLocation = new LatLng(startLat, startLon);
            if(!myPosition.created){
                // нарисовать положение телефона на карте
                myPosition.createPosition(curLocation);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(curLocation));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);
            }else{
                myPosition.deletePosition();
                myPosition.createPosition(curLocation);
            }
        }

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
                }else if(routeNum=="3"){
                    coordinatesLat = route3CoordinatesLat;
                    coordinatesLon = route3CoordinatesLon;
                }else if(routeNum=="4"){
                    coordinatesLat = route4CoordinatesLat;
                    coordinatesLon = route4CoordinatesLon;
                }else if(routeNum=="5"){
                    coordinatesLat = route5CoordinatesLat;
                    coordinatesLon = route5CoordinatesLon;
                }else if(routeNum=="6"){
                    coordinatesLat = route6CoordinatesLat;
                    coordinatesLon = route6CoordinatesLon;
                }else if(routeNum=="61"){
                    coordinatesLat = route61CoordinatesLat;
                    coordinatesLon = route61CoordinatesLon;
                }else if(routeNum=="7"){
                    coordinatesLat = route7CoordinatesLat;
                    coordinatesLon = route7CoordinatesLon;
                }else if(routeNum=="8"){
                    coordinatesLat = route8CoordinatesLat;
                    coordinatesLon = route8CoordinatesLon;
                }else if(routeNum=="9"){
                    coordinatesLat = route9CoordinatesLat;
                    coordinatesLon = route9CoordinatesLon;
                }else if(routeNum=="10"){
                    coordinatesLat = route10CoordinatesLat;
                    coordinatesLon = route10CoordinatesLon;
                }else if(routeNum=="11"){
                    coordinatesLat = route11CoordinatesLat;
                    coordinatesLon = route11CoordinatesLon;
                }else if(routeNum=="13"){
                    coordinatesLat = route13CoordinatesLat;
                    coordinatesLon = route13CoordinatesLon;
                }else if(routeNum=="14"){
                    coordinatesLat = route14CoordinatesLat;
                    coordinatesLon = route14CoordinatesLon;
                }else if(routeNum=="15"){
                    coordinatesLat = route15CoordinatesLat;
                    coordinatesLon = route15CoordinatesLon;
                }else if(routeNum=="16"){
                    coordinatesLat = route16CoordinatesLat;
                    coordinatesLon = route16CoordinatesLon;
                }else if(routeNum=="17"){
                    coordinatesLat = route17CoordinatesLat;
                    coordinatesLon = route17CoordinatesLon;
                }/*else if(routeNum=="21"){
                    coordinatesLat = route21CoordinatesLat;
                    coordinatesLon = route21CoordinatesLon;
                }*/


                for(int h=0; h<coordinatesLat.size(); h++)
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

        route3CoordinatesLat = Arrays.asList(getResources().getStringArray(R.array.route3_lat));
        route3CoordinatesLon = Arrays.asList(getResources().getStringArray(R.array.route3_lon));

        route4CoordinatesLat = Arrays.asList(getResources().getStringArray(R.array.route4_lat));
        route4CoordinatesLon = Arrays.asList(getResources().getStringArray(R.array.route4_lon));

        route5CoordinatesLat = Arrays.asList(getResources().getStringArray(R.array.route5_lat));
        route5CoordinatesLon = Arrays.asList(getResources().getStringArray(R.array.route5_lon));

        route6CoordinatesLat = Arrays.asList(getResources().getStringArray(R.array.route6_lat));
        route6CoordinatesLon = Arrays.asList(getResources().getStringArray(R.array.route6_lon));

        route61CoordinatesLat = Arrays.asList(getResources().getStringArray(R.array.route61_lat));
        route61CoordinatesLon = Arrays.asList(getResources().getStringArray(R.array.route61_lon));

        route7CoordinatesLat = Arrays.asList(getResources().getStringArray(R.array.route7_lat));
        route7CoordinatesLon = Arrays.asList(getResources().getStringArray(R.array.route7_lon));

        route8CoordinatesLat = Arrays.asList(getResources().getStringArray(R.array.route8_lat));
        route8CoordinatesLon = Arrays.asList(getResources().getStringArray(R.array.route8_lon));

        route9CoordinatesLat = Arrays.asList(getResources().getStringArray(R.array.route9_lat));
        route9CoordinatesLon = Arrays.asList(getResources().getStringArray(R.array.route9_lon));

        route10CoordinatesLat = Arrays.asList(getResources().getStringArray(R.array.route10_lat));
        route10CoordinatesLon = Arrays.asList(getResources().getStringArray(R.array.route10_lon));

        route11CoordinatesLat = Arrays.asList(getResources().getStringArray(R.array.route11_lat));
        route11CoordinatesLon = Arrays.asList(getResources().getStringArray(R.array.route11_lon));

        route13CoordinatesLat = Arrays.asList(getResources().getStringArray(R.array.route13_lat));
        route13CoordinatesLon = Arrays.asList(getResources().getStringArray(R.array.route13_lon));

        route14CoordinatesLat = Arrays.asList(getResources().getStringArray(R.array.route14_lat));
        route14CoordinatesLon = Arrays.asList(getResources().getStringArray(R.array.route14_lon));

        route15CoordinatesLat = Arrays.asList(getResources().getStringArray(R.array.route15_lat));
        route15CoordinatesLon = Arrays.asList(getResources().getStringArray(R.array.route15_lon));

        route16CoordinatesLat = Arrays.asList(getResources().getStringArray(R.array.route16_lat));
        route16CoordinatesLon = Arrays.asList(getResources().getStringArray(R.array.route16_lon));

        route17CoordinatesLat = Arrays.asList(getResources().getStringArray(R.array.route17_lat));
        route17CoordinatesLon = Arrays.asList(getResources().getStringArray(R.array.route17_lon));

        /*route21CoordinatesLat = Arrays.asList(getResources().getStringArray(R.array.route21_lat));
        route21CoordinatesLon = Arrays.asList(getResources().getStringArray(R.array.route21_lon));*/

        int[] timeWay1ForwardStart = getResources().getIntArray(R.array.way1_forward_start);
        int[] timeWay1ForwardEnd = getResources().getIntArray(R.array.way1_forward_end);
        int[] timeWay1BackStart = getResources().getIntArray(R.array.way1_back_start);
        int[] timeWay1BackEnd = getResources().getIntArray(R.array.way1_back_end);

        int[] timeWay2ForwardStart = getResources().getIntArray(R.array.way2_forward_start);
        int[] timeWay2ForwardEnd = getResources().getIntArray(R.array.way2_forward_end);

        int[] timeWay3ForwardStart = getResources().getIntArray(R.array.way3_forward_start);
        int[] timeWay3ForwardEnd = getResources().getIntArray(R.array.way3_forward_end);

        int[] timeWay4ForwardStart = getResources().getIntArray(R.array.way4_forward_start);
        int[] timeWay4ForwardEnd = getResources().getIntArray(R.array.way4_forward_end);

        int[] timeWay5ForwardStart = getResources().getIntArray(R.array.way5_forward_start);
        int[] timeWay5ForwardEnd = getResources().getIntArray(R.array.way5_forward_end);
        int[] timeWay5BackStart = getResources().getIntArray(R.array.way5_back_start);
        int[] timeWay5BackEnd = getResources().getIntArray(R.array.way5_back_end);

        int[] timeWay6ForwardStart = getResources().getIntArray(R.array.way6_forward_start);
        int[] timeWay6ForwardEnd = getResources().getIntArray(R.array.way6_forward_end);
        int[] timeWay6BackStart = getResources().getIntArray(R.array.way6_back_start);
        int[] timeWay6BackEnd = getResources().getIntArray(R.array.way6_back_end);

        int[] timeWay61ForwardStart = getResources().getIntArray(R.array.way61_forward_start);
        int[] timeWay61ForwardEnd = getResources().getIntArray(R.array.way61_forward_end);
        int[] timeWay61BackStart = getResources().getIntArray(R.array.way61_back_start);
        int[] timeWay61BackEnd = getResources().getIntArray(R.array.way61_back_end);

        int[] timeWay7ForwardStart = getResources().getIntArray(R.array.way7_forward_start);
        int[] timeWay7ForwardEnd = getResources().getIntArray(R.array.way7_forward_end);

        int[] timeWay8ForwardStart = getResources().getIntArray(R.array.way8_forward_start);
        int[] timeWay8ForwardEnd = getResources().getIntArray(R.array.way8_forward_end);

        int[] timeWay9ForwardStart = getResources().getIntArray(R.array.way9_forward_start);
        int[] timeWay9ForwardEnd = getResources().getIntArray(R.array.way9_forward_end);

        int[] timeWay10ForwardStart = getResources().getIntArray(R.array.way10_forward_start);
        int[] timeWay10ForwardEnd = getResources().getIntArray(R.array.way10_forward_end);

        int[] timeWay11ForwardStart = getResources().getIntArray(R.array.way11_forward_start);
        int[] timeWay11ForwardEnd = getResources().getIntArray(R.array.way11_forward_end);
        int[] timeWay11BackStart = getResources().getIntArray(R.array.way11_back_start);
        int[] timeWay11BackEnd = getResources().getIntArray(R.array.way11_back_end);

        int[] timeWay13ForwardStart = getResources().getIntArray(R.array.way13_forward_start);
        int[] timeWay13ForwardEnd = getResources().getIntArray(R.array.way13_forward_end);
        int[] timeWay13BackStart = getResources().getIntArray(R.array.way13_back_start);
        int[] timeWay13BackEnd = getResources().getIntArray(R.array.way13_back_end);

        int[] timeWay14ForwardStart = getResources().getIntArray(R.array.way14_forward_start);
        int[] timeWay14ForwardEnd = getResources().getIntArray(R.array.way14_forward_end);
        int[] timeWay14BackStart = getResources().getIntArray(R.array.way14_back_start);
        int[] timeWay14BackEnd = getResources().getIntArray(R.array.way14_back_end);

        int[] timeWay15ForwardStart = getResources().getIntArray(R.array.way15_forward_start);
        int[] timeWay15ForwardEnd = getResources().getIntArray(R.array.way15_forward_end);
        int[] timeWay15BackStart = getResources().getIntArray(R.array.way15_back_start);
        int[] timeWay15BackEnd = getResources().getIntArray(R.array.way15_back_end);

        int[] timeWay16ForwardStart = getResources().getIntArray(R.array.way16_forward_start);
        int[] timeWay16ForwardEnd = getResources().getIntArray(R.array.way16_forward_end);
        int[] timeWay16BackStart = getResources().getIntArray(R.array.way16_back_start);
        int[] timeWay16BackEnd = getResources().getIntArray(R.array.way16_back_end);

        int[] timeWay17ForwardStart = getResources().getIntArray(R.array.way17_forward_start);
        int[] timeWay17ForwardEnd = getResources().getIntArray(R.array.way17_forward_end);

        /*int[] timeWay21ForwardStart = getResources().getIntArray(R.array.way21_forward_start);
        int[] timeWay21ForwardEnd = getResources().getIntArray(R.array.way21_forward_end);
        int[] timeWay21BackStart = getResources().getIntArray(R.array.way21_back_start);
        int[] timeWay21BackEnd = getResources().getIntArray(R.array.way21_back_end);*/

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

        // МАРШРУТ 3
        for (int t = 0; t < timeWay3ForwardStart.length; t++) {
            //Log.d(TAG, "Отправление в " + timeWayForwardStart[t] + " прибытие в " + timeWayForwardEnd[t]);
            Route bus = new Route("По маршруту №3", timeWay3ForwardStart[t], timeWay3ForwardEnd[t], 15300, Directions.FORWARD_MOVEMENT, this);
            bus.setGeoCoordinatesAlongAnEntireRoute(route3CoordinatesLat, route3CoordinatesLon, "3");
            bus.setMap(mMap);
            routes.add(bus);
        }

        // МАРШРУТ 4
        for (int t = 0; t < timeWay4ForwardStart.length; t++) {
            //Log.d(TAG, "Отправление в " + timeWayForwardStart[t] + " прибытие в " + timeWayForwardEnd[t]);
            Route bus = new Route("По маршруту №4", timeWay4ForwardStart[t], timeWay4ForwardEnd[t], 4400, Directions.FORWARD_MOVEMENT, this);
            bus.setGeoCoordinatesAlongAnEntireRoute(route4CoordinatesLat, route4CoordinatesLon, "4");
            bus.setMap(mMap);
            routes.add(bus);
        }

        // МАРШРУТ 5
        for (int t = 0; t < timeWay5ForwardStart.length; t++) {
            //Log.d(TAG, "Отправление в " + timeWayForwardStart[t] + " прибытие в " + timeWayForwardEnd[t]);
            Route bus = new Route("По маршруту №5.", timeWay5ForwardStart[t], timeWay5ForwardEnd[t], 9100, Directions.FORWARD_MOVEMENT, this);
            bus.setGeoCoordinatesAlongAnEntireRoute(route5CoordinatesLat, route5CoordinatesLon, "5");
            bus.setMap(mMap);
            routes.add(bus);
        }
        for (int t = 0; t < timeWay5BackStart.length; t++) {
            Route bus = new Route("По маршруту №5", timeWay5BackStart[t], timeWay5BackEnd[t], 9100, Directions.BACK_MOVEMENT, this);
            bus.setGeoCoordinatesAlongAnEntireRoute(route5CoordinatesLat, route5CoordinatesLon, "5");
            bus.setMap(mMap);
            routes.add(bus);
        }

        // МАРШРУТ 6
        for (int t = 0; t < timeWay6ForwardStart.length; t++) {
            //Log.d(TAG, "Отправление в " + timeWayForwardStart[t] + " прибытие в " + timeWayForwardEnd[t]);

            Route bus = new Route("По маршруту №6.", timeWay6ForwardStart[t], timeWay6ForwardEnd[t], 10800, Directions.FORWARD_MOVEMENT, this);
            bus.setGeoCoordinatesAlongAnEntireRoute(route6CoordinatesLat, route6CoordinatesLon, "6");
            bus.setMap(mMap);
            routes.add(bus);
        }
        for (int t = 0; t < timeWay6BackStart.length; t++) {
            Route bus = new Route("По маршруту №6", timeWay6BackStart[t], timeWay6BackEnd[t], 10800, Directions.BACK_MOVEMENT, this);
            bus.setGeoCoordinatesAlongAnEntireRoute(route6CoordinatesLat, route6CoordinatesLon, "6");
            bus.setMap(mMap);
            routes.add(bus);
        }

        // МАРШРУТ 6/1
        for (int t = 0; t < timeWay61ForwardStart.length; t++) {
            //Log.d(TAG, "Отправление в " + timeWayForwardStart[t] + " прибытие в " + timeWayForwardEnd[t]);

            Route bus = new Route("По маршруту №6/1.", timeWay61ForwardStart[t], timeWay61ForwardEnd[t], 11800, Directions.FORWARD_MOVEMENT, this);
            bus.setGeoCoordinatesAlongAnEntireRoute(route61CoordinatesLat, route61CoordinatesLon, "61");
            bus.setMap(mMap);
            routes.add(bus);
        }
        for (int t = 0; t < timeWay61BackStart.length; t++) {
            Route bus = new Route("По маршруту №6/1", timeWay61BackStart[t], timeWay61BackEnd[t], 11800, Directions.BACK_MOVEMENT, this);
            bus.setGeoCoordinatesAlongAnEntireRoute(route61CoordinatesLat, route61CoordinatesLon, "61");
            bus.setMap(mMap);
            routes.add(bus);
        }

        // МАРШРУТ 7
        for (int t = 0; t < timeWay7ForwardStart.length; t++) {
            //Log.d(TAG, "Отправление в " + timeWayForwardStart[t] + " прибытие в " + timeWayForwardEnd[t]);
            Route bus = new Route("По маршруту №7", timeWay7ForwardStart[t], timeWay7ForwardEnd[t], 9500, Directions.FORWARD_MOVEMENT, this);
            bus.setGeoCoordinatesAlongAnEntireRoute(route7CoordinatesLat, route7CoordinatesLon, "7");
            bus.setMap(mMap);
            routes.add(bus);
        }

        // МАРШРУТ 8
        for (int t = 0; t < timeWay8ForwardStart.length; t++) {
            //Log.d(TAG, "Отправление в " + timeWayForwardStart[t] + " прибытие в " + timeWayForwardEnd[t]);
            Route bus = new Route("По маршруту №8", timeWay8ForwardStart[t], timeWay8ForwardEnd[t], 9000, Directions.FORWARD_MOVEMENT, this);
            bus.setGeoCoordinatesAlongAnEntireRoute(route8CoordinatesLat, route8CoordinatesLon, "8");
            bus.setMap(mMap);
            routes.add(bus);
        }

        // МАРШРУТ 9
        for (int t = 0; t < timeWay9ForwardStart.length; t++) {
            //Log.d(TAG, "Отправление в " + timeWayForwardStart[t] + " прибытие в " + timeWayForwardEnd[t]);
            Route bus = new Route("По маршруту №9", timeWay9ForwardStart[t], timeWay9ForwardEnd[t], 5300, Directions.FORWARD_MOVEMENT, this);
            bus.setGeoCoordinatesAlongAnEntireRoute(route9CoordinatesLat, route9CoordinatesLon, "9");
            bus.setMap(mMap);
            routes.add(bus);
        }

        // МАРШРУТ 10
        for (int t = 0; t < timeWay10ForwardStart.length; t++) {
            //Log.d(TAG, "Отправление в " + timeWayForwardStart[t] + " прибытие в " + timeWayForwardEnd[t]);
            Route bus = new Route("По маршруту №10", timeWay10ForwardStart[t], timeWay10ForwardEnd[t], 5300, Directions.FORWARD_MOVEMENT, this);
            bus.setGeoCoordinatesAlongAnEntireRoute(route10CoordinatesLat, route10CoordinatesLon, "10");
            bus.setMap(mMap);
            routes.add(bus);
        }

        // МАРШРУТ 11
        for (int t = 0; t < timeWay11ForwardStart.length; t++) {
            //Log.d(TAG, "Отправление в " + timeWayForwardStart[t] + " прибытие в " + timeWayForwardEnd[t]);

            Route bus = new Route("По маршруту №11.", timeWay11ForwardStart[t], timeWay11ForwardEnd[t], 11000, Directions.FORWARD_MOVEMENT, this);
            bus.setGeoCoordinatesAlongAnEntireRoute(route11CoordinatesLat, route11CoordinatesLon, "11");
            bus.setMap(mMap);
            routes.add(bus);
        }
        for (int t = 0; t < timeWay11BackStart.length; t++) {
            Route bus = new Route("По маршруту №11", timeWay11BackStart[t], timeWay11BackEnd[t], 11000, Directions.BACK_MOVEMENT, this);
            bus.setGeoCoordinatesAlongAnEntireRoute(route11CoordinatesLat, route11CoordinatesLon, "11");
            bus.setMap(mMap);
            routes.add(bus);
        }

        // МАРШРУТ 13
        for (int t = 0; t < timeWay13ForwardStart.length; t++) {
            //Log.d(TAG, "Отправление в " + timeWayForwardStart[t] + " прибытие в " + timeWayForwardEnd[t]);

            Route bus = new Route("По маршруту №13.", timeWay13ForwardStart[t], timeWay13ForwardEnd[t], 8500, Directions.FORWARD_MOVEMENT, this);
            bus.setGeoCoordinatesAlongAnEntireRoute(route13CoordinatesLat, route13CoordinatesLon, "13");
            bus.setMap(mMap);
            routes.add(bus);
        }
        for (int t = 0; t < timeWay13BackStart.length; t++) {
            Route bus = new Route("По маршруту №13", timeWay13BackStart[t], timeWay13BackEnd[t], 8500, Directions.BACK_MOVEMENT, this);
            bus.setGeoCoordinatesAlongAnEntireRoute(route13CoordinatesLat, route13CoordinatesLon, "13");
            bus.setMap(mMap);
            routes.add(bus);
        }

        // МАРШРУТ 14
        for (int t = 0; t < timeWay14ForwardStart.length; t++) {
            //Log.d(TAG, "Отправление в " + timeWayForwardStart[t] + " прибытие в " + timeWayForwardEnd[t]);

            Route bus = new Route("По маршруту №14.", timeWay14ForwardStart[t], timeWay14ForwardEnd[t], 11200, Directions.FORWARD_MOVEMENT, this);
            bus.setGeoCoordinatesAlongAnEntireRoute(route14CoordinatesLat, route14CoordinatesLon, "14");
            bus.setMap(mMap);
            routes.add(bus);
        }
        for (int t = 0; t < timeWay14BackStart.length; t++) {
            Route bus = new Route("По маршруту №14", timeWay14BackStart[t], timeWay14BackEnd[t], 11200, Directions.BACK_MOVEMENT, this);
            bus.setGeoCoordinatesAlongAnEntireRoute(route14CoordinatesLat, route14CoordinatesLon, "14");
            bus.setMap(mMap);
            routes.add(bus);
        }

        // МАРШРУТ 15
        for (int t = 0; t < timeWay15ForwardStart.length; t++) {
            //Log.d(TAG, "Отправление в " + timeWayForwardStart[t] + " прибытие в " + timeWayForwardEnd[t]);

            Route bus = new Route("По маршруту №15.", timeWay15ForwardStart[t], timeWay15ForwardEnd[t], 7500, Directions.FORWARD_MOVEMENT, this);
            bus.setGeoCoordinatesAlongAnEntireRoute(route15CoordinatesLat, route15CoordinatesLon, "15");
            bus.setMap(mMap);
            routes.add(bus);
        }
        for (int t = 0; t < timeWay15BackStart.length; t++) {
            Route bus = new Route("По маршруту №15", timeWay15BackStart[t], timeWay15BackEnd[t], 7500, Directions.BACK_MOVEMENT, this);
            bus.setGeoCoordinatesAlongAnEntireRoute(route15CoordinatesLat, route15CoordinatesLon, "15");
            bus.setMap(mMap);
            routes.add(bus);
        }

        // МАРШРУТ 16
        for (int t = 0; t < timeWay16ForwardStart.length; t++) {
            //Log.d(TAG, "Отправление в " + timeWayForwardStart[t] + " прибытие в " + timeWayForwardEnd[t]);

            Route bus = new Route("По маршруту №16.", timeWay16ForwardStart[t], timeWay16ForwardEnd[t], 9600, Directions.FORWARD_MOVEMENT, this);
            bus.setGeoCoordinatesAlongAnEntireRoute(route16CoordinatesLat, route16CoordinatesLon, "16");
            bus.setMap(mMap);
            routes.add(bus);
        }
        for (int t = 0; t < timeWay16BackStart.length; t++) {
            Route bus = new Route("По маршруту №16", timeWay16BackStart[t], timeWay16BackEnd[t], 9600, Directions.BACK_MOVEMENT, this);
            bus.setGeoCoordinatesAlongAnEntireRoute(route16CoordinatesLat, route16CoordinatesLon, "16");
            bus.setMap(mMap);
            routes.add(bus);
        }

        // МАРШРУТ 17
        for (int t = 0; t < timeWay17ForwardStart.length; t++) {
            //Log.d(TAG, "Отправление в " + timeWayForwardStart[t] + " прибытие в " + timeWayForwardEnd[t]);
            Route bus = new Route("По маршруту №17", timeWay17ForwardStart[t], timeWay17ForwardEnd[t], 6200, Directions.FORWARD_MOVEMENT, this);
            bus.setGeoCoordinatesAlongAnEntireRoute(route17CoordinatesLat, route17CoordinatesLon, "17");
            bus.setMap(mMap);
            routes.add(bus);
        }

    }


    // предложение включить gps
    private AlertDialog enableGpsMessageDialogBox() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.enable_gps_message))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.enable_gps_positive_button), new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton(getResources().getString(R.string.enable_gps_negative_button), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {

                        // закрыть приложение, если пользователь не включил gps
                        if (Build.VERSION.SDK_INT >= 16&&Build.VERSION.SDK_INT<21) {
                            finishAffinity();
                            System.exit(0);
                        }
                        if (Build.VERSION.SDK_INT >= 21) {
                            finishAndRemoveTask ();
                        }


                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
        return alert;
    }

    // запрос разрешения
    private void requestLocationPermissions(){
        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_LOCATION_PERMISSION);
    }
    // объяснение для пользователя
    private AlertDialog explanationAndPermissionRequest(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.explanation_message))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.explanation_positive_button), new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        // запрос разрешения
                        requestLocationPermissions();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.explanation_negative_button), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        //dialog.cancel();
                        // закрыть приложение, если пользователь не включил gps
                        if (Build.VERSION.SDK_INT >= 16&&Build.VERSION.SDK_INT<21) {
                            finishAffinity();
                            System.exit(0);
                        }
                        if (Build.VERSION.SDK_INT >= 21) {
                            finishAndRemoveTask ();
                        }
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
        return alert;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Операции для выбранного пункта меню
        switch (item.getItemId())
        {
            case R.id.bus_schedule:
                Intent myIntent2 = new Intent(this, BusSheduleActivity.class);
                startActivity(myIntent2);
                return(true);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
