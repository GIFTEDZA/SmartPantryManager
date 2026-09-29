package com.example.smartpantrymanager;

import java.util.ArrayList;
import java.util.List;

public class IngredientMatcher {

    /**
     * Normalizes an ingredient name string for robust matching
     * Handles case-insensitivity, whitespace, punctuation, and common plural/singular forms.
     */
    public static String normalizeName(String name) {
        if (name == null) return "";
        String clean = name.toLowerCase().trim().replaceAll("[^a-z0-9\\s]", "");
        
        // Basic singularization for common ingredient suffixes
        if (clean.endsWith("tomatoes")) return clean.substring(0, clean.length() - 2); // tomato
        if (clean.endsWith("potatoes")) return clean.substring(0, clean.length() - 2); // potato
        if (clean.endsWith("berries")) return clean.substring(0, clean.length() - 3) + "y"; // berry
        if (clean.endsWith("es") && clean.length() > 4) return clean.substring(0, clean.length() - 2);
        if (clean.endsWith("s") && !clean.endsWith("ss") && clean.length() > 3) return clean.substring(0, clean.length() - 1);
        
        return clean;
    }

    /**
     * Checks if a required ingredient matches a pantry ingredient name
     */
    public static boolean isIngredientNameMatch(String requiredName, String pantryName) {
        String reqNorm = normalizeName(requiredName);
        String pantryNorm = normalizeName(pantryName);

        if (reqNorm.isEmpty() || pantryNorm.isEmpty()) return false;

        // Exact normalized match
        if (reqNorm.equals(pantryNorm)) return true;

        // Substring / core ingredient match (e.g., "chicken breast" vs "chicken", "cooked rice" vs "rice")
        if (reqNorm.contains(pantryNorm) || pantryNorm.contains(reqNorm)) return true;

        return false;
    }

    /**
     * Converts quantity to a standard base unit if units are comparable.
     */
    public static double convertToBaseUnit(double qty, String unit) {
        if (unit == null) return qty;
        String u = unit.toLowerCase().trim();
        switch (u) {
            case "kg":
                return qty * 1000.0; // convert to grams
            case "g":
                return qty;
            case "l":
                return qty * 1000.0; // convert to ml
            case "ml":
                return qty;
            case "tbsp":
                return qty * 15.0; // approx 15 ml
            case "tsp":
                return qty * 5.0;  // approx 5 ml
            case "cups":
            case "cup":
                return qty * 240.0; // approx 240 ml
            default:
                return qty; // pcs, slices, cans, etc.
        }
    }

    /**
     * Evaluates strict matching for a recipe against the user's current pantry list.
     * Updates recipe's missingIngredientCount and missingIngredientNames.
     * @return true if ALL required ingredients are satisfied in quantity.
     */
    public static boolean evaluateRecipeMatch(Recipe recipe, List<PantryItem> pantryItems) {
        List<RecipeIngredient> requiredIngredients = recipe.getIngredients();
        if (requiredIngredients == null || requiredIngredients.isEmpty()) {
            recipe.setMissingIngredientCount(0);
            recipe.getMissingIngredientNames().clear();
            return true;
        }

        int missingCount = 0;
        List<String> missingNames = new ArrayList<>();

        for (RecipeIngredient req : requiredIngredients) {
            boolean satisfied = false;

            for (PantryItem item : pantryItems) {
                if (isIngredientNameMatch(req.getIngredientName(), item.getName())) {
                    double reqQtyBase = convertToBaseUnit(req.getQuantity(), req.getUnit());
                    double pantryQtyBase = convertToBaseUnit(item.getQuantity(), item.getUnit());

                    // Check if user has at least the required quantity
                    if (pantryQtyBase >= reqQtyBase) {
                        satisfied = true;
                        break;
                    }
                }
            }

            if (!satisfied) {
                missingCount++;
                missingNames.add(req.getIngredientName());
            }
        }

        recipe.setMissingIngredientCount(missingCount);
        recipe.setMissingIngredientNames(missingNames);

        // Strict-matching rule: 0 missing ingredients allowed
        return missingCount == 0;
    }

    /**
     * Filters a list of recipes to return strictly matching recipes (0 missing ingredients)
     */
    public static List<Recipe> getStrictlyMatchingRecipes(List<Recipe> allRecipes, List<PantryItem> pantryItems) {
        List<Recipe> matched = new ArrayList<>();
        for (Recipe r : allRecipes) {
            if (evaluateRecipeMatch(r, pantryItems)) {
                matched.add(r);
            }
        }
        return matched;
    }

    /**
     * Filters a list of recipes to return "Almost There" recipes (missing exactly 1 ingredient)
     */
    public static List<Recipe> getAlmostThereRecipes(List<Recipe> allRecipes, List<PantryItem> pantryItems) {
        List<Recipe> almostThere = new ArrayList<>();
        for (Recipe r : allRecipes) {
            evaluateRecipeMatch(r, pantryItems);
            if (r.getMissingIngredientCount() == 1) {
                almostThere.add(r);
            }
        }
        return almostThere;
    }
}
