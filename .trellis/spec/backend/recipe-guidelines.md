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

---

## Data Generation

Process recipes should be generated from `SkyResources3RecipeProvider` unless a task explicitly requires a hand-authored datapack example.

Guidelines:

- Use `ProcessRecipes` constants instead of repeating process-name string literals.
- Use `ProcessIngredient` for counted inputs rather than encoding repeated items as repeated list entries.
- Prefer item tags for old ore-dictionary-style inputs when the output does not depend on a specific variant.
- Generate one recipe per distinct old output/parameter pair. For example, rock grinder gravel -> sand and gravel -> flint remain separate recipes because their chance parameters differ.
- Do not add fluid process JSON until the project has a fluid capability/storage design for 1.21.11.

---

## Common Mistakes

- Treating `parameter` as one universal concept. Its meaning depends on the process: heat, ticks, chance, catalyst usage, or health cost.
- Matching process names with ad hoc string literals outside `ProcessRecipes`.
- Using `ItemStack` as an input format when a tag-capable `Ingredient` is needed.
- Adding client-only recipe display or JEI classes to common recipe code.
- Reintroducing the old static mutable recipe manager lists instead of using data-pack recipes.
