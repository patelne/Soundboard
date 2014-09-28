package com.neilspatel.soundboard.soundboard;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
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
        TextView textView = (TextView) rowView.findViewById(R.id.title);
        ImageButton buttonView = (ImageButton) rowView.findViewById(R.id.deleteButton);

        /*------------------------------------------
        Use the file name as the title text but
        take off the extension
        ------------------------------------------*/
        String name = files.get(pos).getName();
        name = name.substring(0, name.lastIndexOf("."));
        textView.setText(name);

        /*------------------------------------------
        Set up the delete button listener.
        Removes this view from the list and deletes the file
        ------------------------------------------*/
        buttonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, String.valueOf(pos) + "'s button pressed");
                //TODO: Confirmation or sliding to reveal Delete

                if(!files.get(pos).delete()) {
                    Log.i(TAG, "File delete failed");
                }
                remove(files.get(pos));
            }
        });

        return rowView;
    }
}