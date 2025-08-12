package com.example.net.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.net.Config.Config;
import com.example.net.R;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText phoneNumber, newPassword;
    private Button resetPassword;
    private boolean isPasswordVisible = false;
    private Config config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        phoneNumber = findViewById(R.id.phoneNumber);
        newPassword = findViewById(R.id.password);
        resetPassword = findViewById(R.id.resetButton);


        newPassword.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (newPassword.getRight() - newPassword.getCompoundDrawables()[2].getBounds().width())) {
                    isPasswordVisible = !isPasswordVisible;

                    newPassword.setInputType(isPasswordVisible ?
                            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD :
                            InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

                    newPassword.setSelection(newPassword.length());

                    int visibilityIcon = isPasswordVisible ? R.drawable.baseline_visibility_24 : R.drawable.baseline_visibility_off_24;

                    newPassword.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.baseline_lock_24, // Left lock icon
                            0, // No top drawable
                            visibilityIcon, // Right visibility icon
                            0); // No bottom drawable
                    return true; // Event is handled
                }
            }
            return false;
        });


        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber1 = phoneNumber.getText().toString().trim();
                String newPassword1 = newPassword.getText().toString().trim();

                if (phoneNumber1.isEmpty()) {
                    phoneNumber.setError("Phone number is required");
                } else if (newPassword1.isEmpty()) {
                    newPassword.setError("New password is required");
                } else {
                    new ResetPasswordTask().execute(phoneNumber1, newPassword1);
                }
            }
        });
        config = new Config();
    }

    private class ResetPasswordTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String phone = params[0];
            String newPassword = params[1];

            try {
                String baseUrl = Config.Base_Url;
                URL url = new URL(baseUrl + "/reset_password.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                String data = URLEncoder.encode("phone_number", "UTF-8") + "=" + URLEncoder.encode(phone, "UTF-8") + "&" +
                        URLEncoder.encode("new_password", "UTF-8") + "=" + URLEncoder.encode(newPassword, "UTF-8");

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(data);
                writer.flush();
                writer.close();
                os.close();

                // Read the response from the server
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder responseBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    responseBuilder.append(line);
                }
                return responseBuilder.toString();

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
                    String status = jsonResponse.getString("status");
                    String message = jsonResponse.getString("message");

                    Toast.makeText(ResetPasswordActivity.this, message, Toast.LENGTH_LONG).show();

                    if ("success".equals(status)) {
                        Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(ResetPasswordActivity.this, "Error parsing response", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(ResetPasswordActivity.this, "Password reset failed", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void updatePasswordInputType() {
        newPassword.setInputType(isPasswordVisible ?
                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD :
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        newPassword.setSelection(newPassword.length());
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}
