package ua.com.anyapps.avtobusizjum;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class BusSheduleActivity extends AppCompatActivity {

    ArrayList<BusRoute> routes = new ArrayList<>();
    BusSheduleListAdapter adBusSheduleListAdapter;
    private final String TAG = "debapp";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_shedule);

        ListView lvRoutes = (ListView) findViewById(R.id.lvRoutes);

        // заполнение списка маршрутов
        String[] bus_shedules_array = getResources().getStringArray(R.array.bus_shedules);
        int[] bus_routes_nums_array = getResources().getIntArray(R.array.bus_routes_nums);
        for(int i=0; i<bus_shedules_array.length; i++){
            BusRoute route = new BusRoute();
            route.title = bus_shedules_array[i];
            route.routeNum = bus_routes_nums_array[i];
            routes.add(route);
        }



        adBusSheduleListAdapter = new BusSheduleListAdapter(this, routes);
        lvRoutes.setAdapter(adBusSheduleListAdapter);

        lvRoutes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position,
                                    long id) {

                //Toast.makeText(getApplicationContext(), ((View) itemClicked).getTag() + "",
                        //Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(BusSheduleActivity.this, BusTimeTableActivity.class);
                Bundle b = new Bundle();
                //Log.d(TAG, "to send " +((View) itemClicked).getTag() + "");
                b.putString("table", ((View) itemClicked).getTag() + "");
                intent.putExtras(b);
                startActivity(intent);
                finish();
            }
        });
    }


}


