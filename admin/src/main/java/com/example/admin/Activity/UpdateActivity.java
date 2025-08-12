/**
 * Copyright (c) 2024 Md. Maharab Hosen. All rights reserved.
 * Author: Md. Maharab Hosen
 */
package com.example.admin.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class UpdateActivity extends AppCompatActivity implements ProductAdapter.OnProductClickListener {

    private RecyclerView recyclerViewProducts;
    private EditText UpdateProductName, UpdateProductPrice, UpdateProductDetails, UpdateProductDescription, UpdateProductRating;
    private Button btnUpdateProduct;

    private ArrayList<Product> productList = new ArrayList<>();
    private Product selectedProduct;
    private ProductAdapter adapter;
    private Config config;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        // Initialize UI components
        recyclerViewProducts = findViewById(R.id.recyclerViewProducts);
        UpdateProductName = findViewById(R.id.editTextProductName);
        UpdateProductPrice = findViewById(R.id.editTextProductPrice);
        UpdateProductDetails = findViewById(R.id.editTextProductDetails);
        UpdateProductDescription = findViewById(R.id.editTextProductDescription);
        UpdateProductRating = findViewById(R.id.editTextProductRating);
        btnUpdateProduct = findViewById(R.id.btnUpdateProduct);

        adapter = new ProductAdapter(productList, this);
        recyclerViewProducts.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewProducts.setAdapter(adapter);

        fetchProducts();
        config = new Config();
        btnUpdateProduct.setOnClickListener(v -> updateProduct());
    }

    private void fetchProducts() {
        new FetchProductsTask().execute(Config.Base_Url + "/get_product_ids.php");
    }

    private class FetchProductsTask extends AsyncTask<String, Void, ArrayList<Product>> {
        @Override
        protected ArrayList<Product> doInBackground(String... urls) {
            ArrayList<Product> products = new ArrayList<>();
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();

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
            }
            return products;
        }

        @Override
        protected void onPostExecute(ArrayList<Product> products) {
            productList.clear();
            productList.addAll(products);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onProductClick(Product product) {
        selectedProduct = product;

        // Populate fields with selected product details
        UpdateProductName.setText(product.getName());
        UpdateProductPrice.setText(product.getPrice());
        UpdateProductDetails.setText(product.getDetails());
        UpdateProductDescription.setText(product.getDescription());
        UpdateProductRating.setText(product.getRating());
    }

    private void updateProduct() {
        if (selectedProduct == null) {
            Toast.makeText(this, "Please select a product first", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = UpdateProductName.getText().toString().trim();
        String price = UpdateProductPrice.getText().toString().trim();
        String details = UpdateProductDetails.getText().toString().trim();
        String description = UpdateProductDescription.getText().toString().trim();
        String rating = UpdateProductRating.getText().toString().trim();

        if (!name.isEmpty() && !price.isEmpty() && !details.isEmpty() && !description.isEmpty() && !rating.isEmpty()) {
            new UpdateProductTask().execute(selectedProduct.getId(), name, price, details, description, rating);
        } else {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
        }
    }

    private class UpdateProductTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            try {
                URL url = new URL(Config.Base_Url+"/update_product.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                String postData = "id=" + params[0] + "&name=" + params[1] + "&price=" + params[2]
                        + "&details=" + params[3] + "&description=" + params[4] + "&rating=" + params[5];

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(postData.getBytes());
                    os.flush();
                }

                return conn.getResponseCode() == HttpURLConnection.HTTP_OK;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(UpdateActivity.this, "Product updated successfully", Toast.LENGTH_SHORT).show();
                fetchProducts();
            } else {
                Toast.makeText(UpdateActivity.this, "Failed to update product", Toast.LENGTH_SHORT).show();
            }
        }

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(UpdateActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
