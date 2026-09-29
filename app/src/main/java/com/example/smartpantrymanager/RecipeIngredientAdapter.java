package com.example.smartpantrymanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RecipeIngredientAdapter extends RecyclerView.Adapter<RecipeIngredientAdapter.RIViewHolder> {

    private final Context context;
    private List<RecipeIngredient> ingredients = new ArrayList<>();
    private List<PantryItem> userPantry = new ArrayList<>();

    public RecipeIngredientAdapter(Context context) {
        this.context = context;
    }

    public void setIngredients(List<RecipeIngredient> ingredients, List<PantryItem> userPantry) {
        this.ingredients = ingredients != null ? ingredients : new ArrayList<>();
        this.userPantry = userPantry != null ? userPantry : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RIViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recipe_ingredient, parent, false);
        return new RIViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RIViewHolder holder, int position) {
        RecipeIngredient req = ingredients.get(position);

        holder.tvName.setText(req.getIngredientName());
        String reqQtyStr = formatQty(req.getQuantity()) + " " + (req.getUnit() != null ? req.getUnit() : "");
        holder.tvRequiredQty.setText(reqQtyStr);

        // Check if user has item in pantry
        PantryItem matchedPantryItem = null;
        for (PantryItem item : userPantry) {
            if (IngredientMatcher.isIngredientNameMatch(req.getIngredientName(), item.getName())) {
                matchedPantryItem = item;
                break;
            }
        }

        if (matchedPantryItem != null) {
            double reqQtyBase = IngredientMatcher.convertToBaseUnit(req.getQuantity(), req.getUnit());
            double pantryQtyBase = IngredientMatcher.convertToBaseUnit(matchedPantryItem.getQuantity(), matchedPantryItem.getUnit());

            if (pantryQtyBase >= reqQtyBase) {
                // Completely satisfied
                holder.ivStatus.setImageResource(R.drawable.ic_check_circle);
                holder.ivStatus.setColorFilter(ContextCompat.getColor(context, R.color.status_fresh));
                holder.tvNote.setVisibility(View.VISIBLE);
                holder.tvNote.setTextColor(ContextCompat.getColor(context, R.color.status_fresh));
                holder.tvNote.setText("(In Pantry: " + formatQty(matchedPantryItem.getQuantity()) + " " + matchedPantryItem.getUnit() + ")");
            } else {
                // Partial quantity
                holder.ivStatus.setImageResource(R.drawable.ic_warning_circle);
                holder.ivStatus.setColorFilter(ContextCompat.getColor(context, R.color.status_expiring));
                holder.tvNote.setVisibility(View.VISIBLE);
                holder.tvNote.setTextColor(ContextCompat.getColor(context, R.color.status_expiring));
                holder.tvNote.setText("(Need more: have " + formatQty(matchedPantryItem.getQuantity()) + " " + matchedPantryItem.getUnit() + ")");
            }
        } else {
            // Missing ingredient
            holder.ivStatus.setImageResource(R.drawable.ic_warning_circle);
            holder.ivStatus.setColorFilter(ContextCompat.getColor(context, R.color.status_expired));
            holder.tvNote.setVisibility(View.VISIBLE);
            holder.tvNote.setTextColor(ContextCompat.getColor(context, R.color.status_expired));
            holder.tvNote.setText("(Missing in pantry)");
        }
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    private String formatQty(double qty) {
        if (qty == (long) qty) {
            return String.format("%d", (long) qty);
        } else {
            return String.format("%.1f", qty);
        }
    }

    public static class RIViewHolder extends RecyclerView.ViewHolder {
        ImageView ivStatus;
        TextView tvName, tvRequiredQty, tvNote;

        public RIViewHolder(@NonNull View itemView) {
            super(itemView);
            ivStatus = itemView.findViewById(R.id.ivIngredientStatus);
            tvName = itemView.findViewById(R.id.tvIngredientName);
            tvRequiredQty = itemView.findViewById(R.id.tvIngredientRequiredQty);
            tvNote = itemView.findViewById(R.id.tvPantryStatusNote);
        }
    }
}
