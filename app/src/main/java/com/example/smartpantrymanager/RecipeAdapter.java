package com.example.smartpantrymanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    private final Context context;
    private List<Recipe> recipeList = new ArrayList<>();
    private final OnRecipeClickListener listener;
    private final boolean isSuggestedMode;

    public RecipeAdapter(Context context, OnRecipeClickListener listener, boolean isSuggestedMode) {
        this.context = context;
        this.listener = listener;
        this.isSuggestedMode = isSuggestedMode;
    }

    public void setRecipes(List<Recipe> recipes) {
        this.recipeList = recipes != null ? recipes : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);

        holder.tvTitle.setText(recipe.getTitle());
        holder.tvPrepTime.setText(recipe.getPrepTime() != null ? recipe.getPrepTime() : "15 mins");
        holder.tvCategory.setText(recipe.getCategory() != null ? recipe.getCategory() : "General");

        // Match status styling
        if (isSuggestedMode) {
            holder.cardMatchStatus.setVisibility(View.VISIBLE);
            if (recipe.getMissingIngredientCount() == 0) {
                holder.cardMatchStatus.setCardBackgroundColor(ContextCompat.getColor(context, R.color.status_fresh_bg));
                holder.ivMatchStatusIcon.setImageResource(R.drawable.ic_check_circle);
                holder.ivMatchStatusIcon.setColorFilter(ContextCompat.getColor(context, R.color.status_fresh));
                holder.tvMatchStatusText.setTextColor(ContextCompat.getColor(context, R.color.status_fresh));
                holder.tvMatchStatusText.setText("100% Match • Ready to Cook!");
            } else if (recipe.getMissingIngredientCount() == 1) {
                holder.cardMatchStatus.setCardBackgroundColor(ContextCompat.getColor(context, R.color.status_expiring_bg));
                holder.ivMatchStatusIcon.setImageResource(R.drawable.ic_warning_circle);
                holder.ivMatchStatusIcon.setColorFilter(ContextCompat.getColor(context, R.color.status_expiring));
                holder.tvMatchStatusText.setTextColor(ContextCompat.getColor(context, R.color.status_expiring));
                String missingName = recipe.getMissingIngredientNames().isEmpty() ? "1 item" : recipe.getMissingIngredientNames().get(0);
                holder.tvMatchStatusText.setText("Missing 1 item: " + missingName);
            } else {
                holder.cardMatchStatus.setCardBackgroundColor(ContextCompat.getColor(context, R.color.surface_variant));
                holder.ivMatchStatusIcon.setImageResource(R.drawable.ic_warning_circle);
                holder.ivMatchStatusIcon.setColorFilter(ContextCompat.getColor(context, R.color.text_secondary));
                holder.tvMatchStatusText.setTextColor(ContextCompat.getColor(context, R.color.text_secondary));
                holder.tvMatchStatusText.setText("Missing " + recipe.getMissingIngredientCount() + " ingredients");
            }
        } else {
            // General All Recipes view
            holder.cardMatchStatus.setVisibility(View.VISIBLE);
            int totalIng = recipe.getIngredients() != null ? recipe.getIngredients().size() : 0;
            holder.cardMatchStatus.setCardBackgroundColor(ContextCompat.getColor(context, R.color.primary_light));
            holder.ivMatchStatusIcon.setImageResource(R.drawable.ic_recipe);
            holder.ivMatchStatusIcon.setColorFilter(ContextCompat.getColor(context, R.color.primary_dark));
            holder.tvMatchStatusText.setTextColor(ContextCompat.getColor(context, R.color.primary_dark));
            holder.tvMatchStatusText.setText("Requires " + totalIng + " ingredients • Tap for recipe");
        }

        holder.cardRecipe.setOnClickListener(v -> {
            if (listener != null) listener.onRecipeClick(recipe);
        });
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardRecipe;
        CardView cardRecipeIconBg;
        ImageView ivCategoryIcon, ivMatchStatusIcon;
        TextView tvTitle, tvPrepTime, tvCategory, tvMatchStatusText;
        MaterialCardView cardMatchStatus;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            cardRecipe = itemView.findViewById(R.id.cardRecipe);
            cardRecipeIconBg = itemView.findViewById(R.id.cardRecipeIconBg);
            ivCategoryIcon = itemView.findViewById(R.id.ivRecipeCategoryIcon);
            ivMatchStatusIcon = itemView.findViewById(R.id.ivMatchStatusIcon);
            tvTitle = itemView.findViewById(R.id.tvRecipeTitle);
            tvPrepTime = itemView.findViewById(R.id.tvRecipePrepTime);
            tvCategory = itemView.findViewById(R.id.tvRecipeCategory);
            tvMatchStatusText = itemView.findViewById(R.id.tvMatchStatusText);
            cardMatchStatus = itemView.findViewById(R.id.cardMatchStatus);
        }
    }
}
