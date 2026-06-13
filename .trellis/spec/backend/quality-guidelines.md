# Quality Guidelines

> Code quality standards for backend/mod development.

---

## Overview

SkyResources3 is a Java 21 NeoForge mod. Keep changes small, direct, and aligned with the current project structure. The codebase does not yet have a custom formatter, lint tool, test suite, or layered architecture, so quality depends on matching the existing Gradle/NeoForge patterns and avoiding unnecessary abstractions.

Core principles for this repository:

- Keep the mod id, package namespace, resources, and generated metadata consistent.
- Use NeoForge registration/config/event APIs instead of custom frameworks.
- Add structure only when a feature outgrows the current simple layout.
- Prefer clear local code over speculative helpers.
- Do not introduce dependencies without a concrete task requirement.

---

## Forbidden Patterns

- Hard-coding a different mod id or namespace than `skyresources3`.
- Adding web backend layers such as controllers, repositories, DTOs, or database migrations without an actual API/storage requirement.
- Directly mutating registries instead of using NeoForge `DeferredRegister` patterns for mod content.
- Referencing client-only classes from common code without a `Dist.CLIENT` boundary or another NeoForge-safe side gate.
- Swallowing lifecycle, registry, or config exceptions with broad `catch (Exception)` blocks.
- Adding global mutable state unless it is part of an established NeoForge pattern such as loaded config values or deferred registry holders.
- Adding dependencies only for convenience when Java, Minecraft, or NeoForge APIs already cover the task.
- Leaving template/example content in production-facing resources when replacing it with real mod content.

---

## Required Patterns

- Use Java 21 language level, as configured by `java.toolchain.languageVersion`.
- Keep source packages under `committee.nova.mods.skyresources3`.
- Keep resource paths under the `skyresources3` namespace.
- Use `Skyresources3.MODID` for annotation and registry namespace references in Java.
- Register blocks, items, and creative tabs through `DeferredRegister` and register those deferred registers on the mod event bus.
- Keep config keys and runtime config fields centralized in `Config.java` until the file becomes too large.
- Use `ModConfigSpec` for mod config and register it through the `ModContainer`.
- Use SLF4J parameterized logs instead of string concatenation.
- Update `en_us.json` when adding user-visible blocks, items, tabs, or messages.
- Update `skyresources3.mixins.json` and create classes under the configured mixin package when adding mixins.
- For Minecraft `1.21.11` block-entity item automation, use NeoForge transfer APIs
  (`ResourceHandler<ItemResource>` and `ItemStacksResourceHandler`) instead of deprecated legacy item handlers.
- For Minecraft `1.21.11` block-entity fluid automation, use NeoForge transfer APIs
  (`ResourceHandler<FluidResource>` and `FluidStacksResourceHandler`) instead of legacy Forge fluid handlers.
- When porting entity-based behavior, verify the local `1.21.11` Parchment package names before importing classes.
  Several vanilla entities live in subpackages, for example `monster.skeleton.Skeleton`,
  `monster.spider.Spider`, `monster.zombie.ZombieVillager`, and `animal.squid.Squid`.
- Prefer current Parchment effect names in code: use `MobEffects.MINING_FATIGUE` and `MobEffects.SLOWNESS`
  instead of older mapping names such as `DIG_SLOWDOWN` and `MOVEMENT_SLOWDOWN`.

Current formatting conventions:

- Four-space indentation in Java.
- Opening braces on the same line.
- `final` is used for method parameters in existing event/config handlers.
- Simple one-line guards exist in template code, but use braces when a branch contains more than one action or would be hard to scan.

---

## Block Entity Item Transfer Contract

### 1. Scope / Trigger

Use this contract when a block entity exposes item storage to automation, menus, or machine runtime code.
This prevents new machines from mixing deprecated legacy inventory APIs with the NeoForge transfer API used by
Minecraft `1.21.11`.

### 2. Signatures

- Capability registration:
  ```java
  event.registerBlockEntity(
          Capabilities.Item.BLOCK,
          ModBlockEntityTypes.EXAMPLE.get(),
          (blockEntity, direction) -> blockEntity.getItemHandler()
  );
  ```
