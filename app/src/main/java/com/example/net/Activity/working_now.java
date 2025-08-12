package com.example.net.Activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.net.R;

public class working_now extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_working_now);

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(working_now.this, HelpCenterActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}