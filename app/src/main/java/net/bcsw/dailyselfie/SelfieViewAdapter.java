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
public class SelfieViewAdapter extends BaseAdapter
{
    private static final String TAG = "SelfieViewAdapter";
    private              ArrayList<SelfieRecord> list     = new ArrayList<SelfieRecord>();
    private static       LayoutInflater          inflater = null;
    private Context context;

    public SelfieViewAdapter(Context context)
    {
        Log.i(TAG, "ctor: entered");
        this.context = context;
        inflater = LayoutInflater.from(context);
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
            // Create an object with the data we need to track and save it to the view tag field

            holder = new ViewHolder();

            holder.thumbnail = (ImageView) newView.findViewById(R.id.thumbnail);
            holder.filename = (TextView) newView.findViewById(R.id.filename);
            holder.imageDate = (TextView) newView.findViewById(R.id.imageDate);

            newView = inflater.inflate(R.layout.selfie_image_list_view, parent, false);
            newView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) newView.getTag();
        }
        holder.thumbnail.setImageBitmap(curr.getThumbnail());
        holder.filename.setText(curr.getImageFileName());
        holder.imageDate.setText("Date: " + curr.getDateTakenString());

        return newView;
    }

    static class ViewHolder
    {
        ImageView thumbnail;
        TextView  filename;
        TextView  imageDate;
    }

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
