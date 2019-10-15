package ua.com.anyapps.avtobusizjum;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

public class BusTimeTableActivity extends AppCompatActivity {
    private final String TAG = "debapp";

    WebView wvBusTimeTable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_time_table);

        wvBusTimeTable = (WebView)findViewById(R.id.wvBusTimeTable);

        Bundle b = getIntent().getExtras();
        //Log.d(TAG, ">>"+b.getString("table"));
        int table = -1; // or other values
        if(b != null)
            table = Integer.parseInt(b.getString("table"));
        //Log.d(TAG, table + "");
        String routeTimeTableHtml = "";
        switch(table){
            case 1:
                routeTimeTableHtml = getResources().getString(R.string.route1_time_table_html);
                break;
            case 2:
                routeTimeTableHtml = getResources().getString(R.string.route2_time_table_html);
                break;
            case 3:
                routeTimeTableHtml = getResources().getString(R.string.route3_time_table_html);
                break;
            case 4:
                routeTimeTableHtml = getResources().getString(R.string.route4_time_table_html);
                break;
            case 5:
                routeTimeTableHtml = getResources().getString(R.string.route5_time_table_html);
                break;
            case 6:
                routeTimeTableHtml = getResources().getString(R.string.route6_time_table_html);
                break;
            case 61:
                routeTimeTableHtml = getResources().getString(R.string.route61_time_table_html);
                break;
            case 7:
                routeTimeTableHtml = getResources().getString(R.string.route7_time_table_html);
                break;
            case 8:
                routeTimeTableHtml = getResources().getString(R.string.route8_time_table_html);
                break;
            case 9:
                routeTimeTableHtml = getResources().getString(R.string.route9_time_table_html);
                break;
            case 10:
                routeTimeTableHtml = getResources().getString(R.string.route10_time_table_html);
                break;
            case 11:
                routeTimeTableHtml = getResources().getString(R.string.route11_time_table_html);
                break;
            case 13:
                routeTimeTableHtml = getResources().getString(R.string.route13_time_table_html);
                break;
            case 14:
                routeTimeTableHtml = getResources().getString(R.string.route14_time_table_html);
                break;
            case 15:
                routeTimeTableHtml = getResources().getString(R.string.route15_time_table_html);
                break;
            case 16:
                routeTimeTableHtml = getResources().getString(R.string.route16_time_table_html);
                break;
            case 17:
                routeTimeTableHtml = getResources().getString(R.string.route17_time_table_html);
                break;
            default:
                break;
        }
        wvBusTimeTable.loadDataWithBaseURL("", routeTimeTableHtml, "text/html", "UTF-8", "");
    }
}
