/**
 * Copyright (c) 2024 Md. Maharab Hosen. All rights reserved.
 * Author: Md. Maharab Hosen
 */
package com.example.admin.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.admin.Model.Order;
import com.example.admin.R;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> orders;
    private Context context;

    public OrderAdapter(Context context, List<Order> orders) {
        this.context = context;
        this.orders = orders;
    }


    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.userName.setText(order.getUserName());
        holder.shopName.setText(order.getShopName());
        holder.invoiceId.setText(order.getInvoiceId());
        holder.totalAmount.setText(order.getTotalAmount());
        holder.paymentMethod.setText(order.getPaymentMethod());
        holder.productSize.setText(order.getProductSize());
        holder.productQuantity.setText(String.valueOf(order.getProductQuantity()));
        holder.productTitle.setText(order.getProductTitle());
        holder.orderStatus.setText(order.getStatus());
        holder.address.setText(order.getAddress());

        holder.imageView.setOnClickListener(v -> openGoogleMaps(order.getAddress()));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }
    private void openGoogleMaps(String address) {
        String googleSearchUrl = "https://www.google.com/maps/search/" + Uri.encode(address);
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(googleSearchUrl)));
    }
    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView userName, shopName, invoiceId, totalAmount, paymentMethod, productQuantity, productSize, productTitle, orderStatus, address;
        ImageView imageView;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            shopName = itemView.findViewById(R.id.shopName);
            invoiceId = itemView.findViewById(R.id.invoiceId);
            totalAmount = itemView.findViewById(R.id.totalAmount);
            paymentMethod = itemView.findViewById(R.id.paymentMethod);
            productQuantity = itemView.findViewById(R.id.productQuantity);
            productTitle = itemView.findViewById(R.id.productTitle);
            orderStatus = itemView.findViewById(R.id.orderStatus);
            productSize = itemView.findViewById(R.id.productSize);
            address = itemView.findViewById(R.id.address);
            imageView = itemView.findViewById(R.id.addressIcon);
        }
    }
}