- Block entity exposure:
  ```java
  public ResourceHandler<ItemResource> getItemHandler()
  ```
- Persistent storage helper:
  ```java
  private static class StoredItemStacks extends ItemStacksResourceHandler
  ```

### 3. Contracts

- Storage state lives in the block entity and is serialized through `ValueOutput.putChild(key, handler)`.
- Deserialization uses `ValueInput.readChild(key, handler)` and must preserve the expected slot count.
- When migrating a machine from direct `ItemStack` fields to `ItemStacksResourceHandler`, read legacy slot keys
  (for example `gem` / `input`) only as a fallback when the new handler slot is still empty. New saves should use
  the handler child key only.
- `isValid(int, ItemResource)` defines external insertion rules; output slots should return `false` for insertion.
- Override `extract(int, ItemResource, int, TransactionContext)` when automation may only extract from selected slots.
- Internal machine code may mutate backing stacks directly, but it must call `setChanged()` in the same tick.

### 4. Validation & Error Matrix

| Condition | Expected behavior |
|---|---|
| Empty `ItemResource` passed to `isValid` | Return `false` |
| Insert into output-only slot | Return `false` from `isValid` |
| Extract from non-output slot through automation | Return `0` |
| Stored list deserializes with wrong slot count | Reset to the fixed expected size |
| New handler child exists and legacy slot keys also exist | Prefer the handler child value; do not merge duplicate stacks |
| Block is broken | Drop real inventory stacks only; do not drop ghost filters or virtual in-progress outputs |

### 5. Good/Base/Bad Cases

- Good: `FusionTableBlockEntity` uses `ItemStacksResourceHandler`, persists it as a child value, and registers
  `Capabilities.Item.BLOCK`.
- Base: A block entity with no automation may keep direct `ItemStack` fields, but should migrate to
  `ItemStacksResourceHandler` when exposing item automation.
- Bad: Adding new `IItemHandler` / `ItemStackHandler` usage for `1.21.11` machines when a transfer handler is needed.

### 6. Tests Required

- `./gradlew.bat compileJava` must pass after capability registration.
- `./gradlew.bat build` must pass to verify resources and packaging still load.
- `./gradlew.bat runGameTestServer` should be run for block-entity runtime changes.
- Add GameTests when a machine behavior can be asserted without manual client interaction.

### 7. Wrong vs Correct

Wrong:
```java
// Deprecated for new 1.21.11 machine automation.
private final ItemStackHandler items = new ItemStackHandler(11);
```

Correct:
```java
private final ItemStacksResourceHandler items = new ItemStacksResourceHandler(11);

public ResourceHandler<ItemResource> getItemHandler() {
    return this.items;
}
```

---

## Block Entity Fluid Transfer Contract

### 1. Scope / Trigger

Use this contract when a block entity exposes fluid storage to automation, machine runtime code, or player-held
fluid containers. This keeps new machines on the NeoForge transfer API and prevents reintroducing legacy Forge
`IFluidHandler` patterns.

### 2. Signatures

- Capability registration:
  ```java
  event.registerBlockEntity(
          Capabilities.Fluid.BLOCK,
          ModBlockEntityTypes.EXAMPLE.get(),
          (blockEntity, direction) -> blockEntity.getFluidHandler()
  );
  ```
- Block entity exposure:
  ```java
  public ResourceHandler<FluidResource> getFluidHandler()
  ```
- Persistent storage helper:
  ```java
  private static final class StoredFluidStacks extends FluidStacksResourceHandler
  ```

### 3. Contracts

- Storage state lives in the block entity and is serialized through `ValueOutput.putChild(key, handler)`.
- Deserialization uses `ValueInput.readChild(key, handler)` and must preserve the expected tank count.
- `isValid(int, FluidResource)` defines external insertion rules; reject empty resources.
- Use `ResourceHandlerUtil` or `Transaction`-backed insert/extract calls for automation moves.
- Use `FluidUtil.tryPlaceFluid` / `FluidUtil.tryPickupFluid` for world placement and pickup behavior when possible.

### 4. Validation & Error Matrix

