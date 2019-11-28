package com.example.android.miwok;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class FamilyActivity extends AppCompatActivity {

    //Handles media playback of all audio files.
    private MediaPlayer mMediaPlayer;

    //Handles audio focus when playing a sound file.
    private AudioManager mAudioManager;

    /**
     * This listener is triggered when the {@link MediaPlayer} has completed playing the audio file.
     */
    private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener(){
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            //Release media player resources now that sound file has finished playing
            releaseMediaPlayer();
        }
    };

    /**
     * This listener gets triggered when the audio focus changes.
     * (i.e, we loose or gain audio focus because of another app or device.)
     */
    private AudioManager.OnAudioFocusChangeListener mOnAudioFocusListener = new AudioManager.OnAudioFocusChangeListener(){
        @Override
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ||
                    focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK){
                //AUDIOFOCUS_LOSS_TRANSIENT means the app has lost audio focus temporarily.
                // AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK means our app can continue playing audio but
                // at a lower volume. Due to the short duration of our audio files, we are treating
                //both cases the same way.

                //Pause play back and reset it so that user can hear the word's pronunciation from
                //the beginning.
                mMediaPlayer.pause();
                mMediaPlayer.seekTo(0);
            } else if(focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                //AUDIOFOCUS_GAIN means we have regained audio focus and can resume playback.
                mMediaPlayer.start();
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS){
                //AUDIOFOCUS_LOSS means we have permanently lost audio focus.
                //Stop playback and release resources.
                releaseMediaPlayer();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.words_list);

        //Create and setup the {@link AudioManager} to request audio focus.
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        //List of English words.
        final ArrayList<Word> words = new ArrayList<>() ;
        words.add(new Word("father", "père", R.drawable.family_father, R.raw.family_father));
        words.add(new Word("mother", "mère", R.drawable.family_mother, R.raw.family_mother));
        words.add(new Word("son", "fils", R.drawable.family_son, R.raw.family_son));
        words.add(new Word("daughter", "fille", R.drawable.family_daughter, R.raw.family_daughter));
        words.add(new Word("younger brother", "petit frère", R.drawable.family_younger_brother, R.raw.family_younger_brother));
        words.add(new Word("younger sister", "petite sœur", R.drawable.family_younger_sister, R.raw.family_younger_sister));
        words.add(new Word("older brother", "grand frère", R.drawable.family_older_brother, R.raw.family_older_brother));
        words.add(new Word("older sister", "grand sœur", R.drawable.family_older_sister, R.raw.family_older_sister));
        words.add(new Word("grandfather", "grand-père", R.drawable.family_grandfather, R.raw.family_grandfather));
        words.add(new Word("grandmother", "grand-mère", R.drawable.family_grandmother, R.raw.family_grandmother));

        WordAdapter adapter=
                new WordAdapter(this, words,R.color.category_family);
        //Finding the ListView.
        ListView listView = (ListView) findViewById(R.id.list);
        //Adding the Array Adapter to the list view.
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                //Release media player resources because we are playing a new sound file.
                releaseMediaPlayer();

                //Get the {@link Word} object at the given position the user has clicked on.
                Word word = words.get(position);

                //Request audio focus to play file. The audio files are short so we shall request
                //a short amount of time with AUDIOFOCUS_GAIN_TRANSIENT
                int result = mAudioManager.requestAudioFocus(mOnAudioFocusListener,
                        AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

                if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    //Audio focus granted.

                    //Create and start the media player
                    mMediaPlayer = MediaPlayer.create(FamilyActivity.this, word.getAudioResourceId());
                    mMediaPlayer.start();

                    //Set a completion listener on the media player so that we can release the
                    // resources once the sound has finished playing
                    mMediaPlayer.setOnCompletionListener(mCompletionListener);
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        //When the app is stopped, release media player resources.
        releaseMediaPlayer();
    }

    /**
     * Clean up the media player by releasing its resources.
     */
    private void releaseMediaPlayer(){
        //If media player is not null, it might be playing a sound.
        if (mMediaPlayer != null){
            //Release MediaPlayer resources regardless of its state because we no longer need it.
            mMediaPlayer.release();
        }

        //Set media player to null.
        //In our code we have decided that it is an easy way to tell that the media player is not
        // configuered at the moment.
        mMediaPlayer = null;

        //Abandon audio focus, regardless of whether or not we were granted audio focus.
        mAudioManager.abandonAudioFocus(mOnAudioFocusListener);
    }

}
