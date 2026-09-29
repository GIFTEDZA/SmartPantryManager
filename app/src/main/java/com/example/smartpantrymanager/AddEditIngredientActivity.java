package com.example.smartpantrymanager;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddEditIngredientActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private TextInputLayout tilName, tilQuantity;
    private TextInputEditText etName, etQuantity, etExpiryDate;
    private Spinner spinnerUnit, spinnerCategory;

    private long ingredientId = -1;
    private Calendar calendar = Calendar.getInstance();
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    private final String[] units = new String[]{"pcs", "g", "kg", "ml", "L", "cups", "tbsp", "tsp", "slices", "cans", "pack"};
    private final String[] categories = new String[]{
            "Vegetables & Fruit", "Dairy & Eggs", "Meat & Seafood",
            "Bakery", "Grains & Pasta", "Spices & Oils", "Pantry & Canned"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);

        dbHelper = new DatabaseHelper(this);

        MaterialToolbar toolbar = findViewById(R.id.toolbarAddEdit);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        tilName = findViewById(R.id.tilIngredientName);
        tilQuantity = findViewById(R.id.tilQuantity);
        etName = findViewById(R.id.etIngredientName);
        etQuantity = findViewById(R.id.etQuantity);
        etExpiryDate = findViewById(R.id.etExpiryDate);
        spinnerUnit = findViewById(R.id.spinnerUnit);
        spinnerCategory = findViewById(R.id.spinnerCategory);

        // Spinners Setup
        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, units);
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnit.setAdapter(unitAdapter);

        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(catAdapter);

        // DatePicker for Expiry Date
        etExpiryDate.setOnClickListener(v -> showDatePicker());

        // Preset Date Buttons
        findViewById(R.id.btnPreset3Days).setOnClickListener(v -> setExpiryPreset(3));
        findViewById(R.id.btnPreset1Week).setOnClickListener(v -> setExpiryPreset(7));
        findViewById(R.id.btnPreset1Month).setOnClickListener(v -> setExpiryPreset(30));

        // Check if editing existing ingredient
        Intent intent = getIntent();
        if (intent.hasExtra("EXTRA_INGREDIENT_ID")) {
            ingredientId = intent.getLongExtra("EXTRA_INGREDIENT_ID", -1);
            if (toolbar != null) toolbar.setTitle(R.string.title_edit_ingredient);
            loadIngredientData(ingredientId);
        } else {
            if (toolbar != null) toolbar.setTitle(R.string.title_add_ingredient);
        }

        findViewById(R.id.btnSaveIngredient).setOnClickListener(v -> saveIngredient());
        findViewById(R.id.btnCancelIngredient).setOnClickListener(v -> finish());
    }

    private void loadIngredientData(long id) {
        PantryItem item = dbHelper.getPantryItemById(id);
        if (item != null) {
            etName.setText(item.getName());
            etQuantity.setText(formatQty(item.getQuantity()));
            if (item.getExpiryDate() != null) {
                etExpiryDate.setText(item.getExpiryDate());
            }

            // Set Spinner Selections
            for (int i = 0; i < units.length; i++) {
                if (units[i].equalsIgnoreCase(item.getUnit())) {
                    spinnerUnit.setSelection(i);
                    break;
                }
            }

            for (int i = 0; i < categories.length; i++) {
                if (categories[i].equalsIgnoreCase(item.getCategory())) {
                    spinnerCategory.setSelection(i);
                    break;
                }
            }
        }
    }

    private void showDatePicker() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, (view, y, m, d) -> {
            calendar.set(y, m, d);
            etExpiryDate.setText(dateFormatter.format(calendar.getTime()));
        }, year, month, day);

        dialog.show();
    }

    private void setExpiryPreset(int daysToAdd) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, daysToAdd);
        etExpiryDate.setText(dateFormatter.format(cal.getTime()));
    }

    private void saveIngredient() {
        String name = etName.getText() != null ? etName.getText().toString().trim() : "";
        String qtyStr = etQuantity.getText() != null ? etQuantity.getText().toString().trim() : "";
        String unit = spinnerUnit.getSelectedItem() != null ? spinnerUnit.getSelectedItem().toString() : "pcs";
        String category = spinnerCategory.getSelectedItem() != null ? spinnerCategory.getSelectedItem().toString() : "Pantry & Canned";
        String expiry = etExpiryDate.getText() != null ? etExpiryDate.getText().toString().trim() : "";

        // Form Validation
        boolean isValid = true;
        tilName.setError(null);
        tilQuantity.setError(null);

        if (name.isEmpty()) {
            tilName.setError(getString(R.string.err_name_required));
            isValid = false;
        }

        double quantity = 0.0;
        try {
            quantity = Double.parseDouble(qtyStr);
            if (quantity <= 0) {
                tilQuantity.setError(getString(R.string.err_quantity_invalid));
                isValid = false;
            }
        } catch (NumberFormatException e) {
            tilQuantity.setError(getString(R.string.err_quantity_invalid));
            isValid = false;
        }

        if (!isValid) return;

        if (ingredientId != -1) {
            // Update
            PantryItem item = new PantryItem(ingredientId, name, quantity, unit, expiry, category);
            dbHelper.updatePantryItem(item);
            Toast.makeText(this, "Ingredient updated successfully!", Toast.LENGTH_SHORT).show();
        } else {
            // Create
            PantryItem item = new PantryItem(name, quantity, unit, expiry, category);
            dbHelper.addPantryItem(item);
            Toast.makeText(this, "Ingredient added to pantry!", Toast.LENGTH_SHORT).show();
        }

        finish();
    }

    private String formatQty(double qty) {
        if (qty == (long) qty) {
            return String.format(Locale.US, "%d", (long) qty);
        } else {
            return String.format(Locale.US, "%.1f", qty);
        }
    }
}
