/**
 * Copyright (c) 2024 Md. Maharab Hosen. All rights reserved.
 * Author: Md. Maharab Hosen
 */
package com.example.admin.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.admin.Model.Payment;
import com.example.admin.R;

import java.util.List;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.PaymentViewHolder> {

    private List<Payment> paymentList;

    public PaymentAdapter(List<Payment> paymentList) {
        this.paymentList = paymentList;
    }

    @Override
    public PaymentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.payment_item, parent, false);
        return new PaymentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PaymentViewHolder holder, int position) {
        Payment payment = paymentList.get(position);
        holder.userName.setText(payment.getUserName());
        holder.userShopName.setText(payment.getUserShopName());
        holder.totalAmount.setText(payment.getTotalAmount());
        holder.paymentMethod.setText(payment.getPaymentMethod());
        holder.transactionId.setText(payment.getTransactionId());
        holder.invoiceId.setText(payment.getInvoiceId());
        holder.status.setText(payment.getStatus());

        holder.imageView.setOnClickListener(v -> {
            String paymentId = payment.getId();
            String currentStatus = payment.getStatus();

            String newStatus;
            if (currentStatus.equals("Success")) {
                newStatus = "Pending";
            } else if (currentStatus.equals("Pending")) {
                newStatus = "Cancel";
            } else {
                newStatus = "Success";
            }

            new UpdatePaymentStatusTask(v.getContext()).execute(paymentId, newStatus);

        });
    }

    @Override
    public int getItemCount() {
        return paymentList.size();
    }

    public static class PaymentViewHolder extends RecyclerView.ViewHolder {
        TextView userName, userShopName, totalAmount, paymentMethod, transactionId, invoiceId, status;
        ImageView imageView;

        public PaymentViewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            userShopName = itemView.findViewById(R.id.userShopName);
            totalAmount = itemView.findViewById(R.id.totalAmount);
            paymentMethod = itemView.findViewById(R.id.paymentMethod);
            transactionId = itemView.findViewById(R.id.transactionId);
            invoiceId = itemView.findViewById(R.id.invoiceId);
            status = itemView.findViewById(R.id.status);
            imageView = itemView.findViewById(R.id.optionsIcon);
        }
    }
}