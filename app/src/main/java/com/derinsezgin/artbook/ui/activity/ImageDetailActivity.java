package com.derinsezgin.artbook.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.derinsezgin.artbook.R;
import com.squareup.picasso.Picasso;

public class ImageDetailActivity extends AppCompatActivity {

    private ImageView imageView;
    private String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imaga_detail);

        imageView = findViewById(R.id.imageView);
        imageUrl = getIntent().getExtras().getString("sendImage");

        if(imageUrl.isEmpty()){
            imageView.setBackgroundResource(R.drawable.place_holder);
        }
        else{
            Picasso.get()
                    .load(imageUrl)
                    .resize(600,600)
                    .into(imageView);
        }

    }
}