package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButtonToggleGroup;

import java.util.ArrayList;
import java.util.List;

public class SuggestedRecipesFragment extends Fragment implements RecipeAdapter.OnRecipeClickListener {

    private DatabaseHelper dbHelper;
    private RecyclerView rvSuggested;
    private RecipeAdapter adapter;
    private LinearLayout layoutEmpty;
    private TextView tvEmptyMsg;
    private MaterialButtonToggleGroup toggleMode;

    private List<Recipe> strictlyMatchingRecipes = new ArrayList<>();
    private List<Recipe> almostThereRecipes = new ArrayList<>();
    private boolean isReadyToCookTab = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_suggested_recipes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = new DatabaseHelper(requireContext());

        rvSuggested = view.findViewById(R.id.rvSuggestedRecipes);
        layoutEmpty = view.findViewById(R.id.layoutSuggestedEmpty);
        tvEmptyMsg = view.findViewById(R.id.tvEmptySuggestedText);
        toggleMode = view.findViewById(R.id.toggleSuggestedMode);

        view.findViewById(R.id.btnGoToPantry).setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).switchToPantryTab();
            }
        });

        rvSuggested.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new RecipeAdapter(requireContext(), this, true);
        rvSuggested.setAdapter(adapter);

        toggleMode.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                isReadyToCookTab = (checkedId == R.id.btnTabReady);
                updateListForSelectedTab();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadSuggestedRecipes();
    }

    private void loadSuggestedRecipes() {
        List<PantryItem> pantry = dbHelper.getAllPantryItems();
        List<Recipe> allRecipes = dbHelper.getAllRecipes();

        strictlyMatchingRecipes = IngredientMatcher.getStrictlyMatchingRecipes(allRecipes, pantry);
        almostThereRecipes = IngredientMatcher.getAlmostThereRecipes(allRecipes, pantry);

        updateListForSelectedTab();
    }

    private void updateListForSelectedTab() {
        List<Recipe> displayList = isReadyToCookTab ? strictlyMatchingRecipes : almostThereRecipes;
        adapter.setRecipes(displayList);

        if (displayList.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            rvSuggested.setVisibility(View.GONE);
            if (isReadyToCookTab) {
                tvEmptyMsg.setText("No recipes match your pantry strictly yet!\nAdd more ingredients to get recipes you can cook right now.");
            } else {
                tvEmptyMsg.setText("No recipes missing just 1 ingredient.\nTry adding more pantry items!");
            }
        } else {
            layoutEmpty.setVisibility(View.GONE);
            rvSuggested.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRecipeClick(Recipe recipe) {
        Intent intent = new Intent(requireContext(), RecipeDetailActivity.class);
        intent.putExtra("EXTRA_RECIPE_ID", recipe.getId());
        startActivity(intent);
    }
}
