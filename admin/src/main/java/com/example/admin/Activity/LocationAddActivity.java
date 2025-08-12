/**
 * Copyright (c) 2024 Md. Maharab Hosen. All rights reserved.
 * Author: Md. Maharab Hosen
 */
package com.example.admin.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.admin.Config.Config;
import com.example.admin.MainActivity;
import com.example.admin.R;

import org.json.JSONException;
import org.json.JSONObject;

public class LocationAddActivity extends AppCompatActivity {

    private EditText districtEditText, unionEditText, villageEditText;
    private Button btnSubmit;

    private ImageView toolbar_icon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_location_add);

        districtEditText = findViewById(R.id.editTextDistrict);
        unionEditText = findViewById(R.id.editTextUnion);
        villageEditText = findViewById(R.id.editTextVillage);
        btnSubmit = findViewById(R.id.btnSubmit);
        toolbar_icon = findViewById(R.id.toolbar_icon);

        toolbar_icon.setOnClickListener(v -> onBackPressed());
        btnSubmit.setOnClickListener(v -> insertData());

    }
    private void insertData() {
        String district = districtEditText.getText().toString().trim();
        String union = unionEditText.getText().toString().trim();
        String village = villageEditText.getText().toString().trim();

        if (district.isEmpty() || union.isEmpty() || village.isEmpty()) {
            Toast.makeText(LocationAddActivity.this, "All fields must be filled", Toast.LENGTH_SHORT).show();
            return;
        }

        String baseUrl = Config.Base_Url;
        String url = baseUrl + "Insert_location.php?district=" + district + "&union=" + union + "&village=" + village;
        Log.d("URL", url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.has("success")) {
                            Toast.makeText(LocationAddActivity.this, jsonResponse.getString("success"), Toast.LENGTH_SHORT).show();
                            clearInputFields();
                        } else if (jsonResponse.has("error")) {
                            Toast.makeText(LocationAddActivity.this, jsonResponse.getString("error"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(LocationAddActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(LocationAddActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show());
        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void clearInputFields() {
        districtEditText.setText("");
        unionEditText.setText("");
        villageEditText.setText("");
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(LocationAddActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}