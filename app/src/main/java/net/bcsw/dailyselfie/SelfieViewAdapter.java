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
import java.util.List;

/**
 * Created by cboling on 3/16/2015.
 */
public class SelfieViewAdapter extends BaseAdapter
{
    private static final String             TAG          = "SelfieViewAdapter";
    private static final int                IMAGE_HEIGHT = 64 * 4;
    private static final int                IMAGE_WIDTH  = 64 * 4;
    private static       LayoutInflater     inflater     = null;
    private              List<SelfieRecord> list         = new ArrayList<SelfieRecord>();
    private Context                context;
    private SelfieRecordDataSource dataSource;

    public SelfieViewAdapter(Context context, SelfieRecordDataSource dataSource)
    {
        Log.d(TAG, "ctor: entered");
        this.context = context;
        this.dataSource = dataSource;

        inflater = LayoutInflater.from(context);

        // Restore the list of records from the database (if any)

        list = dataSource.getList();
    }

    public int getCount()
    {
        return list.size();
    }

    public Object getItem(int position)
    {
        return list.get(position);
    }

    public long getItemId(int position)
    {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        View newView = convertView;
        ViewHolder holder;

        if (newView == null)
        {
            newView = inflater.inflate(R.layout.selfie_image_list_view, parent, false);

            // Create an object with the data we need to track and save it to the view tag field

            holder = new ViewHolder((ImageView) newView.findViewById(R.id.thumbnail),
                                    (TextView) newView.findViewById(R.id.filename),
                                    (TextView) newView.findViewById(R.id.imageDate));
            newView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) newView.getTag();
        }
        SelfieRecord curr = list.get(position);

        if (curr != null)
        {
            holder.imageDate.setText("Date: " + curr.getDateTakenString());
            holder.filename.setText("File: " + curr.getImageFileName());
            holder.thumbnail.setImageBitmap(curr.getThumbnail(context, IMAGE_HEIGHT, IMAGE_WIDTH));
        }
        return newView;
    }

    static class ViewHolder
    {
        ImageView thumbnail;
        TextView  filename;
        TextView  imageDate;

        public ViewHolder(ImageView iv, TextView fv, TextView dv)
        {
            thumbnail = iv;
            filename = fv;
            imageDate = dv;
        }
    }

    public List<SelfieRecord> getList()
    {
        return list;
    }

    public void add(SelfieRecord item)
    {
        Log.d(TAG, "add: entered" + item.toString());
        list.add(item);

        // Also add it to the database and make sure we use its index

        long index = dataSource.add(item);
        item.setId(index);

        notifyDataSetChanged();
    }

    public boolean remove(int position)
    {
        Log.d(TAG, "remove by position: entered, position : " + position);

        SelfieRecord oldRecord = list.remove(position);

        if (oldRecord != null)
        {
            dataSource.remove(oldRecord);
            notifyDataSetChanged();
            return true;
        }
        return false;
    }

    public boolean remove(SelfieRecord item)
    {
        Log.d(TAG, "remove by item: entered" + item.toString());

        if (list.remove(item))
        {
            dataSource.remove(item);
            notifyDataSetChanged();
            return true;
        }
        return false;
    }

    public void clear()
    {
        Log.i(TAG, "clear: entered");

        for (SelfieRecord item : list)
        {
            dataSource.remove(item);
        }
        list.clear();
        this.notifyDataSetChanged();
    }
}
