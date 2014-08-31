package org.openpilot_nonag.androidgcs.grid;

import java.util.ArrayList;

import org.openpilot_nonag.androidgcs.R;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageAdapter extends BaseAdapter {
	
    private static final String TAG = ImageAdapter.class.getSimpleName();
	
    private ArrayList<Integer> listMenuItems;
    private ArrayList<Integer> listMenuIcons;
    private Activity activity;

	public ImageAdapter(Activity activity,ArrayList<Integer> listMenuItems, ArrayList<Integer> listMenuIcons) {
        super();
        this.listMenuItems = listMenuItems;
        this.listMenuIcons = listMenuIcons;
        this.activity = activity;
    }

    public int getCount() {
        return listMenuItems.size();
    }

    public Object getItem(int position) {
        return listMenuItems.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public static class ViewHolder
    {
        public ImageView imgViewIcon;
        public TextView txtViewTitle;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder view;
        LayoutInflater inflator = activity.getLayoutInflater();
 
        if(convertView==null)
        {
            view = new ViewHolder();
            convertView = inflator.inflate(R.layout.gridview_row, null);
 
            view.txtViewTitle = (TextView) convertView.findViewById(R.id.textView1);
            view.imgViewIcon = (ImageView) convertView.findViewById(R.id.imageView1);
 
            convertView.setTag(view);
        }
        else
        {
            view = (ViewHolder) convertView.getTag();
        }
 
        view.txtViewTitle.setText(listMenuItems.get(position));
        view.imgViewIcon.setImageResource(listMenuIcons.get(position));
 
        return convertView;
    }

}
