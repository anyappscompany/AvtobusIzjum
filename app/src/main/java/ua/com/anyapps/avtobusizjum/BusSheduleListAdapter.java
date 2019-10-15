package ua.com.anyapps.avtobusizjum;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class BusSheduleListAdapter extends BaseAdapter {
    Context context;
    LayoutInflater lInflater;
    ArrayList<BusRoute> objects;
    private final String TAG = "debapp";

    public BusSheduleListAdapter(Context _context, ArrayList<BusRoute> routes){
        context = _context;
        objects = routes;
        lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.bus_shedule_list_item, parent, false);
        }

        BusRoute bR = (BusRoute)getItem(position);

        TextView tvSheduleListItemTitle = view.findViewById(R.id.tvSheduleListItemTitle);
        ImageView evSheduleListItemIcon = view.findViewById(R.id.evSheduleListItemIcon);
        Drawable shedulelistitemicon = context.getResources().getDrawable(R.drawable.shedulelistitemicon);
        evSheduleListItemIcon.setImageDrawable(shedulelistitemicon);

        tvSheduleListItemTitle.setText(bR.title);
        //tvSheduleListItemTitle.setTag(bR.routeNum);
        view.setTag(bR.routeNum);

        return view;
    }
}
