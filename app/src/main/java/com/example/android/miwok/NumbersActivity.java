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

public class NumbersActivity extends AppCompatActivity {

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

        //List of Number names.
        final ArrayList<Word> words = new ArrayList<>() ;
        words.add(new Word("one", "un", R.drawable.number_one, R.raw.number_one));
        words.add(new Word("two", "deux", R.drawable.number_two, R.raw.number_two));
        words.add(new Word("three", "trois", R.drawable.number_three, R.raw.number_three));
        words.add(new Word("four", "quatre", R.drawable.number_four, R.raw.number_four));
        words.add(new Word("five", "cinq", R.drawable.number_five, R.raw.number_five));
        words.add(new Word("six", "six", R.drawable.number_six, R.raw.number_six));
        words.add(new Word("seven", "sept", R.drawable.number_seven, R.raw.number_seven));
        words.add(new Word("eight", "huit", R.drawable.number_eight, R.raw.number_eight));
        words.add(new Word("nine", "neuf", R.drawable.number_nine, R.raw.number_nine));
        words.add(new Word("ten", "dix", R.drawable.number_ten, R.raw.number_ten));

        //Create an {@link WordAdapter} whose data source is a list of {@link Word}s
        WordAdapter adapter=
                new WordAdapter(this, words, R.color.category_numbers);

        //Finding the ListView.
        ListView listView = (ListView) findViewById(R.id.list);

        //Adding the Array Adapter to the list view.
        listView.setAdapter(adapter);

        /**
         *Set a click listener to play an audio file when the list item is clicked on.
         */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
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
                    mMediaPlayer = MediaPlayer.create(NumbersActivity.this, word.getAudioResourceId());
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
