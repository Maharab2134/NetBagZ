/**
 * Copyright (c) 2024 Md. Maharab Hosen. All rights reserved.
 * Author: Md. Maharab Hosen
 */
package com.example.admin.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.admin.Model.User;
import com.example.admin.R;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private final List<User> userList;
    private final Context context;
    private final OnStatusUpdateListener listener;

    public UserAdapter(Context context, List<User> userList, OnStatusUpdateListener listener) {
        this.context = context;
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.nameTextView.setText(user.getName());
        holder.phoneTextView.setText(user.getPhone());
        holder.shopnameTextView.setText(user.getShopname());
        holder.addressTextView.setText(user.getAddress());
        holder.statusTextView.setText(user.getStatus().toUpperCase());
        holder.postalCode.setText(user.getPostalCode());

        holder.phoneTextView.setOnClickListener(v -> makePhoneCall(user.getPhone()));

        holder.statusTextView.setOnClickListener(v -> {
            String newStatus = user.getStatus().equalsIgnoreCase("Active") ? "Inactive" : "Active";
            listener.updateUserStatus(user.getId(), newStatus);
            user.setStatus(newStatus);
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    private void makePhoneCall(String phoneNumber) {
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "Call permission is required to make this call", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        context.startActivity(intent);
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, phoneTextView, shopnameTextView, addressTextView, statusTextView, postalCode;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.userName);
            phoneTextView = itemView.findViewById(R.id.userPhone);
            shopnameTextView = itemView.findViewById(R.id.userShopname);
            addressTextView = itemView.findViewById(R.id.userAddress);
            statusTextView = itemView.findViewById(R.id.Status);
            postalCode = itemView.findViewById(R.id.postalCode);
        }
    }

    public interface OnStatusUpdateListener {
        void updateUserStatus(String userId, String newStatus);
    }
}
