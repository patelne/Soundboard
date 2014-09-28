package com.neilspatel.soundboard.soundboard;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class SoundBoard extends Activity {
    private static final String TAG = "SoundBoardActivity";
    ListView mListView;
    Button mRecordButton;
    MediaPlayer mediaPlayer;
    MediaRecorder mediaRecorder;
    File mDirectory;
    int mNextFileNumber;

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
        mRecordButton = (Button) findViewById(R.id.recordButton);
        mediaRecorder = new MediaRecorder();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mDirectory = getExternalFilesDir(null);

        /*------------------------------------------
        Calculate and store the new file number
        used for naming new recordings.
        This is just going to be the value after
        the current largest number.
        ------------------------------------------*/
        mNextFileNumber = 0;
        for(String file : mDirectory.list()) {
            String[] split = file.split("\\.");
            if(2 ==split.length &&
               0 == split[1].compareTo("3gp")) {
                if(mNextFileNumber < Integer.valueOf(split[0])) {
                    mNextFileNumber = Integer.valueOf(split[0]);
                }
            }
        }
        mNextFileNumber++;

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
        Sets up the listeners for
        selecting (playing)
        holding (renaming)
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
                    mRecordButton.setText(R.string.record);
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
                    mRecordButton.setText(R.string.recording);
                    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    mediaRecorder.setOutputFile(mDirectory.toString() + "/" + String.valueOf(mNextFileNumber) + ".3gp");
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

    private void setupListViewItemListeners() {
        /*------------------------------------------
        List view item listener
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
    }
}
