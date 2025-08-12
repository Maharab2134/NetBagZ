package com.example.net.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.example.net.Config.Config;
import com.example.net.Fragment.LocationSelectionFragment;
import com.example.net.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class SignupActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText phoneNumberEditText;
    private EditText storeNameEditText;
    public EditText addressEditText;
    private EditText passwordEditText;
    private EditText postalCodeEditText;
    private Button signupButton;
    private TextView loginText;
    private Config config;
    private ImageView addIcon;

    private BottomSheetDialog bottomSheetDialog;

    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        usernameEditText = findViewById(R.id.username);
        phoneNumberEditText = findViewById(R.id.phoneNumber);
        storeNameEditText = findViewById(R.id.storeName);
        addressEditText = findViewById(R.id.address);
        postalCodeEditText = findViewById(R.id.postalCode);
        passwordEditText = findViewById(R.id.password);
        signupButton = findViewById(R.id.signupButton);
        loginText = findViewById(R.id.loginText);
        addIcon = findViewById(R.id.addIcon);

        passwordEditText.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (passwordEditText.getRight() - passwordEditText.getCompoundDrawables()[2].getBounds().width())) {
                    isPasswordVisible = !isPasswordVisible;

                    passwordEditText.setInputType(isPasswordVisible ?
                            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD :
                            InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

                    passwordEditText.setSelection(passwordEditText.length());

                    int visibilityIcon = isPasswordVisible ? R.drawable.baseline_visibility_24 : R.drawable.baseline_visibility_off_24;

                    passwordEditText.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.baseline_lock_24, // Left lock icon
                            0, // No top drawable
                            visibilityIcon, // Right visibility icon
                            0); // No bottom drawable
                    return true; // Event is handled
                }
            }
            return false;
        });


        addIcon.setOnClickListener(v -> {
                FragmentManager fragmentManager = getSupportFragmentManager();
                LocationSelectionFragment dialog = new LocationSelectionFragment();
                dialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialog);
                 dialog.show(fragmentManager, "location_dialog");
        });

        loginText.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        signupButton.setOnClickListener(v -> {
            String name = usernameEditText.getText().toString().trim();
            String phone = phoneNumberEditText.getText().toString().trim();
            String shopName = storeNameEditText.getText().toString().trim();
            String address = addressEditText.getText().toString().trim();
            String postalCode = postalCodeEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            Log.d("RegisterUser", "Name: " + name);
            Log.d("RegisterUser", "Phone: " + phone);
            Log.d("RegisterUser", "Shop Name: " + shopName);
            Log.d("RegisterUser", "Address: " + address);
            Log.d("RegisterUser", "Postal Code: " + postalCode);
            Log.d("RegisterUser", "Password: " + password);

            new RegisterUser().execute(name, phone, shopName, address, postalCode, password);
        });
        config = new Config();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private class RegisterUser extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String name = params[0];
            String phone = params[1];
            String shopName = params[2];
            String address = params[3];
            String postalCode = params[4];
            String password = params[5];

            try {
                String baseUrl = Config.Base_Url;
                URL url = new URL(baseUrl + "/register.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                String data = URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8") + "&" +
                        URLEncoder.encode("phone", "UTF-8") + "=" + URLEncoder.encode(phone, "UTF-8") + "&" +
                        URLEncoder.encode("shopname", "UTF-8") + "=" + URLEncoder.encode(shopName, "UTF-8") + "&" +
                        URLEncoder.encode("address", "UTF-8") + "=" + URLEncoder.encode(address, "UTF-8") + "&" +
                        URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8") + "&" +
                        URLEncoder.encode("postalCode", "UTF-8") + "=" + URLEncoder.encode(postalCode, "UTF-8");

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(data);
                writer.flush();
                writer.close();
                os.close();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                return reader.readLine();

            } catch (Exception e) {
                e.printStackTrace();
                return "Registration failed";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
            if ("Registration successful".equals(result)) {
                showSuccessDialog();
            } else {
                builder.setTitle("Error")
                        .setMessage("Registration failed. Please try again.")
                        .setPositiveButton("OK", (dialog, which) -> {
                            dialog.dismiss();
                        });
            }
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        private void showSuccessDialog() {
            Dialog successDialog = new Dialog(SignupActivity.this);
            successDialog.setContentView(R.layout.dialog_signup_success);
            successDialog.setCancelable(false);
            new Handler().postDelayed(() -> {
                successDialog.dismiss();
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }, 4000);

            successDialog.show();
        }

    }
}
