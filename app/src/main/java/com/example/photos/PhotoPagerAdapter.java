package com.example.photos;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AlignmentSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;

public class PhotoPagerAdapter extends PagerAdapter {

    private Context context;
    private ArrayList<Photo> photoList;

    public PhotoPagerAdapter(Context context, ArrayList<Photo> photoList) {
        this.context = context;
        this.photoList = photoList;
    }

    @Override
    public int getCount() {
        return photoList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.pager_item, container, false);
        ImageView imageView = view.findViewById(R.id.image_view);
        imageView.setImageURI(photoList.get(position).getUri());
        ImageButton imageButton = view.findViewById(R.id.image_button);
        // Assuming Photo class has methods getPeople() and getLocations()
        ArrayList<String> peopleList = photoList.get(position).getPeople();
        ArrayList<String> locationsList = photoList.get(position).getLocations();

        // Create a StringBuilder to build the concatenated string
        StringBuilder concatenatedPeople = new StringBuilder();
        StringBuilder concatenatedLoc = new StringBuilder();

        // Concatenate peopleList with newline characters
        for (String person : peopleList.subList(0,Integer.min(peopleList.size(),3))) {
            if (person.length() > 15) {
                // Append the first 14 characters of the person's name followed by '...'
                concatenatedPeople.append(person.substring(0, 15)).append("...");
            } else {
                // Append the entire person's name
                concatenatedPeople.append(person);
            }
            concatenatedPeople.append("\n");
        }

        // Concatenate locationsList with newline characters
        for (String location : locationsList.subList(0,Integer.min(locationsList.size(),3))) {
            if (location.length() > 15) {
                // Append the first 14 characters of the person's name followed by '...'
                concatenatedLoc.append(location.substring(0, 15)).append("...");
            } else {
                // Append the entire person's name
                concatenatedLoc.append(location);
            }
            concatenatedLoc.append("\n");
        }
        String resultPeople = (!peopleList.isEmpty() ?"_________________\nPEOPLE\n_________________\n":"")+concatenatedPeople.toString() + (peopleList.size() > 3 ? "...\n" : "") ;
        String resultLoc = (!locationsList.isEmpty() ?"_________________\nLOCATION\n_________________\n":"")+concatenatedLoc.toString() + (locationsList.size() > 3 ? "...\n" : "");

        View box = view.findViewById(R.id.transparent_box);
        TextView textView1 = view.findViewById(R.id.text_overlay1);
        TextView textView2 = view.findViewById(R.id.text_overlay2);
//        textView1.setText(resultPeople);
//        textView2.setText(resultLoc);

        if(resultPeople.length()>0){
            // Create a SpannableStringBuilder for textView1's text
            SpannableStringBuilder builder1 = new SpannableStringBuilder(resultPeople);
            builder1.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_NORMAL),
                    42, resultPeople.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            textView1.setText(builder1);
        }
        if(resultLoc.length()>0) {
            SpannableStringBuilder builder2 = new SpannableStringBuilder(resultLoc);
            builder2.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_NORMAL),
                    44, resultLoc.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            textView2.setText(builder2);
        }

        if(!locationsList.isEmpty() && !peopleList.isEmpty()){

            int totAdjusted = peopleList.size()>=3 || locationsList.size()>=3 ? 8 : Integer.max(peopleList.size(),locationsList.size())*2;
            ViewGroup.MarginLayoutParams layoutParams0 = (ViewGroup.MarginLayoutParams) box.getLayoutParams();
            layoutParams0.height = 24*(totAdjusted+10);
            box.setLayoutParams(layoutParams0);

            // Set margin bottom for textView1
            ViewGroup.MarginLayoutParams layoutParams1 = (ViewGroup.MarginLayoutParams) textView1.getLayoutParams();
            layoutParams1.height = (24*(totAdjusted+10))/2;
            layoutParams1.bottomMargin = layoutParams1.height/2; // Set your desired margin in pixels
            textView1.setLayoutParams(layoutParams1);
            textView1.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);

            // Set margin bottom for textView2
            ViewGroup.MarginLayoutParams layoutParams2 = (ViewGroup.MarginLayoutParams) textView2.getLayoutParams();
            layoutParams2.height = (24*(totAdjusted+10))/2;
            layoutParams2.topMargin = layoutParams2.height/2; // Set your desired margin in pixels
            textView2.setLayoutParams(layoutParams2);
            textView2.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
        }else if(!locationsList.isEmpty() || !peopleList.isEmpty()){
            TextView textView = !peopleList.isEmpty() ? textView1 : textView2;
            int totAdjusted = peopleList.size()>3 || locationsList.size()>3 ? 4 : Integer.max(peopleList.size(),locationsList.size());
            ViewGroup.MarginLayoutParams layoutParams0 = (ViewGroup.MarginLayoutParams) box.getLayoutParams();
            layoutParams0.height = 24*(totAdjusted+5);
            box.setLayoutParams(layoutParams0);

            ViewGroup.MarginLayoutParams layoutParams1 = (ViewGroup.MarginLayoutParams) textView.getLayoutParams();
            layoutParams1.height = (24*(totAdjusted+5));
            textView.setGravity(Gravity.LEFT);
            layoutParams1.topMargin = 0;
            layoutParams1.bottomMargin = 0;
            textView.setLayoutParams(layoutParams1);
            textView.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);

            TextView other = !peopleList.isEmpty() ? textView2 : textView1;
            other.setVisibility(View.GONE);
        }else{
            box.setVisibility(View.GONE);
            textView1.setVisibility(View.GONE);
            textView2.setVisibility(View.GONE);
        }


        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform the action you want when the button is clicked,
                // such as navigating back
                ((Activity) context).finish();
            }
        });
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
