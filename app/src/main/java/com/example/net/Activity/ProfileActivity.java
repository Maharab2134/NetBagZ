package com.example.net.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.net.Config.Config;
import com.example.net.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ProfileActivity extends AppCompatActivity {
    private Config config;

    public TextView name, phone, address, status, postalCode;
    public ImageView statusIcon;
    ConstraintLayout helpLayout, setting, devProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        name = findViewById(R.id.textView10);
        phone = findViewById(R.id.textView23);
        address = findViewById(R.id.textView24);
        status = findViewById(R.id.textView12);
        postalCode = findViewById(R.id.textView25);
        statusIcon = findViewById(R.id.statusIcon);

        devProfile = findViewById(R.id.devprofile);
        helpLayout = findViewById(R.id.help);
        setting = findViewById(R.id.setting);

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String userName = sharedPreferences.getString("USER_NAME", "N/A");

        new FetchUserData().execute(userName);
        config = new Config();

        helpLayout.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, HelpCenterActivity.class);
            intent.putExtra("USER_NAME", userName);
            startActivity(intent);
            finish();
        });

        setting.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, SettingActivity.class);
            startActivity(intent);
            finish();
        });

        devProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ChatbotActivity.class);
            startActivity(intent);
            finish();
        });

    }

    private class FetchUserData extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String userName = params[0];
            String response = "";

            try {
                String baseUrl = Config.Base_Url;
                String apiUrl = baseUrl +"/fetch_users.php?username=" + userName;
                URL url = new URL(apiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder responseBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    responseBuilder.append(line);
                }
                response = responseBuilder.toString();
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result != null && !result.isEmpty()) {
                try {
                    JSONObject user = new JSONObject(result);
                    if (user.has("name")) {
                        name.setText(user.getString("name"));
                        phone.setText(user.getString("phone"));
                        address.setText(user.getString("address"));
                        status.setText(user.getString("status"));
                        postalCode.setText(user.getString("postalCode"));

                        String userStatus = user.getString("status");
                        if ("Inactive".equalsIgnoreCase(userStatus)) {
                            statusIcon.setImageResource(R.drawable.status_inactive);
                        } else if ("Active".equalsIgnoreCase(userStatus)) {
                            statusIcon.setImageResource(R.drawable.status_active);
                        }
                    } else {
                        Toast.makeText(ProfileActivity.this, "User not found", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ProfileActivity.this, "Error parsing data", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(ProfileActivity.this, "Error fetching data", Toast.LENGTH_LONG).show();
            }
        }

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ProfileActivity.this, HomePageActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}
