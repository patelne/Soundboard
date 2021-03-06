package com.neilspatel.soundboard.soundboard;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class SoundBoard extends Activity{
    private static final String TAG = "SoundBoardActivity";
    ListView mListView;
    ImageButton mRecordButton;
    MediaPlayer mediaPlayer;
    MediaRecorder mediaRecorder;
    File mDirectory;
    int mNextFileNumber = 1;
    int mSelectedListItem = 0;

    boolean mRecording = false;
    SoundBoardArrayAdapter mSoundAdapter;

    private ArrayList<File> soundBiteFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_board);

        /*------------------------------------------
        Initialize
        ------------------------------------------*/
        mListView = (ListView) findViewById(R.id.listView);
        mRecordButton = (ImageButton) findViewById(R.id.recordButton);
        mediaRecorder = new MediaRecorder();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mDirectory = getExternalFilesDir(null);

        soundBiteFiles = new ArrayList<File>();
        Collections.addAll(soundBiteFiles, mDirectory.listFiles());

        /*------------------------------------------
        Create and set the custom array adapter for the list view
        ------------------------------------------*/
        mSoundAdapter = new SoundBoardArrayAdapter(
                this,
                R.layout.row_view,
                soundBiteFiles);
        mListView.setAdapter(mSoundAdapter);

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                /*------------------------------------------
                Stop the media player. Using reset instead
                of stop() because the mediaplayer's state
                needs to be in Idle in order to set the source.
                ------------------------------------------*/
                mp.reset();
            }
        });

        /*------------------------------------------
        Sets up the listeners for the listview
        -onClick (play the soundbite)
        -holding (rename file)
        ------------------------------------------*/
        setupListViewItemListeners();

        /*------------------------------------------
        Recording button listener
        ------------------------------------------*/
        mRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*------------------------------------------
                If pressed while recording, stop the
                recorder and update the list view.
                ------------------------------------------*/
                if (mRecording) {
                    mRecordButton.setImageResource(R.drawable.record_start_icon);
                    mediaRecorder.stop();
                    mRecording = false;
                    mSoundAdapter.add(new File(mDirectory.toString() + "/" + String.valueOf(mNextFileNumber) + ".3gp"));
                    mNextFileNumber++;
                }
                /*------------------------------------------
                Otherwise we'll want to start recording.
                Set the default file name based on the
                next available number
                ------------------------------------------*/
                else {
                    mRecordButton.setImageResource(R.drawable.record_stop_icon);
                    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

                    /*------------------------------------------
                    Find the next free file number
                    ------------------------------------------*/
                    String outputPath = mDirectory.toString() + "/" + String.valueOf(mNextFileNumber) + ".3gp";
                    while(new File(outputPath).exists()) {
                        mNextFileNumber++;
                        outputPath = mDirectory.toString() + "/" + String.valueOf(mNextFileNumber) + ".3gp";
                    }
                    mediaRecorder.setOutputFile(outputPath);

                    mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    try {
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                        mRecording = true;
                    } catch (IOException e) {
                        mRecording = false;
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view,  menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sound_board, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        mSelectedListItem = info.position;

        switch(item.getItemId()) {
            case R.id.context_menu_rename:
                Log.d(TAG, "Pressed rename");

                /*------------------------------------------
                Pop up a dialog to rename the soundbite file
                ------------------------------------------*/
                TextDialogFragment dialog = new TextDialogFragment();
                dialog.show(getFragmentManager(), "rename_dialog_alert");
                return true;

            case R.id.context_menu_delete:
                Log.d(TAG, "Pressed delete");

                if(!soundBiteFiles.get(mSelectedListItem).delete()) {
                    Log.i(TAG, "File delete failed");
                }

                mSoundAdapter.remove(soundBiteFiles.get(mSelectedListItem));
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    public boolean onDialogOk(String userInputName) {
        Log.d(TAG, "On Dialog OK button press: " + userInputName);

        if(-1 == mSelectedListItem) {
            Log.e(TAG, "Invalid list item index");
            return false;
        }

        File origFile = soundBiteFiles.get(mSelectedListItem);

        /*------------------------------------------
        Set the new file name
        ------------------------------------------*/
        String newFilePath = origFile.getAbsolutePath();
        newFilePath = newFilePath.substring(0, newFilePath.lastIndexOf("/"));
        newFilePath = newFilePath.concat("/" + userInputName);

        /*------------------------------------------
        Create the new file. The old file will be
        renamed to this one.
        ------------------------------------------*/
        //TODO don't hard code extension
        File newFile = new File(newFilePath + ".3gp");

        if(newFile.exists()) {
            //TODO: notify file already exists.
            return false;
        } else {
            /*------------------------------------------
            Rename the file. Toast on failure.
            ------------------------------------------*/
            if(origFile.renameTo(newFile)) {
                /*------------------------------------------
                Update the list adapter
                ------------------------------------------*/
                soundBiteFiles.set(mSelectedListItem, newFile);
                mSoundAdapter.notifyDataSetChanged();
                return true;
            } else {
                Log.d(TAG, "file rename failed");
                //TODO: notify rename fail
                return false;
            }
        }
    }

    private void setupListViewItemListeners() {
        /*------------------------------------------
        onClick listener
        ------------------------------------------*/
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "Clicked row: " + String.valueOf(position));

                /*------------------------------------------
                If already playing something, stop it and
                start playing the newly selected one. This
                will be the case even if the selected one
                is also the one currently playing.
                ------------------------------------------*/
                if(mediaPlayer.isPlaying()) {
                    mediaPlayer.reset();
                }

                /*------------------------------------------
                Play the soundbite!
                ------------------------------------------*/
                File file = soundBiteFiles.get(position);
                try {
                    mediaPlayer.setDataSource(file.getPath());
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                }catch(IOException e) {
                    e.printStackTrace();
                }
            }
        });

        registerForContextMenu(mListView);
    }
}
