package com.example.net.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.net.R;
import com.example.net.Adapter.CheckoutAdapter;
import com.example.net.Model.CartItem;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CheckOutPage extends AppCompatActivity implements CheckoutAdapter.OnQuantityChangeListener {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationClient;


    private TextView totalFeeText, deliveryTxt, mainTotal, addressText, storeText, textView21;
    private Button button;
    private RecyclerView recyclerView;
    private CheckoutAdapter cartAdapter;
    private ImageView imageView1,choiceMethode;
    private ArrayList<CartItem> cartItems = new ArrayList<>();
    private String selectedPaymentMethod = "";
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_page);

        totalFeeText = findViewById(R.id.totalFeeText);
        deliveryTxt = findViewById(R.id.deliveryTxt);
        mainTotal = findViewById(R.id.totalTxt);
        addressText = findViewById(R.id.addressText);
        storeText = findViewById(R.id.storeText);
        textView21 = findViewById(R.id.textView21);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        imageView1 = findViewById(R.id.backBtn);
        choiceMethode = findViewById(R.id.imageView7);
        button = findViewById(R.id.button2);
        recyclerView = findViewById(R.id.cartView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        imageView1.setOnClickListener(v -> onBackPressed());

        Intent intent = getIntent();
        ArrayList<CartItem> productsList = (ArrayList<CartItem>) intent.getSerializableExtra("cart_items");

        if (productsList != null) {
            cartItems.addAll(productsList);
        } else {
            String productTitle = intent.getStringExtra("product_title");
            String productPrice = intent.getStringExtra("product_price");
            String productSize = intent.getStringExtra("product_size");
            int productImageResId = intent.getIntExtra("product_image_res_id", -1);

            if (productTitle != null && productPrice != null && productImageResId != -1 && productSize != null) {
                CartItem cartItem = new CartItem(productTitle, productPrice, productImageResId, productSize, 1);
                cartItems.add(cartItem);
            } else {
                Toast.makeText(this, "Error loading cart item", Toast.LENGTH_SHORT).show();
            }
        }

        cartAdapter = new CheckoutAdapter(cartItems, this);
        recyclerView.setAdapter(cartAdapter);
        updateTotal();

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String userShopName = sharedPreferences.getString("USER_SHOPNAME","No shop name available");
        userName = sharedPreferences.getString("USER_NAME", null);


        choiceMethode.setOnClickListener(v -> {
            String[] paymentOptions = {"BKash", "Nagad", "Cash on Delivery"};

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Payment Method");

            builder.setItems(paymentOptions, (dialog, which) -> {
                selectedPaymentMethod = paymentOptions[which];
                textView21.setText(selectedPaymentMethod);
            });
            builder.show();
        });
        storeText.setText(userShopName);

        button.setOnClickListener(v -> {
            if (!selectedPaymentMethod.isEmpty()) {
                int productQuantity = cartItems.get(0).getQuantity();
                String productTitle = cartItems.get(0).getTitle();
                String productSize = cartItems.get(0).getSize();
                String address = addressText.getText().toString().trim();  // Get the address from the TextView

                switch (selectedPaymentMethod) {
                    case "Cash on Delivery":
                        showSuccessDialog();
                        break;
                    case "BKash":
                        Intent intentBkash = new Intent(CheckOutPage.this, bKashPaymentActivity.class);
                        intentBkash.putExtra("paymentMethod", "BKash");
                        intentBkash.putExtra("totalAmount", mainTotal.getText().toString());
                        intentBkash.putExtra("USER_NAME", userName);
                        intentBkash.putExtra("USER_SHOPNAME", userShopName);
                        intentBkash.putExtra("selectedPaymentMethod", selectedPaymentMethod);
                        intentBkash.putExtra("productQuantity", productQuantity);
                        intentBkash.putExtra("product_size",productSize);
                        intentBkash.putExtra("productTitle", productTitle);
                        intentBkash.putExtra("address", address.isEmpty() ? "No Address Provided" : address);
                        startActivity(intentBkash);
                        break;
                    case "Nagad":
                        Intent intentNagad = new Intent(CheckOutPage.this, NagadPaymentActivity.class);
                        intentNagad.putExtra("paymentMethod", "Nagad");
                        intentNagad.putExtra("totalAmount", mainTotal.getText().toString());
                        intentNagad.putExtra("USER_NAME", userName);
                        intentNagad.putExtra("USER_SHOPNAME", userShopName);
                        intentNagad.putExtra("selectedPaymentMethod", selectedPaymentMethod);
                        intentNagad.putExtra("productQuantity", productQuantity);
                        intentNagad.putExtra("product_size",productSize);
                        intentNagad.putExtra("productTitle", productTitle);
                        intentNagad.putExtra("address", address.isEmpty() ? "No Address Provided" : address);
                        startActivity(intentNagad);
                        break;
                }
            } else {
                Toast.makeText(CheckOutPage.this, "Please select a payment method", Toast.LENGTH_SHORT).show();
            }
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        addressText.setOnClickListener(v -> requestLocationPermission());

    }
    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Permission already granted, get the location
            getUserLocation();
        } else {
            // Request location permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void getUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission has not been granted, request permissions.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        // Permission is granted; proceed to get the location.
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        updateAddressText(location);
                    } else {
                        Toast.makeText(this, "Unable to get location", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to get location: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }


    private void updateAddressText(Location location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && !addresses.isEmpty()) {
                String fullAddress = addresses.get(0).getAddressLine(0);
                addressText.setText(fullAddress);
            } else {
                Toast.makeText(this, "Unable to find address", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error retrieving address", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getUserLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateTotal() {
        double total = 0.0;
        for (CartItem item : cartItems) {
            double productPrice = Double.parseDouble(item.getPrice().replaceAll("[^\\d.]", ""));
            total += productPrice * item.getQuantity();
        }

        double deliveryFee = 70.0;
        double tax = total * 0.1;
        double finalTotal = total + deliveryFee + tax;

        totalFeeText.setText("৳ " + total);
        deliveryTxt.setText("৳ " + deliveryFee);
        mainTotal.setText("৳ " + finalTotal);
    }
    private void showSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Order Successful");
        builder.setMessage("Your order has been placed successfully. You will receive your items soon.");
        builder.setPositiveButton("OK", (dialog, which) -> {
            finish();
        });
        builder.setCancelable(false);
        builder.show();
    }

    @Override
    public void onQuantityChanged() {
        updateTotal();
    }
}