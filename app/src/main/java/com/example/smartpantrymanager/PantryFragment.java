package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class PantryFragment extends Fragment implements PantryAdapter.OnItemClickListener {

    private DatabaseHelper dbHelper;
    private RecyclerView rvPantry;
    private PantryAdapter adapter;
    private LinearLayout layoutEmpty;
    private EditText etSearch;
    private ChipGroup chipGroupCategory;

    private List<PantryItem> allPantryItems = new ArrayList<>();
    private String selectedCategory = "All";
    private String searchQuery = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pantry, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = new DatabaseHelper(requireContext());

        rvPantry = view.findViewById(R.id.rvPantry);
        layoutEmpty = view.findViewById(R.id.layoutPantryEmpty);
        etSearch = view.findViewById(R.id.etSearchPantry);
        chipGroupCategory = view.findViewById(R.id.chipGroupPantryCategory);

        FloatingActionButton fabAdd = view.findViewById(R.id.fabAddPantry);
        view.findViewById(R.id.btnEmptyAdd).setOnClickListener(v -> openAddIngredientActivity());
        fabAdd.setOnClickListener(v -> openAddIngredientActivity());

        rvPantry.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new PantryAdapter(requireContext(), this);
        rvPantry.setAdapter(adapter);

        setupSearchAndFilter();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadPantryItems();
    }

    private void loadPantryItems() {
        allPantryItems = dbHelper.getAllPantryItems();
        applyFilterAndSearch();
    }

    private void setupSearchAndFilter() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchQuery = s.toString().trim().toLowerCase();
                applyFilterAndSearch();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        chipGroupCategory.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                selectedCategory = "All";
            } else {
                int checkedId = checkedIds.get(0);
                if (checkedId == R.id.chipProduce) selectedCategory = "Vegetables & Fruit";
                else if (checkedId == R.id.chipDairy) selectedCategory = "Dairy & Eggs";
                else if (checkedId == R.id.chipMeat) selectedCategory = "Meat & Seafood";
                else if (checkedId == R.id.chipBakery) selectedCategory = "Bakery";
                else if (checkedId == R.id.chipGrains) selectedCategory = "Grains & Pasta";
                else if (checkedId == R.id.chipSpices) selectedCategory = "Spices & Oils";
                else if (checkedId == R.id.chipCanned) selectedCategory = "Pantry & Canned";
                else selectedCategory = "All";
            }
            applyFilterAndSearch();
        });
    }

    private void applyFilterAndSearch() {
        List<PantryItem> filteredList = new ArrayList<>();
        for (PantryItem item : allPantryItems) {
            boolean matchesCategory = selectedCategory.equals("All") || item.getCategory().equalsIgnoreCase(selectedCategory);
            boolean matchesSearch = searchQuery.isEmpty() || item.getName().toLowerCase().contains(searchQuery);

            if (matchesCategory && matchesSearch) {
                filteredList.add(item);
            }
        }

        adapter.setItems(filteredList);

        if (filteredList.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            rvPantry.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            rvPantry.setVisibility(View.VISIBLE);
        }
    }

    private void openAddIngredientActivity() {
        Intent intent = new Intent(requireContext(), AddEditIngredientActivity.class);
        startActivity(intent);
    }

    @Override
    public void onEditClick(PantryItem item) {
        Intent intent = new Intent(requireContext(), AddEditIngredientActivity.class);
        intent.putExtra("EXTRA_INGREDIENT_ID", item.getId());
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(PantryItem item) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Ingredient")
                .setMessage("Are you sure you want to remove '" + item.getName() + "' from your pantry?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    dbHelper.deletePantryItem(item.getId());
                    Toast.makeText(requireContext(), "Ingredient deleted", Toast.LENGTH_SHORT).show();
                    loadPantryItems();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
