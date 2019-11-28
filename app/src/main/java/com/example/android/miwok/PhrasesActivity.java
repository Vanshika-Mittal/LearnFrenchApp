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

public class PhrasesActivity extends AppCompatActivity {

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
        words.add(new Word("Hello!", "Salut!", R.raw.phrases_hello));
        words.add(new Word("Good Morning!", "Bonjour!", R.raw.phrases_good_morning));
        words.add(new Word("Goodbye!", "Au revoir!", R.raw.phrases_goodbye));
        words.add(new Word("See you soon!", "À bientôt!", R.raw.phrases_see_u_soon));
        words.add(new Word("See you tomorrow!", "À demain!", R.raw.phrases_see_u_tomm));
        words.add(new Word("What is your name?", "Comment tu-t'appelles?", R.raw.phrases_what_is_ur_name));
        words.add(new Word("My name is...", "Je m'appelle...", R.raw.phrases_my_name_is));
        words.add(new Word("How are you?", "Comment ça va?", R.raw.phrases_how_are_you));
        words.add(new Word("I'm fine, thank you.", "Ça va bien, merci.", R.raw.phrases_i_am_fine));

        WordAdapter adapter=
                new WordAdapter(this, words, R.color.category_phrases);
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
                    mMediaPlayer = MediaPlayer.create(PhrasesActivity.this, word.getAudioResourceId());
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
