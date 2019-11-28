package com.example.android.miwok;

/**
 * Created by vanshika on 6/5/17.
 * {@link Word} represents a vocbulary word the the user wants to learn
 * Contains English and French word.
 */
public class Word {

    //English word.
    private String mDefaultTranslation;

    //French word.
    private String mFrenchTranslation;

    //Image resource id.
    private int mImageResourceId = NO_IMAGE_PROVIDED;

    //Word pronunciation resource id.
    private int mAudioResourceId;

    private static final int NO_IMAGE_PROVIDED = -1;

    /**
     * Create a new Word object
     *
     * @param DefaultTranslation is the English word.
     * @param FrenchTranslation is the French word.
     * @param AudioResourceId is the word's pronunciation.
     */
    public Word(String DefaultTranslation, String FrenchTranslation, int AudioResourceId) {
        mDefaultTranslation = DefaultTranslation;
        mFrenchTranslation = FrenchTranslation;
        mAudioResourceId = AudioResourceId;
    }

    /**
     * Create a new Word object that takes in an Image id as well.
     *
     * @param DefaultTranslation is the English word.
     * @param FrenchTranslation is the French word.
     * @param ImageResourceId is the Image Resource Id.
     * @param AudioResourceId is the word's pronunciation.
     */
    public Word(String DefaultTranslation, String FrenchTranslation, int ImageResourceId, int AudioResourceId) {
        mDefaultTranslation = DefaultTranslation;
        mFrenchTranslation = FrenchTranslation;
        mImageResourceId = ImageResourceId;
        mAudioResourceId = AudioResourceId;
    }

    /**
     * Get English translation.
     */
    public String getDefaultTranslation() {
        return mDefaultTranslation;
    }

    /**
     * Get French translation.
     */
    public String getFrenchTranslation() {
        return mFrenchTranslation;
    }

    /**
     * Return the Image resource id of the word.
     */
    public int getImageResourceId() {
        return mImageResourceId;
    }

    /**
     * @return whether or not there is an image for the word.
     */
    public boolean hasImage(){
        return mImageResourceId != NO_IMAGE_PROVIDED;
    }

    /**
     * Return the Audio resource id of the word.
     */
    public int getAudioResourceId() {
        return mAudioResourceId;
    }
}

