package com.example.net.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.net.R;

public class AboutMeActivity extends AppCompatActivity {
    ImageView toolbar_icon, imageView, imageView2, imageView3, imageView4;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_about_me);

        toolbar_icon = findViewById(R.id.toolbar_icon);

        imageView = findViewById(R.id.facebook_icon);
        imageView2 = findViewById(R.id.twitter_icon);
        imageView3 = findViewById(R.id.linkedin_icon);
        imageView4 = findViewById(R.id.github_icon);
        button = findViewById(R.id.btn_hire);


        imageView.setOnClickListener(v -> {  // facebook
            String url = "https://www.facebook.com/maharab.hosen.14";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        });

        imageView3.setOnClickListener(v -> {  // linkedin
            String url = "https://www.linkedin.com/in/md-maharab-hosen-679a70253/";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        });

        imageView2.setOnClickListener(v -> {  // twitter
            String url = "https://www.facebook.com/maharab.hosen.14";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        });

        imageView4.setOnClickListener(v -> {  // github
            String url = "https://github.com/Maharab2134";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        });

        toolbar_icon.setOnClickListener(v -> onBackPressed());
        button.setOnClickListener(v -> Toast.makeText(AboutMeActivity.this, "Working Naw......", Toast.LENGTH_SHORT).show());

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AboutMeActivity.this,ProfileActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}