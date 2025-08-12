package com.example.net.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.example.net.Config.Config;
import com.example.net.R;
import com.example.net.Adapter.SizeAdapter;
import com.example.net.Adapter.ViewPagerAdapter;
import com.example.net.Fragment.DescriptionFragment;
import com.example.net.Fragment.DetailsFragment;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ProDetailsActivity extends AppCompatActivity {

    private TextView productTitle, productPrice, rattingText;
    private Button btn_buy;
    private SizeAdapter sizeAdapter;
    private ImageView backIcon, shareIcon, productImageView;
    private RecyclerView recyclerView;
    private RatingBar ratingBar;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Config config;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        // Initialize Views
        productTitle = findViewById(R.id.product_title);
        productPrice = findViewById(R.id.product_price);
        rattingText = findViewById(R.id.rattingText);
        recyclerView = findViewById(R.id.recyclerSize);
        ratingBar = findViewById(R.id.rattingBar);
        btn_buy = findViewById(R.id.btn_buy);
        backIcon = findViewById(R.id.backBtn);
        shareIcon = findViewById(R.id.shareBtn);
        productImageView = findViewById(R.id.product_image_view);
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        String userName = getIntent().getStringExtra("USER_NAME");
        String shopName = getIntent().getStringExtra("SHOP_NAME");
        // Initialize Size Adapter
        initSize();

        // Set up TabLayout with ViewPager
        tabLayout.setupWithViewPager(viewPager);
        setupViewPager(viewPager);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Fetch Product Details
        int productId = getIntent().getIntExtra("product_id", -1);
        int imageResId = getIntent().getIntExtra("product_image", -1);

        if (imageResId != -1) {
            productImageView.setImageResource(imageResId);
        }

        new FetchProductDetails().execute(productId);

        btn_buy.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale_anim));
        });

        backIcon.setOnClickListener(v -> {
            Intent intent = new Intent(ProDetailsActivity.this, HomePageActivity.class);
            startActivity(intent);
            finish();
        });

        shareIcon.setOnClickListener(v -> Toast.makeText(ProDetailsActivity.this, "Working Now", Toast.LENGTH_SHORT).show());

        swipeRefreshLayout.setOnRefreshListener(() -> {
            refreshPage();
        });
        btn_buy.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale_anim));

            // Get product details
            String title = productTitle.getText().toString();
            String price = productPrice.getText().toString();

            // Pass the details to CartPageActivity
            Intent intent = new Intent(ProDetailsActivity.this, CheckOutPage.class);
            intent.putExtra("product_id", productId);
            intent.putExtra("product_title", title);
            intent.putExtra("product_price", price);
            intent.putExtra("product_image_res_id", imageResId);  // Pass the image resource ID
            intent.putExtra("USER_NAME", userName);
            intent.putExtra("SHOP_NAME", shopName);
            intent.putExtra("product_size", selectedSize);
            startActivity(intent);
        });
        config = new Config();
    }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(DescriptionFragment.newInstance("Sample description here"), "Description");
        adapter.addFragment(DetailsFragment.newInstance("Sample details here"), "Details");
        viewPager.setAdapter(adapter);
    }
    private void refreshPage() {
        new Handler().postDelayed(() -> {
            swipeRefreshLayout.setRefreshing(false);
        }, 1500);
    }
    private String selectedSize = "Size not selected";
    public void initSize() {
        ArrayList<String> list = new ArrayList<>();
        list.add("S");
        list.add("M");
        list.add("XL");
        list.add("XXL");
        sizeAdapter = new SizeAdapter(list, size -> selectedSize = size);
        recyclerView.setAdapter(sizeAdapter);
    }

    private class FetchProductDetails extends AsyncTask<Integer, Void, String> {
        @Override
        protected String doInBackground(Integer... params) {
            int productId = params[0];
            String response = null;
            try {
                String baseUrl = Config.Base_Url;
                String urlString = baseUrl + "/get_product.php?id=" + productId;
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                Log.d("ProductPage", "Fetching data from: " + urlString);

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder responseBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    responseBuilder.append(line);
                }
                response = responseBuilder.toString();
                Log.d("ProductPage", "Response: " + response);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    JSONObject jsonResponse = new JSONObject(result);
                    String title = jsonResponse.getString("name");
                    String price = jsonResponse.getString("price");
                    String details = jsonResponse.getString("details");
                    String description = jsonResponse.getString("description");
                    double rating = jsonResponse.getDouble("rating");
                    productTitle.setText(title);
                    productPrice.setText("৳ "+ price);
                    ratingBar.setRating((float) rating);
                    rattingText.setText(String.format("%.1f out of 5 stars", rating));
                    btn_buy.setText("Buy Now ");

                    ViewPagerAdapter adapter = (ViewPagerAdapter) viewPager.getAdapter();
                    if (adapter != null) {
                        DescriptionFragment descriptionFragment = (DescriptionFragment) ((ViewPagerAdapter) viewPager.getAdapter()).getItem(0);
                        descriptionFragment.updateDescription(description);
                        DetailsFragment detailsFragment = (DetailsFragment) ((ViewPagerAdapter) viewPager.getAdapter()).getItem(1);
                        detailsFragment.updateDetails(details);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
