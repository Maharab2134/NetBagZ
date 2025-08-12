/**
 * Copyright (c) 2024 Md. Maharab Hosen. All rights reserved.
 * Author: Md. Maharab Hosen
 */
package com.example.admin.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.admin.Adapter.PaymentAdapter;
import com.example.admin.Config.Config;
import com.example.admin.MainActivity;
import com.example.admin.Model.Payment;
import com.example.admin.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class PaymentHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PaymentAdapter adapter;
    private ArrayList<Payment> paymentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_history);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        paymentList = new ArrayList<>();
        adapter = new PaymentAdapter(paymentList);
        recyclerView.setAdapter(adapter);

        Config config = new Config();
        String baseUrl = config.Base_Url;

        new FetchPaymentHistoryTask().execute(baseUrl + "/get_payment_history.php");
    }

    private class FetchPaymentHistoryTask extends AsyncTask<String, Void, ArrayList<Payment>> {

        @Override
        protected ArrayList<Payment> doInBackground(String... urls) {
            ArrayList<Payment> payments = new ArrayList<>();
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    JSONArray jsonArray = new JSONArray(response.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject paymentObj = jsonArray.getJSONObject(i);

                        payments.add(new Payment(
                                paymentObj.getString("id"),
                                paymentObj.getString("user_name"),
                                paymentObj.getString("user_shop_name"),
                                paymentObj.getString("total_amount"),
                                paymentObj.getString("payment_method"),
                                paymentObj.getString("transaction_id"),
                                paymentObj.getString("invoice_id"),
                                paymentObj.getString("status")
                        ));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return payments;
        }

        @Override
        protected void onPostExecute(ArrayList<Payment> result) {
            if (result == null || result.isEmpty()) {
                Toast.makeText(PaymentHistoryActivity.this, "No payment history found", Toast.LENGTH_SHORT).show();
            } else {
                paymentList.clear();
                paymentList.addAll(result);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(PaymentHistoryActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
