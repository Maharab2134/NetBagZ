package com.example.net.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.net.R;
import java.util.ArrayList;

public class SizeAdapter extends RecyclerView.Adapter<SizeAdapter.SizeViewHolder> {

    private ArrayList<String> sizes;
    private int selectedPosition = -1;
    private SizeSelectedListener sizeSelectedListener;

    public SizeAdapter(ArrayList<String> sizes, SizeSelectedListener sizeSelectedListener) {
        this.sizes = sizes;
        this.sizeSelectedListener = sizeSelectedListener;
    }

    @NonNull
    @Override
    public SizeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_size, parent, false);
        return new SizeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SizeViewHolder holder, int position) {
        String size = sizes.get(position);
        holder.textView9.setText(size);

        if (selectedPosition == position) {
            holder.sizeLayout.setBackgroundResource(R.drawable.size_seleceted);
        } else {
            holder.sizeLayout.setBackgroundResource(R.drawable.size_unselected);
        }

        holder.itemView.setOnClickListener(v -> {
            notifyItemChanged(selectedPosition);
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(selectedPosition);

            if (sizeSelectedListener != null) {
                sizeSelectedListener.onSizeSelected(size);
            }
        });
    }

    @Override
    public int getItemCount() {
        return sizes.size();
    }

    public static class SizeViewHolder extends RecyclerView.ViewHolder {
        public View sizeLayout;
        TextView textView9;

        public SizeViewHolder(View itemView) {
            super(itemView);
            sizeLayout = itemView.findViewById(R.id.sizeLayout);
            textView9 = itemView.findViewById(R.id.textView9);
        }
    }

    public interface SizeSelectedListener {
        void onSizeSelected(String size);
    }
}
