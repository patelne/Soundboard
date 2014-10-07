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

    public View getView(final int pos, View convertView, ViewGroup parent) {
        View v = convertView;

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View rowView = inflater.inflate(R.layout.row_view, parent, false);
        final TextView tv = (TextView) rowView.findViewById(R.id.name);

        /*------------------------------------------
        Use the file name as the title text but
        take off the extension
        ------------------------------------------*/
        String name = files.get(pos).getName();
        name = name.substring(0, name.lastIndexOf("."));
        tv.setText(name);

        return rowView;
    }
}