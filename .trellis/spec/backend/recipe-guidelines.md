# Recipe Guidelines

> Custom recipe conventions for SkyResources3 processing systems.

---

## Overview

SkyResources3 uses vanilla crafting recipes for normal crafting/smelting and a single custom NeoForge recipe type for old SkyResources processing recipes.

The custom type is:

- Recipe type id: `skyresources3:process`
- Serializer id: `skyresources3:process`
- Registration owner: `ModRecipeTypes`
- Data model: `SkyResourcesProcessRecipe`
- Runtime input: `ProcessRecipeInput`
- Lookup helpers and process-name constants: `ProcessRecipes`

This replaces the old 1.12.2 `ProcessRecipe` / `ProcessRecipeManager` lists with data-pack recipes while keeping the old "process name + inputs + outputs + float parameter" shape.

---

## JSON Contract

Generated process recipes live under:

```text
src/generated/resources/data/skyresources3/recipe/process/<process>/<name>.json
```

Every process recipe must contain:

- `process`: non-empty process name such as `freezer`, `knife`, `rockgrinder`, `combustion`, `fusion`, or `infusion`.
- `inputs`: non-empty list of `ProcessIngredient` objects.
- `outputs`: non-empty list of item stacks.
- `parameter`: optional float, defaulting to `0.0`.
- `group`: optional string, defaulting to empty.

`ProcessIngredient` uses:

- `ingredient`: vanilla/NeoForge `Ingredient` codec, including item ids or item tags.
- `count`: optional positive integer, defaulting to `1`.

Example:

```json
{
  "type": "skyresources3:process",
  "process": "freezer",
  "inputs": [
    {
      "ingredient": "minecraft:snowball",
      "count": 4
    }
  ],
  "outputs": [
    {
      "id": "skyresources3:heavy_snowball",
      "count": 1
    }
  ],
  "parameter": 40.0
}
```

---

## Runtime Matching

`SkyResourcesProcessRecipe.matches` follows these rules:

- `process` must match exactly.
- Runtime `parameter` must be greater than or equal to the recipe `parameter`.
- Non-empty input stacks must have the same count of stack entries as recipe inputs.
- Inputs match unordered; each runtime stack can satisfy one `ProcessIngredient`.
- Each matched stack must satisfy the ingredient and have at least the declared `count`.

Use `ProcessRecipes.find(level, process, items, parameter)` when the process has a runtime threshold such as combustion heat.

Use `ProcessRecipes.find(level, process, items)` or `ProcessRecipes.findAll(level, process, items)` when the recipe parameter is descriptive output data such as grinder chance or freezer ticks.

Runtime systems that produce processing outputs must query `ProcessRecipes` from a `ServerLevel`. Do not duplicate output tables in event handlers, items, block entities, or screens. Client/item methods that do not receive a `Level`, such as `Item#getDestroySpeed`, may keep a narrow built-in candidate hint for interaction speed only; those hints must not spawn outputs or replace server recipe matching.

For `ProcessRecipes.INFUSION`, recipe inputs have a positional convention on top of unordered matching: input `0` is the consumed ingredient stack, input `1` is the target block represented by `BlockState#getBlock().asItem()` or an item tag. The `parameter` is the health cost. Runtime code should use `InfusionRecipes` instead of reading those fields directly.

For `ProcessRecipes.FUSION`, the `parameter` preserves the old fusion table's per-progress-tick catalyst drain. A full legacy craft has 100 progress ticks, so the user-facing catalyst percentage is `parameter * 10000`. Catalyst item yield values are a separate fusion-table runtime concern and should not be encoded as normal fusion recipe inputs.

## Combustion Automation Contract

### 1. Scope / Trigger

Use this contract when changing combustion recipe execution, the Combustion Collector, the Smart Combustion
Controller, or the machine-casing multiblock validation used by combustion.

### 2. Signatures

- Manual casing craft:
  ```java
  private boolean craftChamberItems(
          ServerLevel level,
          MachineVariant heaterVariant,
          Predicate<ItemStack> outputFilter,
          int maxCrafts
  )
  ```
- Controller entry point:
  ```java
  public boolean craftSingleForController(ServerLevel level, Predicate<ItemStack> outputFilter)
  ```
- Collector output routing:
  ```java
  public ItemStack insertOutput(ItemStack stack)
  ```

### 3. Contracts

- Combustion recipes are looked up through `CombustionRecipeLogic.findRecipe` / `ProcessRecipes.COMBUSTION`, never
  through a duplicated static output table.
- The controller owns five filter slots. Each slot stores one real item stack and is checked left-to-right; the first
  filter with a craftable first recipe output wins.
- Redstone power on the controller disables automatic crafting. Redstone pulses on the casing still trigger manual
  combustion.
- The controller crafts at most one recipe per cooldown cycle; manual casing pulses may craft repeatedly while inputs
  and heat remain sufficient.
- A collector adjacent to the chamber west/east/north/south/above receives combustion outputs before leftovers are
  dropped in the chamber.
