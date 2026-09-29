package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class RecipeDetailActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private long recipeId = -1;
    private Recipe currentRecipe;

    private TextView tvTitle, tvPrepTime, tvServings, tvInstructions, tvStatusText;
    private ImageView ivStatusIcon;
    private MaterialCardView cardStatusBanner;
    private RecyclerView rvIngredients;
    private RecipeIngredientAdapter ingredientAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        dbHelper = new DatabaseHelper(this);

        MaterialToolbar toolbar = findViewById(R.id.toolbarRecipeDetail);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        tvTitle = findViewById(R.id.tvDetailTitle);
        tvPrepTime = findViewById(R.id.tvDetailPrepTime);
        tvServings = findViewById(R.id.tvDetailServings);
        tvInstructions = findViewById(R.id.tvDetailInstructions);
        tvStatusText = findViewById(R.id.tvDetailStatusText);
        ivStatusIcon = findViewById(R.id.ivDetailStatusIcon);
        cardStatusBanner = findViewById(R.id.cardDetailMatchStatus);

        rvIngredients = findViewById(R.id.rvRecipeIngredients);
        rvIngredients.setLayoutManager(new LinearLayoutManager(this));
        ingredientAdapter = new RecipeIngredientAdapter(this);
        rvIngredients.setAdapter(ingredientAdapter);

        Intent intent = getIntent();
        if (intent.hasExtra("EXTRA_RECIPE_ID")) {
            recipeId = intent.getLongExtra("EXTRA_RECIPE_ID", -1);
        }

        findViewById(R.id.btnCookedThis).setOnClickListener(v -> cookedRecipe());

        loadRecipeDetails();
    }

    private void loadRecipeDetails() {
        if (recipeId == -1) {
            Toast.makeText(this, "Recipe not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        currentRecipe = dbHelper.getRecipeById(recipeId);
        List<PantryItem> pantry = dbHelper.getAllPantryItems();

        if (currentRecipe != null) {
            tvTitle.setText(currentRecipe.getTitle());
            tvPrepTime.setText(currentRecipe.getPrepTime());
            tvServings.setText("Serves " + currentRecipe.getServings());
            tvInstructions.setText(currentRecipe.getInstructions());

            IngredientMatcher.evaluateRecipeMatch(currentRecipe, pantry);
            ingredientAdapter.setIngredients(currentRecipe.getIngredients(), pantry);

            // Match Banner styling
            if (currentRecipe.getMissingIngredientCount() == 0) {
                cardStatusBanner.setCardBackgroundColor(ContextCompat.getColor(this, R.color.status_fresh_bg));
                ivStatusIcon.setImageResource(R.drawable.ic_check_circle);
                ivStatusIcon.setColorFilter(ContextCompat.getColor(this, R.color.status_fresh));
                tvStatusText.setTextColor(ContextCompat.getColor(this, R.color.status_fresh));
                tvStatusText.setText("100% Match • You have all required ingredients!");
            } else if (currentRecipe.getMissingIngredientCount() == 1) {
                cardStatusBanner.setCardBackgroundColor(ContextCompat.getColor(this, R.color.status_expiring_bg));
                ivStatusIcon.setImageResource(R.drawable.ic_warning_circle);
                ivStatusIcon.setColorFilter(ContextCompat.getColor(this, R.color.status_expiring));
                tvStatusText.setTextColor(ContextCompat.getColor(this, R.color.status_expiring));
                String missing = currentRecipe.getMissingIngredientNames().isEmpty() ? "1 ingredient" : currentRecipe.getMissingIngredientNames().get(0);
                tvStatusText.setText("Almost There • Missing 1 item: " + missing);
            } else {
                cardStatusBanner.setCardBackgroundColor(ContextCompat.getColor(this, R.color.status_expired_bg));
                ivStatusIcon.setImageResource(R.drawable.ic_warning_circle);
                ivStatusIcon.setColorFilter(ContextCompat.getColor(this, R.color.status_expired));
                tvStatusText.setTextColor(ContextCompat.getColor(this, R.color.status_expired));
                tvStatusText.setText("Missing " + currentRecipe.getMissingIngredientCount() + " ingredients in pantry");
            }
        }
    }

    private void cookedRecipe() {
        if (currentRecipe != null && currentRecipe.getIngredients() != null) {
            dbHelper.deductRecipeIngredients(currentRecipe.getIngredients());
            Toast.makeText(this, R.string.msg_cooked_success, Toast.LENGTH_LONG).show();
            loadRecipeDetails(); // Refresh UI
        }
    }
}
