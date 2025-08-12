/**
 * Copyright (c) 2024 Md. Maharab Hosen. All rights reserved.
 * Author: Md. Maharab Hosen
 */
package com.example.admin.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.admin.Model.Product;
import com.example.admin.R;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private ArrayList<Product> productList;
    private OnProductClickListener listener;

    // Interface for product click handling
    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    // Constructor
    public ProductAdapter(ArrayList<Product> productList, OnProductClickListener listener) {
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        // Set product details in the RecyclerView item
        holder.textViewProductName.setText(product.getName());
        holder.textViewProductPrice.setText("Price: " + product.getPrice());
        holder.textViewProductRating.setText("Rating: " + product.getRating());

        // Handle item click
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProductClick(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    // ViewHolder Class
    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewProductName, textViewProductPrice, textViewProductRating;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewProductName = itemView.findViewById(R.id.textViewProductName);
            textViewProductPrice = itemView.findViewById(R.id.textViewProductPrice);
            textViewProductRating = itemView.findViewById(R.id.textViewProductRating);
        }
    }
}
