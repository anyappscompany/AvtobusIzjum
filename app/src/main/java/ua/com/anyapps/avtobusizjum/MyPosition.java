package ua.com.anyapps.avtobusizjum;

import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

// позиция телефона на карте
public class MyPosition{
    public boolean created = false;
    private GoogleMap mMap;
    private LatLng point;
    private Circle circle1;
    private Circle circle2;
    public MyPosition(GoogleMap _mMap){
        mMap = _mMap;
    }
    public void createPosition(LatLng _point){
        point = _point;
        if (mMap != null) {
            drawCircles(point);
        }
        created = true;
    }
    public void movePosition(LatLng _newPosition){
        deletePosition();
        drawCircles(_newPosition);
    }
    public void deletePosition(){
        if(circle1!=null && circle2!=null){
            created = false;
            circle1.remove();
            circle1 = null;
            circle2.remove();
            circle2 = null;
        }
    }

    private void drawCircles(LatLng _point){
        CircleOptions circleOptions = new CircleOptions()
                .center(_point)
                .radius(100)
                .fillColor(Color.parseColor("#1AFB2323"))
                .strokeColor(Color.parseColor("#1AFB2323"))
                .strokeWidth(1);
        circle1 = mMap.addCircle(circleOptions);

        circleOptions = new CircleOptions()
                .center(_point)
                .radius(30)
                .fillColor(Color.parseColor("#66FB2323"))
                .strokeColor(Color.parseColor("#66FB2323"))
                .strokeWidth(1);
        circle2 = mMap.addCircle(circleOptions);
    }

}