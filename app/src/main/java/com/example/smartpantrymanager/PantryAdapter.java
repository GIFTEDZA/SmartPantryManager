package com.example.smartpantrymanager;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    public interface OnItemClickListener {
        void onEditClick(PantryItem item);
        void onDeleteClick(PantryItem item);
    }

    private final Context context;
    private List<PantryItem> itemList = new ArrayList<>();
    private final OnItemClickListener listener;

    public PantryAdapter(Context context, OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setItems(List<PantryItem> items) {
        this.itemList = items != null ? items : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pantry, parent, false);
        return new PantryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder, int position) {
        PantryItem item = itemList.get(position);

        holder.tvName.setText(item.getName());
        String qtyText = formatQuantity(item.getQuantity()) + " " + (item.getUnit() != null ? item.getUnit() : "");
        holder.tvQuantity.setText(qtyText);
        holder.tvCategory.setText(item.getCategory());

        // Category icon & vibrant color badge
        setCategoryStyling(holder, item.getCategory());

        // Expiry Date Badge
        if (item.getExpiryDate() != null && !item.getExpiryDate().trim().isEmpty()) {
            holder.tvExpiry.setVisibility(View.VISIBLE);
            holder.tvExpiry.setText("Exp: " + item.getExpiryDate());
        } else {
            holder.tvExpiry.setVisibility(View.GONE);
        }

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEditClick(item);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    private String formatQuantity(double qty) {
        if (qty == (long) qty) {
            return String.format("%d", (long) qty);
        } else {
            return String.format("%.1f", qty);
        }
    }

    private void setCategoryStyling(PantryViewHolder holder, String category) {
        int iconRes = R.drawable.ic_food_apple;
        int bgColor = ContextCompat.getColor(context, R.color.cat_produce_bg);
        int textColor = ContextCompat.getColor(context, R.color.cat_produce_text);

        if (category == null) category = "";

        switch (category) {
            case "Vegetables & Fruit":
            case "Vegetables":
                iconRes = R.drawable.ic_food_carrot;
                bgColor = ContextCompat.getColor(context, R.color.cat_produce_bg);
                textColor = ContextCompat.getColor(context, R.color.cat_produce_text);
                break;
            case "Dairy & Eggs":
            case "Dairy":
                iconRes = R.drawable.ic_food_cheese;
                bgColor = ContextCompat.getColor(context, R.color.cat_dairy_bg);
                textColor = ContextCompat.getColor(context, R.color.cat_dairy_text);
                break;
            case "Meat & Seafood":
            case "Meat":
                iconRes = R.drawable.ic_food_steak;
                bgColor = ContextCompat.getColor(context, R.color.cat_meat_bg);
                textColor = ContextCompat.getColor(context, R.color.cat_meat_text);
                break;
            case "Bakery":
                iconRes = R.drawable.ic_food_bread;
                bgColor = ContextCompat.getColor(context, R.color.cat_bakery_bg);
                textColor = ContextCompat.getColor(context, R.color.cat_bakery_text);
                break;
            case "Grains & Pasta":
            case "Grains":
                iconRes = R.drawable.ic_food_wheat;
                bgColor = ContextCompat.getColor(context, R.color.cat_grains_bg);
                textColor = ContextCompat.getColor(context, R.color.cat_grains_text);
                break;
            case "Spices & Oils":
            case "Spices":
                iconRes = R.drawable.ic_food_spice;
                bgColor = ContextCompat.getColor(context, R.color.cat_spices_bg);
                textColor = ContextCompat.getColor(context, R.color.cat_spices_text);
                break;
            case "Pantry & Canned":
            default:
                iconRes = R.drawable.ic_food_canned;
                bgColor = ContextCompat.getColor(context, R.color.cat_canned_bg);
                textColor = ContextCompat.getColor(context, R.color.cat_canned_text);
                break;
        }

        holder.ivIcon.setImageResource(iconRes);
        holder.cardIconBg.setCardBackgroundColor(bgColor);
        holder.tvCategory.setTextColor(textColor);
    }

    public static class PantryViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        CardView cardIconBg;
        TextView tvName, tvQuantity, tvCategory, tvExpiry;
        ImageButton btnEdit, btnDelete;

        public PantryViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.ivPantryIcon);
            cardIconBg = itemView.findViewById(R.id.cardIconBg);
            tvName = itemView.findViewById(R.id.tvPantryName);
            tvQuantity = itemView.findViewById(R.id.tvPantryQuantity);
            tvCategory = itemView.findViewById(R.id.tvPantryCategory);
            tvExpiry = itemView.findViewById(R.id.tvPantryExpiry);
            btnEdit = itemView.findViewById(R.id.btnEditPantry);
            btnDelete = itemView.findViewById(R.id.btnDeletePantry);
        }
    }
}
