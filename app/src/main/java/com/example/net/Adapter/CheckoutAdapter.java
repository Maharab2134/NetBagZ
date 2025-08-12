package com.example.net.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.net.Activity.CheckOutPage;
import com.example.net.R;
import com.example.net.Model.CartItem;

import java.util.List;

public class CheckoutAdapter extends RecyclerView.Adapter<CheckoutAdapter.CartViewHolder> {

    private List<CartItem> cartItems;
    private OnQuantityChangeListener onQuantityChangeListener;

    public CheckoutAdapter(List<CartItem> cartItems, OnQuantityChangeListener onQuantityChangeListener) {
        this.cartItems = cartItems;
        this.onQuantityChangeListener = onQuantityChangeListener;
    }

    @Override
    public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CartViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        holder.cartProductTitle.setText(cartItem.getTitle());

        String cleanedPriceString = cartItem.getPrice().replaceAll("[^\\d.]", "");
        double productPrice = Double.parseDouble(cleanedPriceString);

        int productQuantity = cartItem.getQuantity();
        double total = productPrice * productQuantity;

        holder.cartProductPrice.setText("৳ " + productPrice);
        holder.productQuantity.setText(String.valueOf(productQuantity));
        holder.feeEachItemPrice.setText("৳ " + total);
        holder.cartProductImage.setImageResource(cartItem.getImageResId());


        holder.productQuantity.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), CheckOutPage.class);
            intent.putExtra("productQuantity", productQuantity);
            v.getContext().startActivity(intent);
        });

        holder.cartProductTitle.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), CheckOutPage.class);
            intent.putExtra("cartProductTitle", cartItem.getTitle());
            v.getContext().startActivity(intent);
        });

        holder.incrementQuantity.setOnClickListener(v -> {
            cartItem.setQuantity(cartItem.getQuantity() + 1);
            notifyItemChanged(position);
            onQuantityChangeListener.onQuantityChanged(); // Notify activity to update total
        });

        holder.decrementQuantity.setOnClickListener(v -> {
            if (cartItem.getQuantity() > 1) {
                cartItem.setQuantity(cartItem.getQuantity() - 1);
                notifyItemChanged(position);
                onQuantityChangeListener.onQuantityChanged(); // Notify activity to update total
            }
        });
        if (cartItem.getSize() != null && !cartItem.getSize().isEmpty()) {
            holder.sizeText.setText("Size: " + cartItem.getSize());
        } else {
            holder.sizeText.setText("Size not selected");
        }
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView cartProductTitle, cartProductPrice, feeEachItemPrice, productQuantity, incrementQuantity, decrementQuantity, sizeText;
        ImageView cartProductImage;

        public CartViewHolder(View itemView) {
            super(itemView);
            cartProductImage = itemView.findViewById(R.id.pic);
            cartProductTitle = itemView.findViewById(R.id.titleTxt);
            cartProductPrice = itemView.findViewById(R.id.totalEachItem);
            feeEachItemPrice = itemView.findViewById(R.id.feeEachItem);
            productQuantity = itemView.findViewById(R.id.numberItemText);
            incrementQuantity = itemView.findViewById(R.id.plusCartbtn);
            decrementQuantity = itemView.findViewById(R.id.minusCartBtn);
            sizeText = itemView.findViewById(R.id.sizeText);
        }
    }

    // Callback interface for quantity changes
    public interface OnQuantityChangeListener {
        void onQuantityChanged();
    }
}
