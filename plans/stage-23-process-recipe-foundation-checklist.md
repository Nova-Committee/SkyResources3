# Stage 23 - Process Recipe Foundation Checklist

> Scope: migrate the old `ProcessRecipe` / `ProcessRecipeManager` data shape to a NeoForge 1.21.11 custom recipe foundation.

## Completed

- [x] Read old `ProcessRecipe` and `ProcessRecipeManager` matching semantics.
- [x] Checked NeoForge 1.21.x custom recipe docs and local 1.21.11 sources for `Recipe`, `RecipeSerializer`, `RecipeType`, `RecipeInput`, and `RecipeOutput`.
- [x] Added `skyresources3:process` recipe type and serializer registration.
- [x] Added `ProcessIngredient` for tag-capable counted inputs.
- [x] Added `ProcessRecipeInput` and `ProcessRecipes` lookup helpers.
- [x] Added `SkyResourcesProcessRecipe` with JSON `MapCodec`, network `StreamCodec`, placement info, and unordered counted-input matching.
- [x] Generated initial old process recipe data for freezer, rock grinder, knife, and the low-risk primus alchemical dust combustion recipe.
- [x] Recorded the new recipe contract in `.trellis/spec/backend/recipe-guidelines.md`.
- [x] Follow-up: cutting knife and rock grinder runtime logic now consumes process recipe data.
- [x] Follow-up: infusion stone and Life Infuser runtime logic now consumes process recipe data.
- [x] Follow-up: low-risk vanilla/SkyResources combustion recipe data has been expanded.
- [x] Follow-up: directly mappable vanilla/SkyResources fusion recipe data has been generated.

## Generated Process Recipes

- `freezer/heavy_snowball`
- `freezer/coarse_dirt`
- `freezer/frozen_iron_ingot`
- `freezer/soul_sand`
- `rockgrinder/gravel`
- `rockgrinder/sand`
- `rockgrinder/flint`
- `rockgrinder/crushed_stone`
- `rockgrinder/crushed_netherrack`
- `rockgrinder/sawdust`
- `knife/cactus_fruit`
- `knife/melon_slice`
- `knife/oak_planks`
- `knife/spruce_planks`
- `knife/birch_planks`
- `knife/jungle_planks`
- `knife/acacia_planks`
- `knife/dark_oak_planks`
- `knife/sticks_from_planks`
- `knife/petrified_planks`
- `knife/sticks_from_petrified_planks`
- `combustion/primus_alchemical_dust`

## Deferred

- [ ] Migrate dynamic old ore-dictionary fusion and ore dust recipes after target mod/tag integration policy is explicit.
- [ ] Add fluid-capable process recipe data after fluid storage/capability design is in place.
- [ ] Add JEI/EMI display categories after menu/screen and integration dependencies are selected.

## Verification

- [x] `./gradlew.bat compileJava`
- [x] `./gradlew.bat runData`
