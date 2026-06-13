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

## Machine Casing Installed Machine Contract

### 1. Scope / Trigger

Use this contract when changing `MachineCasingBlockEntity`, casing-installed machine items, casing menus/screens, or
systems that read casing-provided heat such as the Crucible.

### 2. Signatures

- Install gate:
  ```java
  public boolean canInstallMachine(ItemStack stack)
  ```
- Installed-machine interaction:
  ```java
  public boolean installHeater(ItemStack stack, Player player)
  public ItemStack removeHeater()
  ```
- Heat-provider bridge:
  ```java
  public int heatSourceValue()
  public static int HeatSources.getHeatSourceValue(Level level, BlockPos pos)
  ```

### 3. Contracts

- Machine Casings may install exactly one casing machine item at a time.
- Current supported installed machine item classes are `CombustionHeaterItem`, `HeatProviderItem`, and
  `CondenserItem`.
- The stored installed-machine stack remains serialized under the existing `heater` key for compatibility with older
  saves that already installed combustion heaters.
- Combustion heaters own combustion multiblock heat accumulation and pulse crafting.
- Heat providers own direct heat-source output for blocks above them, especially Crucibles.
- Condensers own fluid/block-aware world-source recipe execution and use the casing slot as their catalyst slot.
- The casing fuel slot validates against the currently installed machine variant; if no installed machine exists, the
  slot rejects insertion.
- Heat provider output is `variant.heatPerTick()` while active and `0` while redstone-powered or out of fuel.
- Do not add optional legacy metal/RF/fluid variants unless the corresponding target mod dependency and capability
  policy is explicit.

### 4. Validation & Error Matrix

| Condition | Expected behavior |
|---|---|
| Right-click empty casing with supported machine item | Install one item and shrink the player's stack unless creative |
| Right-click empty casing with unsupported item | Do not install; open/pass according to normal block interaction |
| Shift-right-click casing with installed machine | Return installed machine and reset runtime heat/fuel display state |
| Fuel inserted before machine is installed | Reject through the item transfer handler |
| Fuel does not match installed machine variant | Reject through slot/menu/capability validation |
| Heat provider is redstone-powered | Report `0` heat and do not consume new fuel |
| Combustion heater installed | Continue using combustion chamber validation and controller/collector routing |
| Condenser is redstone-powered | Pause condenser progress without consuming a new catalyst or source |
| Condenser has no matching source above | Reset progress display, keep any partially consumed stored catalyst |
| Condenser output below is blocked by a full item handler | Keep completed progress and retry output without clearing the source |

### 5. Good/Base/Bad Cases

- Good: `HeatSources.getHeatSourceValue` checks `MachineCasingBlockEntity.heatSourceValue()` before static block heat
  maps, so Crucible code stays ignorant of installed machine item classes.
- Base: Casing menus may expose one fuel slot while using DataSlots for active machine state.
- Bad: Crucible, screen, or recipe code checking `instanceof HeatProviderItem` directly instead of reading
  `HeatSources` or casing data.

### 6. Tests Required

- `./gradlew.bat compileJava`
- `./gradlew.bat runData` when installed-machine recipes or generated data change
- `./gradlew.bat build`
- `./gradlew.bat runGameTestServer` for casing block-entity or heat-source behavior changes
- `git diff --check`
- Add focused GameTests when reusable machine placement helpers exist for asserting install/remove, redstone disable,
  and Crucible heat transfer.

### 7. Wrong vs Correct

Wrong:
```java
// Leaks casing-machine knowledge into Crucible melting.
if (level.getBlockEntity(pos.below()) instanceof MachineCasingBlockEntity casing
        && casing.heater().getItem() instanceof HeatProviderItem) {
    return 10;
}
```

Correct:
```java
final int heat = HeatSources.getHeatSourceValue(level, this.worldPosition.below());
if (heat <= 0) {
    return 0;
}
```

## Condenser Recipe Contract

### 1. Scope / Trigger

Use this contract when changing `CondenserRecipe`, `CondenserRecipes`, condenser data generation, or
`MachineCasingBlockEntity` Condenser runtime behavior.

### 2. Signatures

