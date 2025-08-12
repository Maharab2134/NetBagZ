/**
 * Copyright (c) 2024 Md. Maharab Hosen. All rights reserved.
 * Author: Md. Maharab Hosen
 */
package com.example.admin.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.admin.Config.Config;
import com.example.admin.MainActivity;
import com.example.admin.R;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class InsertActivity extends AppCompatActivity {

    private Config config;
    private EditText editTextProductName, editTextProductPrice, editTextProductDetails, editTextProductDescription, editTextProductRating;
    private Button btnInsert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);

        // Initialize UI components
        editTextProductName = findViewById(R.id.editTextProductName);
        editTextProductPrice = findViewById(R.id.editTextProductPrice);
        editTextProductDetails = findViewById(R.id.editTextProductDetails);
        editTextProductDescription = findViewById(R.id.editTextProductDescription);
        editTextProductRating = findViewById(R.id.editTextProductRating);
        btnInsert = findViewById(R.id.btnInsert);

        config = new Config(); // ip
        btnInsert.setOnClickListener(v -> insertProduct()); // insert product
    }

    private void insertProduct() {
        String name = editTextProductName.getText().toString().trim();
        String price = editTextProductPrice.getText().toString().trim();
        String details = editTextProductDetails.getText().toString().trim();
        String description = editTextProductDescription.getText().toString().trim();
        String rating = editTextProductRating.getText().toString().trim();

        if (!name.isEmpty() && !price.isEmpty() && !details.isEmpty() && !description.isEmpty() && !rating.isEmpty()) {
            new InsertProductTask().execute(name, price, details, description, rating);
        } else {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
        }
    }

    private class InsertProductTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            try {
                String baseUrl = config.Base_Url;
                URL url = new URL(baseUrl + "/insert_product.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                String postData = "name=" + params[0] + "&price=" + params[1] + "&details=" + params[2]
                        + "&description=" + params[3] + "&rating=" + params[4];

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(postData.getBytes());
                    os.flush();
                }

                return conn.getResponseCode() == HttpURLConnection.HTTP_OK;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(InsertActivity.this, "Product inserted successfully", Toast.LENGTH_SHORT).show();
                clearInputFields();
            } else {
                Toast.makeText(InsertActivity.this, "Failed to insert product", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void clearInputFields() {
        editTextProductName.setText("");
        editTextProductPrice.setText("");
        editTextProductDetails.setText("");
        editTextProductDescription.setText("");
        editTextProductRating.setText("");
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(InsertActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
