package ua.com.anyapps.avtobusizjum;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class SplashScreenActivity extends AppCompatActivity {

    private static final String TAG = "debapp";
    private LocationManager locationManager;
    private ConstraintLayout layLoadingString;
    private ConstraintLayout layLoader;


    public static final int REQUEST_LOCATION_PERMISSION = 0;
    @Override
    protected void onResume() {
        super.onResume();

        // проверка прав при каждом показе активити
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            // есть разрешения
            setLocationListener();
        }else{
            // разрешения отсутствуют. запрос
            // До android 6 и выше
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

        // если включен gps, показать загрузчик
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            layLoadingString.setVisibility(View.VISIBLE);
            layLoader.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            locationManager.removeUpdates(locationListener);
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        layLoadingString = findViewById(R.id.layLoadingString);
        layLoader = findViewById(R.id.layLoader);

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
            /*Toast toast = Toast.makeText(getApplicationContext(),
                    "Координаты получены. Смена активити. " + Calendar.getInstance().getTime(), Toast.LENGTH_SHORT);
            toast.show();*/

            // получены координаты - переход на карту
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("START_LATITUDE", location.getLatitude());
            intent.putExtra("START_LONGITUDE", location.getLongitude());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


            locationManager.removeUpdates(locationListener);

            startActivity(intent);
            finish();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
            // gps включен - показать загрузчик
            Log.d(TAG, "GPS enabled");
            layLoadingString.setVisibility(View.VISIBLE);
            layLoader.setVisibility(View.VISIBLE);
        }


        @Override
        public void onProviderDisabled(String provider) {
            // gps выключен - скрыть загрузчик
            layLoadingString.setVisibility(View.GONE);
            layLoader.setVisibility(View.GONE);
            // показать окно с информацией, что gps отключен
            enableGpsMessageDialogBox();
        }
    };

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
}
