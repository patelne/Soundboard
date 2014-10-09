package com.neilspatel.soundboard.soundboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Neil on 9/27/2014.
 */
public class SoundBoardArrayAdapter extends ArrayAdapter<File> {
    private static final String TAG = "SoundBoardArrayAdapter";
    private ArrayList<File> files;

    public SoundBoardArrayAdapter(Context context, int textViewResourceId, ArrayList<File> objects) {
        super(context, textViewResourceId, objects);
        this.files = objects;
    }

    private class ViewHolder {
        TextView tv;
    }

    public View getView(final int pos, View convertView, ViewGroup parent) {

        if(null == convertView) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_view, parent, false);

            ViewHolder holder = new ViewHolder();
            holder.tv = (TextView) convertView.findViewById(R.id.name);

            convertView.setTag(holder);
        }

        ViewHolder v = (ViewHolder)convertView.getTag();

        /*------------------------------------------
        Use the file name as the title text but
        take off the extension
        ------------------------------------------*/
        String name = files.get(pos).getName();
        name = name.substring(0, name.lastIndexOf("."));
        v.tv.setText(name);

        return convertView;
    }
}