- Recipe model:
  ```java
  public final class CondenserRecipe implements Recipe<CondenserRecipeInput>
  ```
- Runtime lookup:
  ```java
  public static Optional<RecipeHolder<CondenserRecipe>> find(
          ServerLevel level,
          ItemStack catalyst,
          CondenserRecipe.Source source
  )
  ```
- Casing runtime:
  ```java
  private void condense(ServerLevel level, MachineVariant condenserVariant)
  ```

### 3. Contracts

#### JSON Contract

Generated condenser recipes live under:

```text
src/generated/resources/data/skyresources3/recipe/condenser/<source>/<name>.json
```

Every condenser recipe must contain:

- `catalyst`: a vanilla/NeoForge `Ingredient`; one item is consumed when no stored catalyst charge remains.
- `source`: an object with `type` (`fluid` or `block`) and `id` (`namespace:path`) for the world block above the casing.
- `output`: a non-empty item stack.
- `parameter`: positive legacy timing value; runtime converts it to progress ticks through the installed Condenser speed.
- `group`: optional string, defaulting to empty.

#### Runtime Contracts

- Condenser recipes use the dedicated `skyresources3:condenser` type; do not encode fluid or block world sources in
  `SkyResourcesProcessRecipe`.
- `CondenserRecipes.find` is the server-side lookup path for runtime execution.
- The block above the casing is interpreted as a source fluid only when its fluid state is a source; otherwise a
  non-air block is matched by block id.
- Redstone power pauses the Condenser without clearing progress.
- Successful completion clears the source above the casing, clears adjacent flowing fluid blocks, and sends the output
  to an item handler below before falling back to dropping the item below.
- Initial generated recipes should stay to stable vanilla/SkyResources content until the mod/tag integration policy is
  explicit.

### 4. Validation Matrix

| Condition | Expected behavior |
|---|---|
| Slot item is not used by any condenser recipe | Reject through menu/capability insertion |
| Source above is air or non-matching | No craft; progress display resets |
| Source above is a flowing non-source fluid | No craft |
| Source and catalyst match but output handler below is full | Do not clear source; retry output later |
| No item handler exists below | Drop the condenser output below the casing |

### 5. Good/Base/Bad Cases

- Good: `MachineCasingBlockEntity` resolves a `CondenserRecipe.Source` from the world and calls `CondenserRecipes.find`.
- Base: Data generation creates one JSON recipe per stable vanilla output and source pair.
- Bad: Reusing `SkyResourcesProcessRecipe` for fluid/block world sources or duplicating condenser output tables in the
  block entity.

### 6. Tests Required

- `./gradlew.bat compileJava`
- `./gradlew.bat runData` when condenser recipes change
- `./gradlew.bat build`
- `./gradlew.bat runGameTestServer` for Condenser runtime or casing slot validation changes
- `git diff --check`

### 7. Wrong vs Correct

Wrong:
```java
// Duplicates recipe behavior in the machine runtime.
if (stack.is(ModItems.ORE_ALCHEMICAL_DUSTS.get(OreAlchemyDust.IRON).get())) {
    return new ItemStack(Items.IRON_INGOT);
}
```

Correct:
```java
final Optional<RecipeHolder<CondenserRecipe>> holder = CondenserRecipes.find(level, catalyst, source);
if (holder.isEmpty()) {
    return;
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
- Generate condenser recipes from `SkyResources3RecipeProvider`; keep them under `recipe/condenser/<source>/<name>.json`.
- Do not add fluid process JSON until the project has a fluid capability/storage design for 1.21.11.
- Do not generate dynamic old ore-dictionary integration recipes until the target mod/tag policy is explicit. Prefer stable vanilla/SkyResources recipes first.

---

## Common Mistakes

- Treating `parameter` as one universal concept. Its meaning depends on the process: heat, ticks, chance, per-tick catalyst drain, or health cost.
- Matching process names with ad hoc string literals outside `ProcessRecipes`.
- Using `ItemStack` as an input format when a tag-capable `Ingredient` is needed.
- Adding client-only recipe display or JEI classes to common recipe code.
- Reintroducing the old static mutable recipe manager lists instead of using data-pack recipes.
