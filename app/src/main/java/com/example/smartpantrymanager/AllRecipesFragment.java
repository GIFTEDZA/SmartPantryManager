package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AllRecipesFragment extends Fragment implements RecipeAdapter.OnRecipeClickListener {

    private DatabaseHelper dbHelper;
    private RecyclerView rvAllRecipes;
    private RecipeAdapter adapter;
    private EditText etSearch;

    private List<Recipe> allRecipes = new ArrayList<>();
    private List<PantryItem> pantryItems = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_all_recipes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = new DatabaseHelper(requireContext());

        rvAllRecipes = view.findViewById(R.id.rvAllRecipes);
        etSearch = view.findViewById(R.id.etSearchRecipes);

        rvAllRecipes.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new RecipeAdapter(requireContext(), this, false);
        rvAllRecipes.setAdapter(adapter);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterRecipes(s.toString().trim().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadRecipes();
    }

    private void loadRecipes() {
        allRecipes = dbHelper.getAllRecipes();
        pantryItems = dbHelper.getAllPantryItems();

        // Evaluate matching for each recipe to show match badges
        for (Recipe r : allRecipes) {
            IngredientMatcher.evaluateRecipeMatch(r, pantryItems);
        }

        filterRecipes(etSearch.getText().toString().trim().toLowerCase());
    }

    private void filterRecipes(String query) {
        if (query.isEmpty()) {
            adapter.setRecipes(allRecipes);
            return;
        }

        List<Recipe> filtered = new ArrayList<>();
        for (Recipe r : allRecipes) {
            boolean titleMatches = r.getTitle().toLowerCase().contains(query);
            boolean ingMatches = false;
            for (RecipeIngredient ri : r.getIngredients()) {
                if (ri.getIngredientName().toLowerCase().contains(query)) {
                    ingMatches = true;
                    break;
                }
            }

            if (titleMatches || ingMatches) {
                filtered.add(r);
            }
        }
        adapter.setRecipes(filtered);
    }

    @Override
    public void onRecipeClick(Recipe recipe) {
        Intent intent = new Intent(requireContext(), RecipeDetailActivity.class);
        intent.putExtra("EXTRA_RECIPE_ID", recipe.getId());
        startActivity(intent);
    }
}
