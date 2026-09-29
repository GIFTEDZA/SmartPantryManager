package com.example.smartpantrymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 1;

    // Pantry Table
    private static final String TABLE_PANTRY = "pantry";
    private static final String KEY_PANTRY_ID = "id";
    private static final String KEY_PANTRY_NAME = "name";
    private static final String KEY_PANTRY_QTY = "quantity";
    private static final String KEY_PANTRY_UNIT = "unit";
    private static final String KEY_PANTRY_EXPIRY = "expiry_date";
    private static final String KEY_PANTRY_CATEGORY = "category";

    // Recipes Table
    private static final String TABLE_RECIPES = "recipes";
    private static final String KEY_RECIPE_ID = "id";
    private static final String KEY_RECIPE_TITLE = "title";
    private static final String KEY_RECIPE_CATEGORY = "category";
    private static final String KEY_RECIPE_PREP_TIME = "prep_time";
    private static final String KEY_RECIPE_SERVINGS = "servings";
    private static final String KEY_RECIPE_INSTRUCTIONS = "instructions";
    private static final String KEY_RECIPE_IMAGE_CAT = "image_category";

    // Recipe Ingredients Table
    private static final String TABLE_RECIPE_INGREDIENTS = "recipe_ingredients";
    private static final String KEY_RI_ID = "id";
    private static final String KEY_RI_RECIPE_ID = "recipe_id";
    private static final String KEY_RI_NAME = "ingredient_name";
    private static final String KEY_RI_QTY = "quantity";
    private static final String KEY_RI_UNIT = "unit";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PANTRY_TABLE = "CREATE TABLE " + TABLE_PANTRY + " ("
                + KEY_PANTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_PANTRY_NAME + " TEXT NOT NULL, "
                + KEY_PANTRY_QTY + " REAL NOT NULL, "
                + KEY_PANTRY_UNIT + " TEXT, "
                + KEY_PANTRY_EXPIRY + " TEXT, "
                + KEY_PANTRY_CATEGORY + " TEXT" + ")";

        String CREATE_RECIPES_TABLE = "CREATE TABLE " + TABLE_RECIPES + " ("
                + KEY_RECIPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_RECIPE_TITLE + " TEXT NOT NULL, "
                + KEY_RECIPE_CATEGORY + " TEXT, "
                + KEY_RECIPE_PREP_TIME + " TEXT, "
                + KEY_RECIPE_SERVINGS + " INTEGER, "
                + KEY_RECIPE_INSTRUCTIONS + " TEXT, "
                + KEY_RECIPE_IMAGE_CAT + " TEXT" + ")";

        String CREATE_RI_TABLE = "CREATE TABLE " + TABLE_RECIPE_INGREDIENTS + " ("
                + KEY_RI_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_RI_RECIPE_ID + " INTEGER NOT NULL, "
                + KEY_RI_NAME + " TEXT NOT NULL, "
                + KEY_RI_QTY + " REAL NOT NULL, "
                + KEY_RI_UNIT + " TEXT, "
                + "FOREIGN KEY(" + KEY_RI_RECIPE_ID + ") REFERENCES " + TABLE_RECIPES + "(" + KEY_RECIPE_ID + ") ON DELETE CASCADE)";

        db.execSQL(CREATE_PANTRY_TABLE);
        db.execSQL(CREATE_RECIPES_TABLE);
        db.execSQL(CREATE_RI_TABLE);

        // Pre-load / Seed 20 diverse recipes
        seedRecipes(db);
        seedInitialPantry(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANTRY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE_INGREDIENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        onCreate(db);
    }

    // ==================== PANTRY CRUD OPERATIONS ====================

    public long addPantryItem(PantryItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_PANTRY_NAME, item.getName().trim());
        values.put(KEY_PANTRY_QTY, item.getQuantity());
        values.put(KEY_PANTRY_UNIT, item.getUnit());
        values.put(KEY_PANTRY_EXPIRY, item.getExpiryDate());
        values.put(KEY_PANTRY_CATEGORY, item.getCategory());

        long id = db.insert(TABLE_PANTRY, null, values);
        item.setId(id);
        return id;
    }

    public List<PantryItem> getAllPantryItems() {
        List<PantryItem> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PANTRY, null, null, null, null, null, KEY_PANTRY_NAME + " ASC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                PantryItem item = new PantryItem();
                item.setId(cursor.getLong(cursor.getColumnIndexOrThrow(KEY_PANTRY_ID)));
                item.setName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PANTRY_NAME)));
                item.setQuantity(cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_PANTRY_QTY)));
                item.setUnit(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PANTRY_UNIT)));
                item.setExpiryDate(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PANTRY_EXPIRY)));
                item.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PANTRY_CATEGORY)));
                list.add(item);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public PantryItem getPantryItemById(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PANTRY, null, KEY_PANTRY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            PantryItem item = new PantryItem();
            item.setId(cursor.getLong(cursor.getColumnIndexOrThrow(KEY_PANTRY_ID)));
            item.setName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PANTRY_NAME)));
            item.setQuantity(cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_PANTRY_QTY)));
            item.setUnit(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PANTRY_UNIT)));
            item.setExpiryDate(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PANTRY_EXPIRY)));
            item.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PANTRY_CATEGORY)));
            cursor.close();
            return item;
        }
        if (cursor != null) cursor.close();
        return null;
    }

    public int updatePantryItem(PantryItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_PANTRY_NAME, item.getName().trim());
        values.put(KEY_PANTRY_QTY, item.getQuantity());
        values.put(KEY_PANTRY_UNIT, item.getUnit());
        values.put(KEY_PANTRY_EXPIRY, item.getExpiryDate());
        values.put(KEY_PANTRY_CATEGORY, item.getCategory());

        return db.update(TABLE_PANTRY, values, KEY_PANTRY_ID + "=?",
                new String[]{String.valueOf(item.getId())});
    }

    public void deletePantryItem(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PANTRY, KEY_PANTRY_ID + "=?", new String[]{String.valueOf(id)});
    }

    public void clearPantry() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PANTRY, null, null);
    }

    public void seedSamplePantryData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PANTRY, null, null);
        seedInitialPantry(db);
    }

    /**
     * Deducts recipe ingredient quantities from user's pantry when a recipe is cooked.
     */
    public void deductRecipeIngredients(List<RecipeIngredient> recipeIngredients) {
        List<PantryItem> pantry = getAllPantryItems();
        SQLiteDatabase db = this.getWritableDatabase();

        for (RecipeIngredient req : recipeIngredients) {
            for (PantryItem item : pantry) {
                if (IngredientMatcher.isIngredientNameMatch(req.getIngredientName(), item.getName())) {
                    double reqQtyBase = IngredientMatcher.convertToBaseUnit(req.getQuantity(), req.getUnit());
                    double pantryQtyBase = IngredientMatcher.convertToBaseUnit(item.getQuantity(), item.getUnit());

                    double remainingBase = Math.max(0, pantryQtyBase - reqQtyBase);
                    if (remainingBase <= 0) {
                        db.delete(TABLE_PANTRY, KEY_PANTRY_ID + "=?", new String[]{String.valueOf(item.getId())});
                    } else {
                        // Convert remaining back to original unit scale
                        double newQty = remainingBase;
                        if ("kg".equalsIgnoreCase(item.getUnit()) || "l".equalsIgnoreCase(item.getUnit())) {
                            newQty /= 1000.0;
                        }
                        ContentValues cv = new ContentValues();
                        cv.put(KEY_PANTRY_QTY, newQty);
                        db.update(TABLE_PANTRY, cv, KEY_PANTRY_ID + "=?", new String[]{String.valueOf(item.getId())});
                    }
                    break;
                }
            }
        }
    }

    // ==================== RECIPES QUERY OPERATIONS ====================

    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_RECIPES, null, null, null, null, null, KEY_RECIPE_TITLE + " ASC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Recipe recipe = new Recipe();
                long rId = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_RECIPE_ID));
                recipe.setId(rId);
                recipe.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(KEY_RECIPE_TITLE)));
                recipe.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(KEY_RECIPE_CATEGORY)));
                recipe.setPrepTime(cursor.getString(cursor.getColumnIndexOrThrow(KEY_RECIPE_PREP_TIME)));
                recipe.setServings(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_RECIPE_SERVINGS)));
                recipe.setInstructions(cursor.getString(cursor.getColumnIndexOrThrow(KEY_RECIPE_INSTRUCTIONS)));
                recipe.setImageCategory(cursor.getString(cursor.getColumnIndexOrThrow(KEY_RECIPE_IMAGE_CAT)));

                recipe.setIngredients(getIngredientsForRecipe(db, rId));
                recipes.add(recipe);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return recipes;
    }

    public Recipe getRecipeById(long recipeId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_RECIPES, null, KEY_RECIPE_ID + "=?",
                new String[]{String.valueOf(recipeId)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Recipe recipe = new Recipe();
            recipe.setId(recipeId);
            recipe.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(KEY_RECIPE_TITLE)));
            recipe.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(KEY_RECIPE_CATEGORY)));
            recipe.setPrepTime(cursor.getString(cursor.getColumnIndexOrThrow(KEY_RECIPE_PREP_TIME)));
            recipe.setServings(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_RECIPE_SERVINGS)));
            recipe.setInstructions(cursor.getString(cursor.getColumnIndexOrThrow(KEY_RECIPE_INSTRUCTIONS)));
            recipe.setImageCategory(cursor.getString(cursor.getColumnIndexOrThrow(KEY_RECIPE_IMAGE_CAT)));

            recipe.setIngredients(getIngredientsForRecipe(db, recipeId));
            cursor.close();
            return recipe;
        }
        if (cursor != null) cursor.close();
        return null;
    }

    private List<RecipeIngredient> getIngredientsForRecipe(SQLiteDatabase db, long recipeId) {
        List<RecipeIngredient> list = new ArrayList<>();
        Cursor cursor = db.query(TABLE_RECIPE_INGREDIENTS, null, KEY_RI_RECIPE_ID + "=?",
                new String[]{String.valueOf(recipeId)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                RecipeIngredient ri = new RecipeIngredient();
                ri.setId(cursor.getLong(cursor.getColumnIndexOrThrow(KEY_RI_ID)));
                ri.setRecipeId(recipeId);
                ri.setIngredientName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_RI_NAME)));
                ri.setQuantity(cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_RI_QTY)));
                ri.setUnit(cursor.getString(cursor.getColumnIndexOrThrow(KEY_RI_UNIT)));
                list.add(ri);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    // ==================== SEED DATA ====================

    private void seedInitialPantry(SQLiteDatabase db) {
        // Seed default pantry ingredients so user immediately gets some matched recipes!
        insertPantryDirect(db, "Eggs", 6, "pcs", "2026-05-10", "Dairy & Eggs");
        insertPantryDirect(db, "Butter", 200, "g", "2026-06-01", "Dairy & Eggs");
        insertPantryDirect(db, "Milk", 1, "L", "2026-04-20", "Dairy & Eggs");
        insertPantryDirect(db, "Bread", 1, "pack", "2026-04-18", "Bakery");
        insertPantryDirect(db, "Cheddar Cheese", 250, "g", "2026-05-15", "Dairy & Eggs");
        insertPantryDirect(db, "Garlic", 5, "cloves", "2026-07-01", "Vegetables & Fruit");
        insertPantryDirect(db, "Pasta", 500, "g", "2026-12-31", "Grains & Pasta");
        insertPantryDirect(db, "Olive Oil", 500, "ml", "2027-01-01", "Spices & Oils");
        insertPantryDirect(db, "Tomatoes", 4, "pcs", "2026-04-25", "Vegetables & Fruit");
    }

    private void insertPantryDirect(SQLiteDatabase db, String name, double qty, String unit, String expiry, String cat) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_PANTRY_NAME, name);
        cv.put(KEY_PANTRY_QTY, qty);
        cv.put(KEY_PANTRY_UNIT, unit);
        cv.put(KEY_PANTRY_EXPIRY, expiry);
        cv.put(KEY_PANTRY_CATEGORY, cat);
        db.insert(TABLE_PANTRY, null, cv);
    }

    private void seedRecipes(SQLiteDatabase db) {
        // 1. Classic Omelette
        addRecipeWithIngredients(db, "Classic Fluffy Omelette", "Breakfast", "10 mins", 1,
                "1. Whisk eggs, milk, salt, and black pepper in a bowl.\n" +
                        "2. Melt butter in a non-stick skillet over medium heat.\n" +
                        "3. Pour in egg mixture and gently cook until set.\n" +
                        "4. Fold in half and serve warm.", "Dairy",
                new String[]{"Eggs", "Butter", "Milk", "Salt", "Black Pepper"},
                new double[]{2, 1, 2, 1, 1},
                new String[]{"pcs", "tbsp", "tbsp", "tsp", "tsp"});

        // 2. Grilled Cheese Sandwich
        addRecipeWithIngredients(db, "Golden Grilled Cheese", "Lunch", "10 mins", 1,
                "1. Butter one side of each bread slice.\n" +
                        "2. Place cheddar cheese slices between unbuttered sides.\n" +
                        "3. Grill in pan over medium-low heat until bread is golden and cheese is melted.", "Bakery",
                new String[]{"Bread", "Butter", "Cheddar Cheese"},
                new double[]{2, 1, 2},
                new String[]{"slices", "tbsp", "slices"});

        // 3. Garlic Butter Pasta
        addRecipeWithIngredients(db, "Garlic Butter Pasta", "Dinner", "15 mins", 2,
                "1. Boil pasta in salted water until al dente.\n" +
                        "2. Sauté minced garlic in melted butter and olive oil until fragrant.\n" +
                        "3. Toss drained pasta into garlic butter sauce. Top with Parmesan cheese if desired.", "Grains",
                new String[]{"Pasta", "Garlic", "Butter", "Olive Oil"},
                new double[]{200, 3, 2, 1},
                new String[]{"g", "cloves", "tbsp", "tbsp"});

        // 4. Fresh Tomato Soup
        addRecipeWithIngredients(db, "Fresh Tomato Basil Soup", "Soup", "25 mins", 2,
                "1. Chop tomatoes, onion, and garlic.\n" +
                        "2. Sauté onion and garlic in olive oil, then add tomatoes and salt.\n" +
                        "3. Simmer until soft, blend smooth, and serve hot.", "Vegetables",
                new String[]{"Tomatoes", "Garlic", "Butter", "Olive Oil", "Salt"},
                new double[]{4, 2, 1, 1, 1},
                new String[]{"pcs", "cloves", "tbsp", "tbsp", "tsp"});

        // 5. French Toast
        addRecipeWithIngredients(db, "Classic French Toast", "Breakfast", "15 mins", 2,
                "1. Whisk eggs and milk in a shallow bowl.\n" +
                        "2. Dip bread slices into mixture until coated on both sides.\n" +
                        "3. Sauté in butter until golden brown on both sides.", "Breakfast",
                new String[]{"Bread", "Eggs", "Milk", "Butter"},
                new double[]{4, 2, 0.5, 1},
                new String[]{"slices", "pcs", "cups", "tbsp"});

        // 6. Chicken Fried Rice
        addRecipeWithIngredients(db, "Savory Chicken Fried Rice", "Dinner", "20 mins", 2,
                "1. Sauté diced chicken breast in vegetable oil until fully cooked.\n" +
                        "2. Push chicken to side, scramble eggs in pan.\n" +
                        "3. Mix in cooked rice, minced garlic, mixed vegetables, and soy sauce. Stir fry 3-5 mins.", "Meat",
                new String[]{"Cooked Rice", "Chicken Breast", "Eggs", "Garlic", "Soy Sauce"},
                new double[]{2, 200, 2, 2, 2},
                new String[]{"cups", "g", "pcs", "cloves", "tbsp"});

        // 7. Caprese Salad
        addRecipeWithIngredients(db, "Fresh Caprese Salad", "Salad", "10 mins", 2,
                "1. Slice fresh tomatoes and mozzarella cheese into round discs.\n" +
                        "2. Alternate layers of tomato, mozzarella, and fresh basil leaves.\n" +
                        "3. Drizzle with olive oil and season with salt.", "Salad",
                new String[]{"Tomatoes", "Mozzarella Cheese", "Fresh Basil", "Olive Oil", "Salt"},
                new double[]{2, 150, 5, 2, 1},
                new String[]{"pcs", "g", "pcs", "tbsp", "tsp"});

        // 8. Fluffy Pancakes
        addRecipeWithIngredients(db, "Homemade Fluffy Pancakes", "Breakfast", "20 mins", 3,
                "1. Mix flour, sugar, baking powder, and a pinch of salt.\n" +
                        "2. Whisk in milk, egg, and melted butter until smooth batter forms.\n" +
                        "3. Pour onto hot greased skillet until bubbles pop, flip and cook golden.", "Breakfast",
                new String[]{"Flour", "Milk", "Egg", "Sugar", "Butter"},
                new double[]{1, 1, 1, 2, 2},
                new String[]{"cups", "cups", "pcs", "tbsp", "tbsp"});

        // 9. Guacamole & Chips
        addRecipeWithIngredients(db, "Fresh Guacamole Dip", "Snack", "10 mins", 4,
                "1. Mash ripe avocados in a bowl.\n" +
                        "2. Stir in diced onion, lime juice, cilantro, and salt.\n" +
                        "3. Serve immediately with tortilla chips.", "Snack",
                new String[]{"Avocados", "Lime", "Onion", "Salt", "Tortilla Chips"},
                new double[]{2, 1, 0.5, 1, 100},
                new String[]{"pcs", "pc", "pc", "tsp", "g"});

        // 10. Garlic Mashed Potatoes
        addRecipeWithIngredients(db, "Creamy Garlic Mashed Potatoes", "Side", "25 mins", 4,
                "1. Peel and boil potatoes until fork tender.\n" +
                        "2. Drain water and mash potatoes with butter, milk, minced garlic, and salt until smooth.", "Vegetables",
                new String[]{"Potatoes", "Butter", "Milk", "Garlic", "Salt"},
                new double[]{4, 3, 0.5, 3, 1},
                new String[]{"pcs", "tbsp", "cups", "cloves", "tsp"});

        // 11. Berry Smoothie Bowl
        addRecipeWithIngredients(db, "Berry Yogurt Smoothie Bowl", "Breakfast", "5 mins", 1,
                "1. Blend frozen berries, banana, yogurt, and milk until thick.\n" +
                        "2. Pour into bowl and drizzle with honey.", "Breakfast",
                new String[]{"Banana", "Frozen Berries", "Yogurt", "Milk", "Honey"},
                new double[]{1, 1, 1, 0.5, 1},
                new String[]{"pc", "cups", "cups", "cups", "tbsp"});

        // 12. Creamy Avocado Toast
        addRecipeWithIngredients(db, "Lemon Garlic Avocado Toast", "Breakfast", "5 mins", 1,
                "1. Toast bread slices until golden crisp.\n" +
                        "2. Mash avocado with lemon juice, olive oil, and salt.\n" +
                        "3. Spread generously over toast.", "Breakfast",
                new String[]{"Bread", "Avocado", "Lemon Juice", "Olive Oil", "Salt"},
                new double[]{2, 1, 1, 1, 0.5},
                new String[]{"slices", "pc", "tbsp", "tsp", "tsp"});

        // 13. Spinach Scramble
        addRecipeWithIngredients(db, "Butter Spinach Scrambled Eggs", "Breakfast", "10 mins", 1,
                "1. Sauté fresh spinach in butter until wilted.\n" +
                        "2. Whisk eggs with salt and pepper, pour over spinach.\n" +
                        "3. Scramble gently over medium-low heat.", "Breakfast",
                new String[]{"Eggs", "Spinach", "Butter", "Salt", "Black Pepper"},
                new double[]{3, 1, 1, 0.5, 0.5},
                new String[]{"pcs", "cups", "tbsp", "tsp", "tsp"});

        // 14. Chicken Caesar Salad
        addRecipeWithIngredients(db, "Chicken Caesar Salad", "Salad", "15 mins", 2,
                "1. Grill seasoned chicken breast and slice into strips.\n" +
                        "2. Chop romaine lettuce and toss with Caesar dressing and croutons.\n" +
                        "3. Top with chicken and grated Parmesan cheese.", "Salad",
                new String[]{"Chicken Breast", "Romaine Lettuce", "Parmesan Cheese", "Caesar Dressing"},
                new double[]{200, 2, 30, 3},
                new String[]{"g", "cups", "g", "tbsp"});

        // 15. Cheesy Chicken Quesadilla
        addRecipeWithIngredients(db, "Loaded Chicken Quesadilla", "Lunch", "15 mins", 2,
                "1. Lay tortilla flat, spread cooked shredded chicken and cheddar cheese on one half.\n" +
                        "2. Fold tortilla in half and cook in warm skillet until cheese melts and tortilla is crispy.", "Lunch",
                new String[]{"Tortillas", "Cheddar Cheese", "Chicken Breast", "Butter"},
                new double[]{2, 1, 100, 1},
                new String[]{"pcs", "cups", "g", "tbsp"});

        // 16. Banana Oat Pancakes
        addRecipeWithIngredients(db, "3-Ingredient Banana Oat Pancakes", "Breakfast", "12 mins", 2,
                "1. Blend oats, banana, eggs, and milk until smooth batter forms.\n" +
                        "2. Cook small pancakes on buttered pan until golden.", "Breakfast",
                new String[]{"Oats", "Banana", "Eggs", "Milk"},
                new double[]{1, 1, 2, 0.25},
                new String[]{"cups", "pc", "pcs", "cups"});

        // 17. Stir-Fry Vegetables with Rice
        addRecipeWithIngredients(db, "Garlic Soy Vegetable Stir-Fry", "Dinner", "18 mins", 2,
                "1. Heat sesame oil in skillet over high heat.\n" +
                        "2. Sauté minced garlic, ginger, and mixed vegetables for 5 mins.\n" +
                        "3. Stir in soy sauce and serve over hot steamed rice.", "Vegetables",
                new String[]{"Mixed Vegetables", "Soy Sauce", "Garlic", "Rice"},
                new double[]{2, 2, 2, 1},
                new String[]{"cups", "tbsp", "cloves", "cups"});

        // 18. Greek Salad
        addRecipeWithIngredients(db, "Mediterranean Greek Salad", "Salad", "10 mins", 2,
                "1. Chop cucumber, tomatoes, and feta cheese into cubes.\n" +
                        "2. Toss in a bowl with olives, olive oil, and oregano.", "Salad",
                new String[]{"Cucumber", "Tomatoes", "Feta Cheese", "Olives", "Olive Oil"},
                new double[]{1, 2, 100, 0.5, 2},
                new String[]{"pc", "pcs", "g", "cups", "tbsp"});

        // 19. Warm Honey Apple Oatmeal
        addRecipeWithIngredients(db, "Honey Apple Oatmeal", "Breakfast", "10 mins", 1,
                "1. Cook oats in milk over medium heat for 5-7 minutes.\n" +
                        "2. Stir in diced apple and drizzle with honey before serving.", "Breakfast",
                new String[]{"Oats", "Milk", "Honey", "Apple"},
                new double[]{1, 2, 1, 1},
                new String[]{"cups", "cups", "tbsp", "pc"});

        // 20. Classic Tuna Salad Sandwich
        addRecipeWithIngredients(db, "Classic Tuna Salad Sandwich", "Lunch", "10 mins", 1,
                "1. Drain canned tuna and mix with mayonnaise, chopped celery, salt, and pepper.\n" +
                        "2. Spread tuna mixture between bread slices and serve.", "Lunch",
                new String[]{"Bread", "Canned Tuna", "Mayonnaise", "Salt"},
                new double[]{2, 1, 2, 0.5},
                new String[]{"slices", "can", "tbsp", "tsp"});
    }

    private void addRecipeWithIngredients(SQLiteDatabase db, String title, String category,
                                          String prepTime, int servings, String instructions,
                                          String imgCat, String[] ingNames, double[] qtys, String[] units) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_RECIPE_TITLE, title);
        cv.put(KEY_RECIPE_CATEGORY, category);
        cv.put(KEY_RECIPE_PREP_TIME, prepTime);
        cv.put(KEY_RECIPE_SERVINGS, servings);
        cv.put(KEY_RECIPE_INSTRUCTIONS, instructions);
        cv.put(KEY_RECIPE_IMAGE_CAT, imgCat);

        long recipeId = db.insert(TABLE_RECIPES, null, cv);

        for (int i = 0; i < ingNames.length; i++) {
            ContentValues riCv = new ContentValues();
            riCv.put(KEY_RI_RECIPE_ID, recipeId);
            riCv.put(KEY_RI_NAME, ingNames[i]);
            riCv.put(KEY_RI_QTY, qtys[i]);
            riCv.put(KEY_RI_UNIT, units[i]);
            db.insert(TABLE_RECIPE_INGREDIENTS, null, riCv);
        }
    }
}
