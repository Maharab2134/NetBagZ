package com.example.net.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.net.Model.Order;
import com.example.net.R;
import java.util.List;
import android.os.Handler;
import android.os.Looper;
import java.util.Date;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.OrderViewHolder> {
    private List<Order> orderList;

    public CartAdapter(List<Order> orderList) {
        this.orderList = orderList;
    }

    @Override
    public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.productTitle.setText(order.getProductTitle());
        holder.totalAmount.setText("Total: ৳" + order.getTotalAmount());
        holder.paymentMethod.setText("Payment: " + order.getPaymentMethod());

        long currentTime = new Date().getTime();
        long orderTime = order.getOrderTime();
        long fourHoursInMillis = 2 * 60 * 60 * 1000;

        if ("Success".equalsIgnoreCase(order.getOrderStatus())) {
            if ((currentTime - orderTime) < fourHoursInMillis) {
                holder.orderStatus.setText("Out for Delivery");
            } else {
                holder.orderStatus.setText("Delivered");
            }
        } else {
            holder.orderStatus.setText("Status: " + order.getOrderStatus());
        }

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if ("Success".equalsIgnoreCase(order.getOrderStatus()) &&
                    (currentTime - orderTime) >= fourHoursInMillis) {
                holder.orderStatus.setText("Delivered");
            }
        }, fourHoursInMillis - (currentTime - orderTime));
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView productTitle, totalAmount, paymentMethod, orderStatus;

        public OrderViewHolder(View itemView) {
            super(itemView);
            productTitle = itemView.findViewById(R.id.productTitle);
            totalAmount = itemView.findViewById(R.id.totalAmount);
            paymentMethod = itemView.findViewById(R.id.paymentMethod);
            orderStatus = itemView.findViewById(R.id.orderStatus);
        }
    }
}


// If the order is marked "Success", check if 4 hours have passed since the order time.
// If less than 4 hours, show "Out for Delivery".
// If 4+ hours have passed, update the status to "Delivered".
// Automatically updates status after 4 hours using a Handler.
// This ensures the order status dynamically updates at the right time.