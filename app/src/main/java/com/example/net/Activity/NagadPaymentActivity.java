package com.example.net.Activity;

import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.net.Config.Config;
import com.example.net.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
public class NagadPaymentActivity extends AppCompatActivity {
    private TextView paymentAmount, paymentAmount1, paymentPhoneNumber, invoiceIdTextView;
    private ImageView copyImg;
    private EditText transactionIdEditText;
    private Button verifyButton;
    private String invoiceId;  // Store the invoice ID
    private String selectedPaymentMethod;  // Store selected payment method
    private Config config;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_nagad_payment);

        paymentAmount = findViewById(R.id.paymentAmount);
        paymentAmount1 = findViewById(R.id.paymentAmount1);
        paymentPhoneNumber = findViewById(R.id.paymentPhoneNumber);
        transactionIdEditText = findViewById(R.id.transactionIdEditText);
        invoiceIdTextView = findViewById(R.id.invoiceId);
        copyImg = findViewById(R.id.copyImg);
        verifyButton = findViewById(R.id.verifyButton);

        copyImg.setOnClickListener(v -> copyPhoneNumberToClipboard());

        Intent intent = getIntent();
        String productTitle = intent.getStringExtra("productTitle");
        String totalAmount = intent.getStringExtra("totalAmount");
        String userName = intent.getStringExtra("USER_NAME");
        String productSize = intent.getStringExtra("product_size");
        String shopName = intent.getStringExtra("USER_SHOPNAME");
        selectedPaymentMethod = intent.getStringExtra("selectedPaymentMethod");
        String address = getIntent().getStringExtra("address");


        paymentAmount.setText(totalAmount);
        paymentAmount1.setText(totalAmount);

        invoiceId = generateInvoiceId();
        invoiceIdTextView.setText(invoiceId);
        int productQuantity = intent.getIntExtra("productQuantity", 1); // Default to 1 if not passed


        verifyButton.setOnClickListener(v -> {
            String paymentAmountStr = paymentAmount.getText().toString();
            String transactionId = transactionIdEditText.getText().toString();

            if (transactionId.isEmpty()) {
                Toast.makeText(NagadPaymentActivity.this, "Please enter a transaction ID", Toast.LENGTH_SHORT).show();
                return;
            }
            if (paymentAmountStr.isEmpty() || selectedPaymentMethod.isEmpty()) {
                Toast.makeText(NagadPaymentActivity.this, "Please complete all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d("Payment Details", "Amount: " + paymentAmountStr + ", Transaction ID: " + transactionId +
                    ", User Name: " + userName + ", Shop Name: " + shopName +
                    ", Invoice ID: " + invoiceId + ", Payment Method: " + selectedPaymentMethod +  ", Product Quantity: " +
                    productQuantity + ", Product Size: " + productSize + ", Product Title: " + productTitle + ", Address: " + address);

            if (!isNetworkAvailable()) {
                Toast.makeText(NagadPaymentActivity.this, "No network connection", Toast.LENGTH_SHORT).show();
                return;
            }
            new InsertPaymentTask().execute(paymentAmountStr, transactionId, userName, shopName, invoiceId, selectedPaymentMethod, productSize, address);

            new InsertOrderTask().execute(userName, shopName, invoiceId, paymentAmountStr, selectedPaymentMethod, String.valueOf(productQuantity), productSize, productTitle, address);
        });
        config = new Config();
    }

    private String generateInvoiceId() {
        long timestamp = System.currentTimeMillis();
        int randomNum = (int) (Math.random() * 10000);
        return "INV" + timestamp + randomNum;
    }

    private class InsertPaymentTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String paymentAmount = params[0];
            String transactionId = params[1];
            String userName = params[2];
            String shopName = params[3];
            String invoiceId = params[4];
            String selectedPaymentMethod = params[5];
            String productSize = params[6];
            String address = params[7];
            String status = "pending";  // Default status

            try {
                String baseUrl = Config.Base_Url;
                URL url = new URL(baseUrl + "/payments.php");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);

                OutputStream os = httpURLConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                String postData = URLEncoder.encode("total_amount", "UTF-8") + "=" + URLEncoder.encode(paymentAmount, "UTF-8") + "&" +
                        URLEncoder.encode("transaction_id", "UTF-8") + "=" + URLEncoder.encode(transactionId, "UTF-8") + "&" +
                        URLEncoder.encode("user_name", "UTF-8") + "=" + URLEncoder.encode(userName, "UTF-8") + "&" +
                        URLEncoder.encode("user_shop_name", "UTF-8") + "=" + URLEncoder.encode(shopName, "UTF-8") + "&" +
                        URLEncoder.encode("invoice_id", "UTF-8") + "=" + URLEncoder.encode(invoiceId, "UTF-8") + "&" +
                        URLEncoder.encode("payment_method", "UTF-8") + "=" + URLEncoder.encode(selectedPaymentMethod, "UTF-8") + "&" +
                        URLEncoder.encode("product_size", "UTF-8") + "=" + URLEncoder.encode(productSize, "UTF-8") + "&" +
                        URLEncoder.encode("address", "UTF-8") + "=" + URLEncoder.encode(address, "UTF-8") + "&" +
                        URLEncoder.encode("status", "UTF-8") + "=" + URLEncoder.encode(status, "UTF-8");

                writer.write(postData);
                writer.flush();
                writer.close();
                os.close();

                int responseCode = httpURLConnection.getResponseCode();
                Log.d("Payment Response", "Response Code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    return response.toString();
                } else {
                    return "Server Error - Response Code: " + responseCode;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Exception: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("Payment Response", "Server Response: " + result);

            try {
                JSONObject jsonResponse = new JSONObject(result);
                String status = jsonResponse.getString("status");
                if (status.equals("success")) {
                    showSuccessDialog();
                } else {
                    showUnsuccessDialog();
                    Toast.makeText(NagadPaymentActivity.this, "Payment failed: " + result, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(NagadPaymentActivity.this, "Error !!", Toast.LENGTH_SHORT).show();
            }
        }

    }
    private class InsertOrderTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String userName = params[0];
            String shopName = params[1];
            String invoiceId = params[2];
            String totalAmount = params[3];
            String paymentMethod = params[4];
            int productQuantity = Integer.parseInt(params[5]);
            String productSize = params[6];
            String productTitle = params[7];
            String address = params[8];

            try {
                String baseUrl = Config.Base_Url;
                URL url = new URL(baseUrl + "/insert_order.php");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);

                OutputStream os = httpURLConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                String postData = URLEncoder.encode("user_name", "UTF-8") + "=" + URLEncoder.encode(userName, "UTF-8") + "&" +
                        URLEncoder.encode("user_shop_name", "UTF-8") + "=" + URLEncoder.encode(shopName, "UTF-8") + "&" +
                        URLEncoder.encode("invoice_id", "UTF-8") + "=" + URLEncoder.encode(invoiceId, "UTF-8") + "&" +
                        URLEncoder.encode("total_amount", "UTF-8") + "=" + URLEncoder.encode(totalAmount, "UTF-8") + "&" +
                        URLEncoder.encode("payment_method", "UTF-8") + "=" + URLEncoder.encode(paymentMethod, "UTF-8") + "&" +
                        URLEncoder.encode("product_quantity", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(productQuantity), "UTF-8") + "&" +
                        URLEncoder.encode("product_size", "UTF-8") + "=" + URLEncoder.encode(productSize, "UTF-8") + "&" +
                        URLEncoder.encode("productTitle", "UTF-8") + "=" + URLEncoder.encode(productTitle, "UTF-8") + "&" +
                        URLEncoder.encode("address", "UTF-8") + "=" + URLEncoder.encode(address, "UTF-8");
                writer.write(postData);
                writer.flush();
                writer.close();
                os.close();

                int responseCode = httpURLConnection.getResponseCode();
                Log.d("Order Response", "Response Code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    return response.toString();
                } else {
                    return "Server Error - Response Code: " + responseCode;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Exception: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("Order Response", "Server Response: " + result);

            try {
                JSONObject jsonResponse = new JSONObject(result);
                String status = jsonResponse.getString("status");
                if (status.equals("success")) {
                } else {
                    Toast.makeText(NagadPaymentActivity.this, "Order failed: " + result, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(NagadPaymentActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showSuccessDialog() {
        Dialog successDialog = new Dialog(NagadPaymentActivity.this);
        successDialog.setContentView(R.layout.dialog_success);
        successDialog.setCancelable(true);
        successDialog.show();

        transactionIdEditText.setText("");

        Intent intent = getIntent();
        String userName = intent.getStringExtra("USER_NAME");
        String productTitle = intent.getStringExtra("productTitle");
        String totalAmount = intent.getStringExtra("totalAmount");
        String shopName = intent.getStringExtra("USER_SHOPNAME");
        String productSize = intent.getStringExtra("product_size");
        String address = intent.getStringExtra("address");
        int productQuantity = intent.getIntExtra("productQuantity", 1);

        InvoiceGenerator.generateInvoice(
                NagadPaymentActivity.this,
                invoiceId,
                userName,
                totalAmount,
                shopName,
                productQuantity,
                selectedPaymentMethod,
                productTitle,
                productSize,
                address
        );

        // Dismiss the dialog after 4 seconds and navigate to the home page
        new Handler().postDelayed(() -> {
            successDialog.dismiss();
            Intent intent1 = new Intent(NagadPaymentActivity.this, HomePageActivity.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent1);
            finish();
        }, 4000);
    }

    private void showUnsuccessDialog() {
        Dialog unsuccessDialog = new Dialog(NagadPaymentActivity.this);
        unsuccessDialog.setContentView(R.layout.dialog_payment_unsuccess);
        unsuccessDialog.setCancelable(true);
        unsuccessDialog.show();
        transactionIdEditText.setText("");
        new Handler().postDelayed(() -> {
            unsuccessDialog.dismiss();
            Intent intent = new Intent(NagadPaymentActivity.this, CheckOutPage.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }, 4000);
    }
    private void copyPhoneNumberToClipboard() {
        String phoneNumber = paymentPhoneNumber.getText().toString();
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        if (clipboard != null) {
            android.content.ClipData clip = android.content.ClipData.newPlainText("Phone Number", phoneNumber);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Copied!", Toast.LENGTH_SHORT).show();
        }
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
