package com.example.smartpantrymanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.materialswitch.MaterialSwitch;

public class SettingsFragment extends Fragment {

    private DatabaseHelper dbHelper;
    private MaterialSwitch switchAlerts;
    private Spinner spinnerUnitSystem;
    private SharedPreferences prefs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = new DatabaseHelper(requireContext());
        prefs = requireContext().getSharedPreferences("smart_pantry_prefs", Context.MODE_PRIVATE);

        switchAlerts = view.findViewById(R.id.switchAlerts);
        spinnerUnitSystem = view.findViewById(R.id.spinnerUnitSystem);

        // Setup Spinner
        String[] systems = new String[]{"Metric (g, ml, kg)", "Imperial (oz, lbs, cups)"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, systems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnitSystem.setAdapter(adapter);

        // Load saved preferences
        boolean alertsEnabled = prefs.getBoolean("PREF_ALERTS_ENABLED", true);
        switchAlerts.setChecked(alertsEnabled);

        int selectedUnitSys = prefs.getInt("PREF_UNIT_SYS", 0);
        spinnerUnitSystem.setSelection(selectedUnitSys);

        switchAlerts.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("PREF_ALERTS_ENABLED", isChecked).apply();
            Toast.makeText(requireContext(), isChecked ? "Expiring alerts enabled" : "Expiring alerts disabled", Toast.LENGTH_SHORT).show();
        });

        view.findViewById(R.id.btnSeedSampleData).setOnClickListener(v -> {
            dbHelper.seedSamplePantryData();
            Toast.makeText(requireContext(), R.string.msg_sample_seeded, Toast.LENGTH_LONG).show();
        });

        view.findViewById(R.id.btnClearPantry).setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Clear Pantry")
                    .setMessage("Are you sure you want to remove ALL ingredients from your pantry?")
                    .setPositiveButton("Clear All", (dialog, which) -> {
                        dbHelper.clearPantry();
                        Toast.makeText(requireContext(), "Pantry cleared!", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }
}
