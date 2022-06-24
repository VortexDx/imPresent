package com.final_project.impresent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

public class ShowImage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);

        ImageView imageView = findViewById(R.id.imageView_showImage_studentImage);

        Intent incomingIntent = getIntent();
        String imageString = "";
        imageString = incomingIntent.getStringExtra("imageString");
        Log.d("Show Image", imageString);
        //convert to bitmap from base64 String
        if(imageString.length()>0) {
            byte[] decodedString = Base64.decode(imageString, Base64.DEFAULT);
            Bitmap imageBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            imageView.setImageBitmap(imageBitmap);
        }
    }
}