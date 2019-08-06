package com.example.skillshop.ClassDescription;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.skillshop.NavigationFragments.Compose.ComposeFragment;
import com.example.skillshop.R;

public class ImageViewFragment extends DialogFragment {

    ImageView image;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        image = view.findViewById(R.id.ivClassImage);
        Bundle bundle = this.getArguments();

        String photo = bundle.getString("photo");

        switch (photo) {

            case "Culinary":
                image.setImageResource(R.drawable.cooking);
                break;
            case "Education":
                image.setImageResource(R.drawable.education);
                break;
            case "Fitness":
                image.setImageResource(R.drawable.fitness);
                break;
            case "Arts/Crafts":
                image.setImageResource(R.drawable.arts);
                break;
            case "Other":
                image.setImageResource(R.drawable.misc);
                break;
            default:
                Glide.with(this)
                        .load(photo)
                        .centerCrop()
                        .into(image);
                break;
        }

    }


}
