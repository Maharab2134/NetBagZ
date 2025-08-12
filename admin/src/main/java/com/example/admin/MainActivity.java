/**
 * Copyright (c) 2025 Md. Maharab Hosen. All rights reserved.
 * Author: Md. Maharab Hosen
 */
package com.example.admin;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.admin.Activity.AdminSplashScreen;
import com.example.admin.Activity.InsertActivity;
import com.example.admin.Activity.LocationAddActivity;
import com.example.admin.Activity.OrderListActivity;
import com.example.admin.Activity.PaymentHistoryActivity;
import com.example.admin.Activity.ProductListActivity;
import com.example.admin.Activity.UpdateActivity;
import com.example.admin.Activity.UserListActivity;
import com.example.admin.Activity.SignupDelivery;
import com.example.admin.Config.Config;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private CardView cardView1, cardView2, cardView3, cardView4, cardView5, cardView6, cardView7, cardView8;
    private TextView textView, textView1, textView2, status;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageView LogOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        cardView1 = findViewById(R.id.cardView1);
        cardView2 = findViewById(R.id.cardView2);
        cardView3 = findViewById(R.id.cardView3);
        cardView4 = findViewById(R.id.cardView4);
        cardView5 = findViewById(R.id.cardView5);
        cardView6 = findViewById(R.id.cardView6);
        cardView7 = findViewById(R.id.cardView7);
        cardView8 = findViewById(R.id.cardView8);

        textView = findViewById(R.id.pendingOrder);
        textView1 = findViewById(R.id.completeOrder);
        textView2 = findViewById(R.id.monthlyEarn);
        status = findViewById(R.id.status);

        LogOut = findViewById(R.id.logout_icon);
        setStatusBasedOnTime();

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        fetchMonthlyEarnings();
        fetchOrdersData();

        swipeRefreshLayout.setOnRefreshListener(() -> {
            fetchMonthlyEarnings();
            fetchOrdersData();
            swipeRefreshLayout.setRefreshing(false);
        });


        cardView1.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, UserListActivity.class); //userList show
            startActivity(intent);
            finish();
        });

        cardView2.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProductListActivity.class); //productList show
            startActivity(intent);
            finish();
        });

        cardView3.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, InsertActivity.class); //addProduct
            startActivity(intent);
            finish();
        });

        cardView4.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, UpdateActivity.class); //updateProduct
            startActivity(intent);
            finish();
        });

        cardView5.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, OrderListActivity.class); //orderList show
            startActivity(intent);
            finish();
        });

        cardView6.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PaymentHistoryActivity.class); //paymentList Show
            startActivity(intent);
            finish();
        });

        cardView7.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LocationAddActivity.class); //addressAdd show
            startActivity(intent);
            finish();
        });

        cardView8.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SignupDelivery.class); // Delivery Man add
           startActivity(intent);
           finish();
        });

        LogOut.setOnClickListener(v -> onBackPressed()); // Exit Application
    }
    private void setStatusBasedOnTime() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        if (hour >= 5 && hour < 12) {
            status.setText("Good Morning");
        } else if (hour >= 17 && hour < 21) {
            status.setText("Good Evening");
        } else {
            status.setText("Good Night");
        }

    }
    private void refreshPage() {
        Toast.makeText(MainActivity.this, "Refreshing data...", Toast.LENGTH_SHORT).show();
        fetchMonthlyEarnings();
        fetchOrdersData();
    }

    private void fetchMonthlyEarnings() {
        String url = Config.Base_Url + "/get_monthly_earnings.php";
        new FetchMonthlyEarningsTask().execute(url);
    }

    private class FetchMonthlyEarningsTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            HttpURLConnection conn = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(urls[0]);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                JSONObject jsonObject = new JSONObject(response.toString());
                return jsonObject.getString("monthly_earnings");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return "0";
        }

        @Override
        protected void onPostExecute(String earnings) {
            textView2.setText(earnings);
        }
    }

    private void fetchOrdersData() {
        String url = Config.Base_Url + "/get_order_status_counts.php";
        new FetchOrdersDataTask().execute(url);
    }

    private class FetchOrdersDataTask extends AsyncTask<String, Void, String[]> {
        @Override
        protected String[] doInBackground(String... urls) {
            HttpURLConnection conn = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(urls[0]);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                JSONObject jsonObject = new JSONObject(response.toString());
                String pendingOrders = jsonObject.getString("pending_orders");
                String completedOrders = jsonObject.getString("completed_orders");

                return new String[]{pendingOrders, completedOrders};

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return new String[]{"0", "0"};
        }

        @Override
        protected void onPostExecute(String[] orders) {
            textView.setText(orders[0]);
            textView1.setText(orders[1]);
        }
    }
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Do you want to exit?")
                .setMessage("If you click Yes, you will be logged out and out of the app.")
                .setCancelable(false)
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Yes", (dialog, which) -> {
                    SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.apply();
                    Intent intent = new Intent(MainActivity.this, AdminSplashScreen.class);
                    startActivity(intent);
                    finish();
                    super.onBackPressed();
                })
                .show();
    }
}
