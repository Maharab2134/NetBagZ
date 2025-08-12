package com.example.net.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class LoginActivity extends AppCompatActivity {

    public EditText phoneNumber, password;
    public Button loginButton;
    private TextView forgetPasswordText, signupText2;
    private boolean isPasswordVisible = false;
    private Config config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        phoneNumber = findViewById(R.id.phoneNumber);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        forgetPasswordText = findViewById(R.id.forgetPassword);
        signupText2 = findViewById(R.id.signupText2);

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

        loginButton.setOnClickListener(v -> {
            String phoneNumberText = phoneNumber.getText().toString().trim();
            String passwordText = password.getText().toString().trim();

            if (phoneNumberText.isEmpty()) {
                phoneNumber.setError("Phone Number is required");
            } else if (passwordText.isEmpty()) {
                password.setError("Password is required");
            } else {
                new LoginUser().execute(phoneNumberText, passwordText);
            }
        });

        signupText2.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
            finish();
        });

        forgetPasswordText.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
            startActivity(intent);
            finish();
        });
        config = new Config();
    }

    private class LoginUser extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String phone = params[0];
            String password = params[1];

            try {
                String baseUrl = Config.Base_Url;
                URL url = new URL(baseUrl + "/login.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                String data = URLEncoder.encode("phone", "UTF-8") + "=" + URLEncoder.encode(phone, "UTF-8") + "&" +
                        URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(data);
                writer.flush();
                writer.close();
                os.close();

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
                Log.d("LoginActivity", "Response: " + result);

                try {
                    JSONObject jsonResponse = new JSONObject(result);
                    String status = jsonResponse.getString("status");
                    String message = jsonResponse.getString("message");

                    if ("success".equals(status)) {
                        String userName = jsonResponse.getString("name");
                        String userPhone = jsonResponse.getString("phone");
                        String userAddress = jsonResponse.getString("address");
                        String userShopName = jsonResponse.getString("shopname");
                        String accountStatus = jsonResponse.getString("userstatus");

                        if ("Active".equals(accountStatus)) {
                            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("USER_NAME", userName);
                            editor.putString("USER_PHONE", userPhone);
                            editor.putString("USER_ADDRESS", userAddress);
                            editor.putString("USER_SHOPNAME", userShopName);
                            editor.putString("USER_STATUS", accountStatus);
                            editor.putBoolean("IS_LOGGED_IN", true);
                            editor.apply();

                            Intent intent = new Intent(LoginActivity.this, HomePageActivity.class);
                            intent.putExtra("USER_NAME", userName);
                            intent.putExtra("USER_ADDRESS", userAddress);
                            intent.putExtra("USER_SHOPNAME", userShopName);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Your account is inactive. Please wait for a few minutes.", Toast.LENGTH_LONG).show();
                        }

                    } else {
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(LoginActivity.this, "Error parsing response", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_LONG).show();
            }
        }


    }
}