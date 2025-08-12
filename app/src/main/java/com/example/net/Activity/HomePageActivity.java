package com.example.net.Activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.airbnb.lottie.LottieImageAsset;
import com.example.net.Config.Config;
import com.example.net.R;
import com.example.net.Adapter.ImageSlideAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

public class HomePageActivity extends AppCompatActivity {

    public TextView status, name, productName1, productName2, productName3, productName4, productName5, productName6, productName7, productName8;
    public TextView price1, price2, price3, price4, price5, price6, price7, price8;
    public ImageView lottieImageAsset;
    private ViewPager viewPager;
    private ImageSlideAdapter imageSlideAdapter;
    private Handler handler;
    private Runnable runnable;
    public CardView cardView1, cardView2, cardView3, cardView4, cardView5, cardView6, cardView7, cardView8;
    private BottomNavigationView bottomNavigation;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String shopName, userName;
    private Config config;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);

        lottieImageAsset = findViewById(R.id.profile);

        cardView1 = findViewById(R.id.cardView1);
        cardView2 = findViewById(R.id.cardView2);
        cardView3 = findViewById(R.id.cardView3);
        cardView4 = findViewById(R.id.cardView4);
        cardView5 = findViewById(R.id.cardView5);
        cardView6 = findViewById(R.id.cardView6);
        cardView7 = findViewById(R.id.cardView7);
        cardView8 = findViewById(R.id.cardView8);


        name = findViewById(R.id.name);
        status = findViewById(R.id.status);

        productName1 = findViewById(R.id.product_name1);
        productName2 = findViewById(R.id.product_name2);
        productName3 = findViewById(R.id.product_name3);
        productName4 = findViewById(R.id.product_name4);
        productName5 = findViewById(R.id.product_name5);
        productName6 = findViewById(R.id.product_name6);
        productName7 = findViewById(R.id.product_name7);
        productName8 = findViewById(R.id.product_name8);

        price1 = findViewById(R.id.product_price1);
        price2 = findViewById(R.id.product_price2);
        price3 = findViewById(R.id.product_price3);
        price4 = findViewById(R.id.product_price4);
        price5 = findViewById(R.id.product_price5);
        price6 = findViewById(R.id.product_price6);
        price7 = findViewById(R.id.product_price7);
        price8 = findViewById(R.id.product_price8);


        bottomNavigation = findViewById(R.id.bottom_navigation);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userName = sharedPreferences.getString("USER_NAME", null);
        shopName = sharedPreferences.getString("SHOP_NAME", null);
        name.setText(userName != null ? "Welcome, " + userName : "Welcome!");


        lottieImageAsset.setOnClickListener(v -> {
            Intent intent = new Intent(HomePageActivity .this,AboutMeActivity.class);
            startActivity(intent);
            finish();
        });

        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            Intent intent;
            if (item.getItemId() == R.id.home) {
                return true;
            } else if (item.getItemId() == R.id.cart) {
                intent = new Intent(HomePageActivity.this, CartPageActivity.class);
                intent.putExtra("USER_NAME", userName);
                startActivity(intent);
                return true;
            } else if (item.getItemId() == R.id.profile) {
                intent = new Intent(HomePageActivity.this, ProfileActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });

        setStatusBasedOnTime();

        viewPager = findViewById(R.id.view_pager);
        int[] imageIds = {
                R.drawable.s1,
                R.drawable.s3,
                R.drawable.s6,
                R.drawable.s7,
                R.drawable.s5,
                R.drawable.s2,
                R.drawable.s4,
        };

        imageSlideAdapter = new ImageSlideAdapter(this, imageIds);
        viewPager.setAdapter(imageSlideAdapter);

        handler = new Handler();
        runnable = new Runnable() {
            int currentPage = 0;

            @Override
            public void run() {
                if (currentPage == imageIds.length) {
                    currentPage = 0;
                }
                viewPager.setCurrentItem(currentPage++, true);
                handler.postDelayed(this, 4000);
            }
        };
        handler.postDelayed(runnable, 4000);

        loadProductDetails();

        // Update card click listeners to pass only the image resource ID
        cardView1.setOnClickListener(v -> openProductPage(1, R.drawable.p1));
        cardView2.setOnClickListener(v -> openProductPage(2, R.drawable.p2));
        cardView3.setOnClickListener(v -> openProductPage(3, R.drawable.p3));
        cardView4.setOnClickListener(v -> openProductPage(4, R.drawable.p4));
        cardView5.setOnClickListener(v -> openProductPage(5, R.drawable.p5));
        cardView6.setOnClickListener(v -> openProductPage(6, R.drawable.p6));
        cardView7.setOnClickListener(v -> openProductPage(7, R.drawable.p7));
        cardView8.setOnClickListener(v -> openProductPage(8, R.drawable.p8));



        swipeRefreshLayout.setOnRefreshListener(() -> {
            refreshPage();
        });

        config = new Config();

    }
    private void refreshPage() {
        new Handler().postDelayed(() -> {
            swipeRefreshLayout.setRefreshing(false);
        }, 1500);
    }

    private void loadProductDetails() {
        for (int id = 1; id <= 8; id++) {
            new FetchProductDetailsTask(id).execute();
        }
    }

    // Updated method to only accept the image resource ID
    private void openProductPage(int productId, int imageResId) {
        Intent intent = new Intent(HomePageActivity.this, ProDetailsActivity.class);
        intent.putExtra("product_id", productId);
        intent.putExtra("product_image", imageResId);
        intent.putExtra("USER_NAME", userName);
        intent.putExtra("SHOP_NAME", shopName);
        startActivity(intent);
    }


    private class FetchProductDetailsTask extends AsyncTask<Void, Void, JSONObject> {
        private final int productId;

        FetchProductDetailsTask(int productId) {
            this.productId = productId;
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            String baseUrl = Config.Base_Url;
            String urlString = baseUrl + "/get_product.php?id=" + productId;
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                StringBuilder jsonResult = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonResult.append(line);
                }
                reader.close();
                return new JSONObject(jsonResult.toString());
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (jsonObject != null) {
                try {
                    String productName = jsonObject.getString("name");
                    String productPrice = jsonObject.getString("price");

                    if (productId == 1) {
                        productName1.setText(productName);
                        price1.setText("৳ "+ productPrice);
                    } else if (productId == 2) {
                        productName2.setText(productName);
                        price2.setText("৳ "+ productPrice);
                    } else if (productId == 3) {
                        productName3.setText(productName);
                        price3.setText("৳ "+ productPrice);
                    } else if (productId == 4) {
                        productName4.setText(productName);
                        price4.setText("৳ " +productPrice);
                    }else if (productId == 5) {
                        productName5.setText(productName);
                        price5.setText("৳ " +productPrice);
                    }else if (productId == 6) {
                        productName6.setText(productName);
                        price6.setText("৳ " +productPrice);
                    }else if (productId == 7) {
                        productName7.setText(productName);
                        price7.setText("৳ " +productPrice);
                    }else if (productId == 8) {
                        productName8.setText(productName);
                        price8.setText("৳ " +productPrice);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String userName = sharedPreferences.getString("USER_NAME", null);
        name.setText(userName != null ? "Welcome, " + userName : "Welcome!");
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(HomePageActivity.this)
                .setTitle("Do you want to exit?")
                .setMessage("If you click Yes, you will be logged out and out of the app.")
                .setCancelable(false)
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Yes", (dialog, which) -> {
                    SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear(); // Clears all saved data
                    editor.apply();

                    Intent intent = new Intent(HomePageActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    super.onBackPressed();
                })
                .show();
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
}
