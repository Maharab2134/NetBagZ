package com.example.admin.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.admin.Config.Config;
import com.example.admin.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.telephony.SmsManager;

public class SignupDelivery extends AppCompatActivity {
    private EditText name, userName, phoneNumber, emailAddress, password, address;
    private Button signupBtn;
    private boolean isPasswordVisible = false;
    private Config config;
    private ExecutorService executorService;
    private static final int SMS_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        name = findViewById(R.id.name);
        userName = findViewById(R.id.userName);
        phoneNumber = findViewById(R.id.phoneNumber);
        emailAddress = findViewById(R.id.emailAddress);
        password = findViewById(R.id.password);
        address = findViewById(R.id.address);
        signupBtn = findViewById(R.id.signupButton);
        config = new Config();
        executorService = Executors.newSingleThreadExecutor();

        signupBtn.setOnClickListener(v -> {
            String nameText = name.getText().toString().trim();
            String usernameText = userName.getText().toString().trim();
            String phoneText = phoneNumber.getText().toString().trim();
            String emailText = emailAddress.getText().toString().trim();
            String passwordText = password.getText().toString().trim();
            String addressText = address.getText().toString().trim();

            if (!nameText.isEmpty() && !phoneText.isEmpty() && !emailText.isEmpty() && !passwordText.isEmpty() && !addressText.isEmpty()) {
                Log.d("RegistrationData", "Name: " + nameText + ", Phone: " + phoneText + ", Email: " + emailText + ", Password: " + passwordText + ", Address: " + addressText);
                registerDeli(nameText, usernameText, phoneText, emailText, passwordText, addressText);
            } else {
                Toast.makeText(SignupDelivery.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
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
                            R.drawable.baseline_lock_24,
                            0,
                            visibilityIcon,
                            0);
                    return true;
                }
            }
            return false;
        });

        // Request permissions if not already granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "SMS permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "SMS permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void registerDeli(String name, String username, String phoneNumber, String email, String password, String address) {
        executorService.execute(() -> {
            try {
                String baseUrl = Config.Base_Url;
                String serverUrl = baseUrl + "/delivery_registration.php";

                URL url = new URL(serverUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setDoOutput(true);

                String postData = "name=" + name + "&username=" + username + "&phone_number=" + phoneNumber + "&email=" + email + "&password=" + password + "&address=" + address;

                try (OutputStream os = connection.getOutputStream()) {
                    os.write(postData.getBytes());
                    os.flush();
                }

                int responseCode = connection.getResponseCode();
                StringBuilder response = new StringBuilder();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                    }
                }

                runOnUiThread(() -> handleResponse(response.toString()));
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(SignupDelivery.this, "Registration failed, please try again", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void handleResponse(String result) {
        if (result != null) {
            try {
                JSONObject jsonResponse = new JSONObject(result);
                boolean success = jsonResponse.getBoolean("success");
                String message = jsonResponse.getString("message");

                Toast.makeText(SignupDelivery.this, message, Toast.LENGTH_SHORT).show();

                if (success) {
                    String phoneText = phoneNumber.getText().toString().trim();
                    String usernameText = userName.getText().toString().trim();
                    String passwordText = password.getText().toString().trim();
                    String emailText = emailAddress.getText().toString().trim();

                    if (ContextCompat.checkSelfPermission(SignupDelivery.this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                        sendSMS(phoneText, usernameText, passwordText);
                        sendEmail(emailText, usernameText, passwordText);
                    } else {
                        Toast.makeText(this, "Permission to send SMS not granted", Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(this, "Registration Success.", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(SignupDelivery.this, "Error parsing response", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(SignupDelivery.this, "Registration failed, please try again", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendSMS(String phoneNumber, String username, String password) { // sms send
        try {
            String Link = "h";
            String message = "Welcome to NetBagz!" + "\nYour Username: " + username + "\nYour Password is: " + password + "\n App Link is: " + Link;
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(SignupDelivery.this, "Message sent to " + phoneNumber, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(SignupDelivery.this, "Failed to send message", Toast.LENGTH_SHORT).show();
        }
    }
    private void sendEmail(String emailAddress, String username, String password) { // email send
        String Link = "h";
        String subject = "Welcome to NetBagz!";
        String message = "Your Username: " + username + "\nYour password is: " + password +" \n App Link is: " + Link;

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailAddress});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);

        try {
            startActivity(Intent.createChooser(emailIntent, "Choose an email client"));
        } catch (android.content.ActivityNotFoundException e) {
            Toast.makeText(SignupDelivery.this, "No email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

}