| Condition | Expected behavior |
|---|---|
| Empty `FluidResource` passed to `isValid` | Return `false` |
| Stored list deserializes with wrong tank count | Reset to the fixed expected size |
| Neighbor has no `Capabilities.Fluid.BLOCK` provider | Skip that side without logging |
| Destination has no capacity for the resource | Move/extract returns `0` and leaves both handlers unchanged |
| World target cannot accept a fluid source | Do not drain or clear the machine tank |

### 5. Good/Base/Bad Cases

- Good: `FluidDropperBlockEntity` uses `FluidStacksResourceHandler`, persists it as a child value, and registers
  `Capabilities.Fluid.BLOCK`.
- Base: A block entity with no external fluid automation may keep no fluid handler at all.
- Bad: Adding new `IFluidHandler`, `FluidTank`, or legacy capability usage for `1.21.11` machines.

### 6. Tests Required

- `./gradlew.bat compileJava` must pass after capability registration.
- `./gradlew.bat build` must pass to verify resources and packaging still load.
- `./gradlew.bat runData` must pass when recipes, loot, or tags change.
- `./gradlew.bat runGameTestServer` should be run for block-entity runtime changes.
- Add GameTests when a fluid machine behavior can be asserted without manual client interaction.

### 7. Wrong vs Correct

Wrong:
```java
// Legacy Forge fluid API; do not add this to new 1.21.11 machines.
private final FluidTank tank = new FluidTank(1000);
```

Correct:
```java
private final FluidStacksResourceHandler fluids = new FluidStacksResourceHandler(1, FluidType.BUCKET_VOLUME);

public ResourceHandler<FluidResource> getFluidHandler() {
    return this.fluids;
}
```

---

## Testing Requirements

Current baseline:

- No test source set or project tests exist yet.
- NeoForge run configurations exist for `client`, `server`, `gameTestServer`, and `data`.
- `gameTestServer` is configured with `neoforge.enabledGameTestNamespaces` set to the project mod id.

For code changes:

- Run `./gradlew build` when feasible.
- Run the relevant NeoForge run task when a change depends on client, server, data generation, or GameTest behavior.
- Add GameTests when adding behavior that can be validated without manual gameplay.
- For docs-only changes, verify the edited Markdown has no placeholders and references real files or current project facts.

### GameTest Registration Convention

For Minecraft `1.21.11` / NeoForge `21.11.42`, register reusable GameTest functions through the vanilla test-function
registry and register test instances on the mod event bus:

```java
public static final DeferredRegister<Consumer<GameTestHelper>> TEST_FUNCTIONS =
        DeferredRegister.create(BuiltInRegistries.TEST_FUNCTION, Skyresources3.MODID);

private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> EXAMPLE =
        TEST_FUNCTIONS.register("example", () -> ExampleGameTests::run);

public static void register(final IEventBus modEventBus) {
    TEST_FUNCTIONS.register(modEventBus);
    modEventBus.addListener(ModGameTests::registerGameTests);
}
```

Use `RegisterGameTestsEvent` to register function-based test instances. Command-only tests may use the vanilla
`minecraft:empty` structure and a no-op `TestEnvironmentDefinition.AllOf(List.of())` environment. Prefer assertions
against durable state such as `SavedData` when a command mutates server data.

When a GameTest command resolves online players by name, do not create multiple players with repeated
`GameTestHelper#makeMockServerPlayerInLevel()` calls: the vanilla helper uses the fixed profile name
`test-mock-player`. Create named mock `ServerPlayer` instances and register them through `PlayerList#placeNewPlayer`
so `/command <player>` resolves the intended target.

If a verification command fails because of pre-existing project state, record the command and the failure clearly in the task summary.

---

## Code Review Checklist

Reviewers should check:

- The change stays under the correct package and resource namespace.
- Java constants, Gradle properties, metadata templates, and resources remain aligned.
- New registry entries use lower snake case ids and deferred registration.
- Client-only code is side-gated.
- Config values are validated before use.
- Logs use placeholders and do not expose secrets or unnecessary player identifiers.
- No new architecture layer or dependency was added without a concrete need.
- User-visible content has corresponding language entries.
- Mixin additions update both the JSON config and Java package.
- Verification commands were run or the reason they were not run is documented.
