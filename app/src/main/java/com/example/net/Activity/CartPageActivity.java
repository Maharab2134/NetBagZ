package com.example.net.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.net.Adapter.CartAdapter;
import com.example.net.Config.Config;
import com.example.net.Model.Order;
import com.example.net.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class CartPageActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private List<Order> orderList;
    private Config config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart_page);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        orderList = new ArrayList<>();
        config = new Config();

        Intent intent = getIntent();
        String userName = intent.getStringExtra("USER_NAME");

        fetchCartData(userName);
    }

    private void fetchCartData(String userName) {
        String baseUrl = Config.Base_Url;
        String url = baseUrl + "/fetch_cart_data.php?user_name=" + userName;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");

                            if ("success".equals(status)) {
                                JSONArray ordersArray = response.getJSONArray("data");
                                orderList.clear();

                                for (int i = 0; i < ordersArray.length(); i++) {
                                    JSONObject orderObject = ordersArray.getJSONObject(i);
                                    String productTitle = orderObject.optString("productTitle", "No Title");
                                    String totalAmount = orderObject.optString("total_amount", "0.0");
                                    String paymentMethod = orderObject.optString("payment_method", "Not Specified");
                                    String orderStatus = orderObject.optString("status", "Not Available");
                                    long orderTime = orderObject.optLong("order_time", System.currentTimeMillis());
                                    Order order = new Order(userName, productTitle, totalAmount, paymentMethod, orderStatus,orderTime);
                                    orderList.add(order);
                                }
                                if (cartAdapter == null) {
                                    cartAdapter = new CartAdapter(orderList);
                                    recyclerView.setAdapter(cartAdapter);
                                } else {
                                    cartAdapter.notifyDataSetChanged();
                                }
                            } else {
                                String message = response.getString("message");
                                Toast.makeText(CartPageActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(CartPageActivity.this, "Error parsing data", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                error -> {
                    Toast.makeText(CartPageActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                }
        );

        Volley.newRequestQueue(this).add(request);
    }


}
