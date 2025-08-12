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

import com.example.admin.Adapter.ProductAdapter;
import com.example.admin.Config.Config;
import com.example.admin.MainActivity;
import com.example.admin.Model.Product;
import com.example.admin.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ProductListActivity extends AppCompatActivity {

    private RecyclerView recyclerViewProducts;
    private ProductAdapter productAdapter;
    private ArrayList<Product> productList = new ArrayList<>();
    private Config config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        recyclerViewProducts = findViewById(R.id.recyclerViewProducts);

        productAdapter = new ProductAdapter(productList, product -> {
        });
        recyclerViewProducts.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewProducts.setAdapter(productAdapter);

        config = new Config();
        fetchProducts();
    }

    private void fetchProducts() {
        String url = Config.Base_Url + "/get_product_ids.php";
        new FetchProductsTask().execute(url);
    }

    private class FetchProductsTask extends AsyncTask<String, Void, ArrayList<Product>> {
        @Override
        protected ArrayList<Product> doInBackground(String... urls) {
            ArrayList<Product> products = new ArrayList<>();
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

                // Parse JSON response
                JSONArray jsonArray = new JSONArray(response.toString());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    products.add(new Product(
                            obj.getString("id"),
                            obj.getString("name"),
                            obj.getString("price"),
                            obj.getString("details"),
                            obj.getString("description"),
                            obj.getString("rating")
                    ));
                }

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
            return products;
        }

        @Override
        protected void onPostExecute(ArrayList<Product> products) {
            if (products == null || products.isEmpty()) {
                Toast.makeText(ProductListActivity.this, "No products found", Toast.LENGTH_SHORT).show();
            } else {
                productList.clear();
                productList.addAll(products);
                productAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ProductListActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
