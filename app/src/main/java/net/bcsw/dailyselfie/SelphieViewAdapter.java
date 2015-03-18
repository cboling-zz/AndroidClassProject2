package net.bcsw.dailyselfie;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by cboling on 3/16/2015.
 */
public class SelphieViewAdapter extends BaseAdapter
{
    private static final String                  TAG      = "SelphieViewAdapter";
    private              ArrayList<SelfieRecord> list     = new ArrayList<SelfieRecord>();
    private static       LayoutInflater          inflater = null;
    private Context mContext;

    public SelphieViewAdapter(Context context)
    {
        Log.i(TAG, "ctor: entered");
        mContext = context;
        inflater = LayoutInflater.from(mContext);
    }

    public int getCount()
    {
        return list.size();
    }

    public Object getItem(int position)
    {
        Log.i(TAG, "getItem: entered, position: " + position);
        return list.get(position);
    }

    public long getItemId(int position)
    {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        Log.i(TAG, "getView: entered, position: " + position);
        View newView = convertView;
        ViewHolder holder;

        SelfieRecord curr = list.get(position);

        if (null == convertView)
        {
            holder = new ViewHolder();
            //            newView = inflater
            //                    .inflate(R.layout.place_badge_view, parent, false);
            //            holder.flag = (ImageView) newView.findViewById(R.id.flag);
            //            holder.country = (TextView) newView.findViewById(R.id.country_name);
            //            holder.place = (TextView) newView.findViewById(R.id.place_name);
            newView.setTag(holder);

        }
        else
        {
            holder = (ViewHolder) newView.getTag();
        }

        //        holder.flag.setImageBitmap(curr.getFlagBitmap());
        //        holder.country.setText("Country: " + curr.getCountryName());
        //        holder.place.setText("Place: " + curr.getPlace());

        return newView;
    }

    static class ViewHolder
    {
        ImageView flag;
        TextView  country;
        TextView  place;
    }

    //    public boolean intersects(Location location) {
    //        for (SelfieRecord item : list) {
    //            if (item.intersects(location)) {
    //                return true;
    //            }
    //        }
    //        return false;
    //    }

    public void add(SelfieRecord item)
    {
        Log.i(TAG, "add: entered" + item.toString());
        list.add(item);
        notifyDataSetChanged();
    }

    public ArrayList<SelfieRecord> getList()
    {
        return list;
    }

    public void removeAllViews()
    {
        Log.i(TAG, "removeAllViews: entered");
        list.clear();
        this.notifyDataSetChanged();
    }
}
