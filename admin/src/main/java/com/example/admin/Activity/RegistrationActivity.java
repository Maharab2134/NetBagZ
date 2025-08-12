/**
 * Copyright (c) 2024 Md. Maharab Hosen. All rights reserved.
 * Author: Md. Maharab Hosen
 */
package com.example.admin.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.admin.Config.Config;
import com.example.admin.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RegistrationActivity extends AppCompatActivity {

    private EditText username, phoneNumber, emailAddress, password;
    private Button signupBtn;
    private boolean isPasswordVisible = false;
    private Config config;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        username = findViewById(R.id.username);
        phoneNumber = findViewById(R.id.phoneNumber);
        emailAddress = findViewById(R.id.emailAddress);
        password = findViewById(R.id.password);
        signupBtn = findViewById(R.id.signupButton);
        config = new Config(); // ip

        signupBtn.setOnClickListener(v -> {
            String usernameText = username.getText().toString().trim();
            String phoneText = phoneNumber.getText().toString().trim();
            String emailText = emailAddress.getText().toString().trim();
            String passwordText = password.getText().toString().trim();

            if (!usernameText.isEmpty() && !phoneText.isEmpty() && !emailText.isEmpty() && !passwordText.isEmpty()) {
                Log.d("RegistrationData", "Username: " + usernameText + ", Phone: " + phoneText + ", Email: " + emailText + ", Password: " + passwordText);
                registerUser(usernameText, phoneText, emailText, passwordText);
            } else {
                Toast.makeText(RegistrationActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });
        password.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (password.getRight() - password.getCompoundDrawables()[2].getBounds().width())) {
                    isPasswordVisible = !isPasswordVisible;

                    password.setInputType(isPasswordVisible ?
                            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD :
                            InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

                    password.setSelection(password.length());

                    int visibilityIcon = isPasswordVisible ? R.drawable.baseline_visibility_24 : R.drawable.baseline_visibility_off_24;

                    password.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.baseline_lock_24, // Left lock icon
                            0, // No top drawable
                            visibilityIcon, // Right visibility icon
                            0); // No bottom drawable
                    return true; // Event is handled
                }
            }
            return false;
        });
    }

    @SuppressLint("StaticFieldLeak")
    private void registerUser(String username, String phoneNumber, String email, String password) {
        String baseUrl = Config.Base_Url;
        String serverUrl = baseUrl + "/registration.php"; // Replace with your actual server URL

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL(serverUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connection.setDoOutput(true);

                    String postData = "username=" + username + "&phone_number=" + phoneNumber + "&email=" + email + "&password=" + password;

                    OutputStream os = connection.getOutputStream();
                    os.write(postData.getBytes());
                    os.flush();
                    os.close();

                    int responseCode = connection.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        reader.close();
                        return response.toString();
                    } else {
                        return null;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String result) {
                if (result != null) {
                    try {
                        JSONObject jsonResponse = new JSONObject(result);

                        boolean success = jsonResponse.getBoolean("success");
                        String message = jsonResponse.getString("message");

                        Toast.makeText(RegistrationActivity.this, message, Toast.LENGTH_SHORT).show();
                        if (success) {
                            Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(RegistrationActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RegistrationActivity.this, "Registration failed, please try again", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RegistrationActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}
