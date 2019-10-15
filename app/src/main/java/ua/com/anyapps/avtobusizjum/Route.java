package ua.com.anyapps.avtobusizjum;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.Arrays;
import java.util.List;

public class Route {
    private final String TAG = "debapp";

    public String busNumber;
    public long busDeparture;
    public long busArrival;
    public int distance;
    private float averageSpeed;
    private long travelTime;
    private List<String> routeCoordinatesLat;
    private List<String> routeCoordinatesLon;
    private GoogleMap mMap;
    private LatLng startCoordinate;
    private LatLng endCoordinate;
    private Marker marker;
    public  int direction;
    private boolean busExists = false;
    private int busStatus=0;
    private Context context;
    private List<String> busStatuses;
    private PolylineOptions busPathOptions;
    private Polyline busPath = null;
    private String routeNum;


    public Route(String _busNumber, long _busDeparture, long _busArrival, int _distance, int _direction, Context _context){
        busNumber = _busNumber;
        busDeparture = _busDeparture;
        busArrival = _busArrival;
        distance = _distance;
        direction = _direction;
        context = _context;

        // время в пути
        travelTime = busArrival - busDeparture;

        averageSpeed = (float)distance/(float)travelTime;
        busStatuses = Arrays.asList(context.getResources().getStringArray(R.array.bus_statuses));
    }

    // средняя скорость. метров в миллисекунду
    public float getAverageSpeed(){
        return averageSpeed;
    }

    public String getRouteNum(){
        return routeNum;
    }

    // инициализация масиива с координатами точек на пути
    public void setGeoCoordinatesAlongAnEntireRoute(List<String> _routeCoordinatesLat, List<String> _routeCoordinatesLon, String _routeNum){
        routeCoordinatesLat = _routeCoordinatesLat;
        routeCoordinatesLon = _routeCoordinatesLon;
        routeNum = _routeNum;

//Log.d(TAG, "routeNum" + routeNum);

        // установка начальных и конечных координат пути
        if(direction == Directions.FORWARD_MOVEMENT){
            endCoordinate = new LatLng(Float.parseFloat(routeCoordinatesLat.get(0)), Float.parseFloat(routeCoordinatesLon.get(0)));
            startCoordinate = new LatLng(Float.parseFloat(routeCoordinatesLat.get(routeCoordinatesLat.size()-1)), Float.parseFloat(routeCoordinatesLon.get(routeCoordinatesLon.size()-1)));
        }else{
            startCoordinate = new LatLng(Float.parseFloat(_routeCoordinatesLat.get(0)), Float.parseFloat(_routeCoordinatesLon.get(0)));
            endCoordinate = new LatLng(Float.parseFloat(routeCoordinatesLat.get(routeCoordinatesLat.size()-1)), Float.parseFloat(routeCoordinatesLon.get(routeCoordinatesLon.size()-1)));
        }


        //Log.d(TAG, "Количество точек в пути " + routeNum + ": " + busPathOptions.getPoints().size());
    }

    // установка карты
    public void setMap(GoogleMap _map){
        mMap = _map;
    }

    public void createBus(String _routeNum){
        busExists = true;



        if(this.direction == Directions.FORWARD_MOVEMENT) {
            marker = mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.forwardbus))
                    .title(busNumber)
                    .snippet(busStatuses.get(busStatus))
                    .position(startCoordinate)
                    .flat(true));

        }else{
            marker = mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.backbus))
                    .title(busNumber)
                    .snippet(busStatuses.get(busStatus))
                    .position(startCoordinate)
                    .flat(true));
        }
        marker.setTag(_routeNum);


        //перенести в другое место
        // линия пути
        /*mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker m) {
                Log.d(TAG, "Рисуется путь с точками " + busPathOptions.getPoints().size() + " " + routeCoordinatesLat.size() + " номер пути " + routeNum + " " + m.getTag());
                if(m.getTitle().contains("1")){
                    //Log.d(TAG, "11111111");
                }
                if(m.getTitle().contains("2")){
                    //Log.d(TAG, "222222");
                }
                if (marker.equals(marker))
                {
                    if(busPath==null) {
                        busPath = mMap.addPolyline(busPathOptions);
                    }else{
                        busPath.remove();
                        busPath = null;
                        busPath = mMap.addPolyline(busPathOptions);
                    }
                    //Toast.makeText(context, busNumber + "\n" + busStatuses.get(busStatus), Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });*/

        // клик в любом месте стирает линию пути



    }


    public boolean busExists(){
        return busExists;
    }

    // всего точек на пути
    public int getTotalCoordinatsInRoute(){
        return routeCoordinatesLat.size();
    }

    public void markerMove(int pointIndex, long currentTime){
        // удалить автобус по приезду на конечную
        if(pointIndex>=(getTotalCoordinatsInRoute()-1) && direction == Directions.FORWARD_MOVEMENT) {
            marker.remove();
            if(busPath!=null) {
                busPath.remove();
                busPath = null;
            }
            busStatus = BusStatuses.BUS_NOT_CREATED;
            return;
        }
//удалять автобус если конец пути
        if(pointIndex<=0 && direction == Directions.BACK_MOVEMENT) {
            marker.remove();
            if(busPath!=null) {
                busPath.remove();
                busPath = null;
            }
            busStatus = BusStatuses.BUS_NOT_CREATED;
            return;
        }

        // статус автобуса
        marker.setSnippet(busStatuses.get(busStatus));
        marker.setPosition(new LatLng(Float.parseFloat(routeCoordinatesLat.get(pointIndex)), Float.parseFloat(routeCoordinatesLon.get(pointIndex))));


    }

    public void setBusStatus(int _busStatus){
        busStatus = _busStatus;
    }

    public int getDistance(){
        return distance;
    }
}
