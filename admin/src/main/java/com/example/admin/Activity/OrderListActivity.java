/**
 * Copyright (c) 2024 Md. Maharab Hosen. All rights reserved.
 * Author: Md. Maharab Hosen
 */
package com.example.admin.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.admin.Adapter.OrderAdapter;
import com.example.admin.Config.Config;
import com.example.admin.MainActivity;
import com.example.admin.Model.Order;
import com.example.admin.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class OrderListActivity extends AppCompatActivity {

    private RecyclerView orderRecyclerView;
    private OrderAdapter orderAdapter;
    private List<Order> orderList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        orderRecyclerView = findViewById(R.id.orderRecyclerView);
        orderRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        orderAdapter = new OrderAdapter(this, orderList);
        orderRecyclerView.setAdapter(orderAdapter);


        fetchOrders();
    }

    private void fetchOrders() {
        new FetchOrdersTask().execute();
    }

    private class FetchOrdersTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            try {
                String baseUrl = Config.Base_Url;
                URL url = new URL(baseUrl + "/get_orders.php");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");

                int responseCode = httpURLConnection.getResponseCode();
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
            super.onPostExecute(result);

            try {
                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    String userName = jsonObject.getString("user_name");
                    String shopName = jsonObject.getString("user_shop_name");
                    String invoiceId = jsonObject.getString("invoice_id");
                    String totalAmount = jsonObject.getString("total_amount");
                    String paymentMethod = jsonObject.getString("payment_method");
                    int productQuantity = jsonObject.getInt("product_quantity");
                    String productSize = jsonObject.getString("product_size");
                    String productTitle = jsonObject.getString("productTitle");
                    String address = jsonObject.getString("address");

                    Order order = new Order(userName, shopName, invoiceId, totalAmount, paymentMethod, productSize, productQuantity, productTitle, address, "Pending");
                    orderList.add(order);
                }

                orderAdapter.notifyDataSetChanged();
                for (Order order : orderList) {
                    displayOrderStatus(order.getInvoiceId(), order);
                }

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(OrderListActivity.this, "No Data Show" , Toast.LENGTH_LONG).show();
            }
        }
    }

    private void displayOrderStatus(final String invoiceId, final Order order) {
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                String invoiceId = params[0];
                try {
                    String baseUrl = Config.Base_Url;
                    URL url = new URL(baseUrl + "/check_payment_status.php");
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);

                    OutputStream os = httpURLConnection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    String postData = URLEncoder.encode("invoice_id", "UTF-8") + "=" + URLEncoder.encode(invoiceId, "UTF-8");
                    writer.write(postData);
                    writer.flush();
                    writer.close();
                    os.close();

                    int responseCode = httpURLConnection.getResponseCode();
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
                super.onPostExecute(result);

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String status = jsonObject.getString("status");

                    order.setStatus(status);
                    orderAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(OrderListActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }.execute(invoiceId);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(OrderListActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}