- Collector/controller blocks count as valid structure blocks only for metal-tier combustion casings. Wooden and stone
  casings must continue to reject them.

### 4. Validation & Error Matrix

| Condition | Expected behavior |
|---|---|
| Controller has no matching filter item | No craft; chamber item entities stay in place |
| Controller is redstone powered | No automatic craft; cooldown does not restart |
| Collector inventory is full | Insert what fits, then drop the remaining output in the chamber |
| Recipe needs more heat than the casing has | No craft; chamber item entities stay in place |
| Wooden or stone casing uses collector/controller as a wall block | Structure remains invalid |

### 5. Good/Base/Bad Cases

- Good: Controller calls the casing craft entry once per non-empty filter slot to preserve legacy output priority.
- Base: Manual casing combustion passes an always-true output filter and routes outputs through the same collector path.
- Bad: Client screens, item tooltips, or block classes spawn combustion outputs or inspect generated recipe JSON directly.

### 6. Tests Required

- `./gradlew.bat compileJava`
- `./gradlew.bat runData` when recipes, tags, or loot tables change
- `./gradlew.bat build`
- `./gradlew.bat runGameTestServer` for combustion block-entity or multiblock changes
- Add focused GameTests when reusable machine test helpers exist for asserting controller priority, redstone disable,
  and collector overflow behavior.

### 7. Wrong vs Correct

Wrong:
```java
// Picks whichever recipe happens to appear first and ignores filter priority.
casing.craftSingleForController(level, this::matchesFilter);
```

Correct:
```java
for (int slot = 0; slot < SLOT_COUNT; slot++) {
    ItemStack filter = this.getStackInSlot(slot);
    if (!filter.isEmpty() && casing.craftSingleForController(level, output -> ItemStack.isSameItemSameComponents(filter, output))) {
        return true;
    }
}
```

## Water Extractor / Aqueous Machine Contract

### 1. Scope / Trigger

Use this contract when changing the hand-held Water Extractor, the Aqueous Concentrator, the Aqueous
Deconcentrator, or the legacy water extraction/insertion rules shared by those systems.

### 2. Contracts

- Water extraction/insertion rules live in `WaterExtractorRecipes`; do not duplicate those tables in the item, block
  entity, screen, or data provider.
- The hand-held Water Extractor applies block-state recipes directly in the world.
- The Aqueous machines apply item-stack recipes in a two-slot inventory:
  - Concentrator consumes water and produces item outputs.
  - Deconcentrator produces water and may have an empty item output.
- Aqueous machines keep the old 4000 mB tank, 100000 FE buffer, 100 progress operation, and mode-specific speed/power
  config defaults.
- Do not encode these recipes in `SkyResourcesProcessRecipe` until the project has a first-class fluid process recipe
  JSON contract. The current process recipe codec requires non-empty item outputs and has no fluid input/output field.

### 3. Validation Matrix

| Condition | Expected behavior |
|---|---|
| Concentrator receives a non-water fluid | Reject through fluid capability |
| Concentrator output slot is full | Do not consume energy, input, or water |
| Deconcentrator tank is full | Do not consume energy or input |
| Deconcentrator recipe has no item output | Consume one input and add water when the tank has space |
| Hand-held extractor targets Dirt or Dehydrated Cactus | Consume stored water and replace the block |
| Hand-held extractor targets Snow, Cactus, or leaves | Add stored water and clear/replace the block |

### 4. Tests Required

- `./gradlew.bat compileJava`
- `./gradlew.bat runData` when recipes, tags, or loot tables change
- `./gradlew.bat build`
- `./gradlew.bat runGameTestServer` for machine or capability changes
- `git diff --check`

---

## Data Generation

Process recipes should be generated from `SkyResources3RecipeProvider` unless a task explicitly requires a hand-authored datapack example.

Guidelines:

- Use `ProcessRecipes` constants instead of repeating process-name string literals.
- Use `ProcessIngredient` for counted inputs rather than encoding repeated items as repeated list entries.
- Prefer item tags for old ore-dictionary-style inputs when the output does not depend on a specific variant.
- Generate one recipe per distinct old output/parameter pair. For example, rock grinder gravel -> sand and gravel -> flint remain separate recipes because their chance parameters differ.
- Do not add fluid process JSON until the project has a fluid capability/storage design for 1.21.11.
- Do not generate dynamic old ore-dictionary integration recipes until the target mod/tag policy is explicit. Prefer stable vanilla/SkyResources recipes first.

---

## Common Mistakes

- Treating `parameter` as one universal concept. Its meaning depends on the process: heat, ticks, chance, per-tick catalyst drain, or health cost.
- Matching process names with ad hoc string literals outside `ProcessRecipes`.
- Using `ItemStack` as an input format when a tag-capable `Ingredient` is needed.
- Adding client-only recipe display or JEI classes to common recipe code.
- Reintroducing the old static mutable recipe manager lists instead of using data-pack recipes.
