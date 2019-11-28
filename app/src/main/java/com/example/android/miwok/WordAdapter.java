package com.example.android.miwok;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by vanshika on 6/5/17.
 * Creates a custom array adapter.
 */
public class WordAdapter extends ArrayAdapter<Word> {

    private int mColorResourceId;

    /**
     * Create a new {@link WordAdapter} object.
     * @param context is the current context that the adapter is being created in
     * @param words is the list of {@link Word}s to be displayed.
     */
    public WordAdapter(Context context, ArrayList<Word> words, int colorResourceId) {
        super(context, 0, words);
        mColorResourceId = colorResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        // Get the {@link Word} object located at this position in the list
        Word currentWord = getItem(position);

        // Find the TextView in the list_item.xml layout with the ID version_name
        TextView frenchTextView = (TextView) listItemView.findViewById(R.id.french_text_view);
        // Get the version name from the current AndroidFlavor object and
        // set this text on the name TextView
        frenchTextView.setText(currentWord.getFrenchTranslation());

        // Find the TextView in the list_item.xml layout with the ID version_number
        TextView defaultTextView = (TextView) listItemView.findViewById(R.id.default_text_view);
        // Get the version number from the current AndroidFlavor object and
        // set this text on the number TextView
        defaultTextView.setText(currentWord.getDefaultTranslation());

        // Find the ImageView in the list_item.xml layout with the ID image
        ImageView imageView = (ImageView) listItemView.findViewById(R.id.image);

        if (currentWord.hasImage()) {
            //Sets the image to image source defined in the Word.
            imageView.setImageResource(currentWord.getImageResourceId());

            imageView.setVisibility(View.VISIBLE);
        } else {
            imageView.setVisibility(View.GONE);
        }

        // Set the theme color for the list item
        View textContainer = listItemView.findViewById(R.id.text_container);
        // Find the color that the resource ID maps to
        int containerColor = ContextCompat.getColor(getContext(), mColorResourceId);
        // Set the background color of the text container View
        textContainer.setBackgroundColor(containerColor);


        // Set the theme color for the list item
        View playIcon = listItemView.findViewById(R.id.play_icon);
        // Find the color that the resource ID maps to
        int iconColor = ContextCompat.getColor(getContext(), mColorResourceId);
        // Set the background color of the text container View
        playIcon.setBackgroundColor(iconColor);

        // Return the whole list item layout (containing 2 TextViews and an ImageView)
        // so that it can be shown in the ListView
        return listItemView;
    }
}
