# Optional Integration Guidelines

> Contracts for optional mod integrations in SkyResources3.

---

## Overview

SkyResources3 targets Minecraft `1.21.11` and NeoForge `21.11.42`. External mod integrations are optional compatibility layers, not core runtime dependencies. A compatibility layer may only be added after the target mod has a verified `1.21.11` NeoForge artifact and an API that matches the feature being migrated.

Use `plans/integration-availability.md` as the current audit index before adding any integration dependency.

---

## 1. Scope / Trigger

Read this guide before:

- Adding a dependency for JEI, REI, EMI, Jade, WTHIT, CraftTweaker, Integrated Dynamics, or another optional mod.
- Porting any old `com.bartz24.skyresources.jei` class.
- Porting any old `com.bartz24.skyresources.plugin.*` class.
- Adding runtime checks for an optional mod id.
- Changing guide recipe actions to open an external recipe viewer.

---

## 2. Signatures

Version audit endpoint:

```text
https://api.modrinth.com/v2/project/<slug>/version?game_versions=["1.21.11"]&loaders=["neoforge"]
```

Candidate Modrinth Maven repository:

```gradle
repositories {
    exclusiveContent {
        forRepository {
            maven {
                name = "Modrinth"
                url = "https://api.modrinth.com/maven"
            }
        }
        filter {
            includeGroup "maven.modrinth"
        }
    }
}
```

Candidate dependency shape:

```gradle
dependencies {
    compileOnly "maven.modrinth:<slug>:<version>"
    runtimeOnly "maven.modrinth:<slug>:<version>"
}
```

Runtime optional-mod gate:

```java
ModList.get().isLoaded("<modid>")
```

---

## 3. Contracts

- Prefer the target mod's official developer Maven and API docs when available.
- Modrinth Maven coordinates are allowed only when the POM resolves and the task records that Modrinth Maven has no transitive dependency metadata.
- Optional integration code must live behind an explicit loaded-mod check or the integration framework's plugin annotation/loader contract.
- Core gameplay, recipe loading, registries, datagen, and server startup must work when the optional mod is absent.
- Client-only integration APIs must not be referenced from common/server startup paths unless NeoForge side loading guarantees the class is not resolved there.
- Do not add a dependency just to inspect item ids or tags; prefer vanilla/NeoForge registries and tags for generic compatibility.

---

## 4. Validation & Error Matrix

| Condition | Expected behavior |
|---|---|
| Target mod has no verified `1.21.11` NeoForge artifact | Keep the integration in `plans/integration-availability.md` as TODO and do not add a Gradle dependency |
| Maven POM or official Maven coordinate does not resolve | Do not commit the dependency; record the blocker in the task research |
| Optional mod is absent at runtime | SkyResources3 still starts and the feature silently degrades or uses in-mod fallback text |
| Client recipe viewer is absent | Guide recipe actions show the existing in-mod feedback instead of crashing |
| Integration API is client-only | Keep references in client-only classes or plugin callbacks only |
| External API changed from the old 1.12.2 API | Port the behavior to the new API directly; do not recreate the old wrapper hierarchy unless it removes real duplication |

---

## 5. Good/Base/Bad Cases

- Good: Add JEI as a small compatibility task after verifying `1.21.11` NeoForge artifacts, keep the API classes in a dedicated client/plugin package, and leave normal recipes usable without JEI.
- Base: Keep guide actions internal and show a translated message when no recipe viewer integration exists.
- Bad: Add CraftTweaker or AE2 to `build.gradle` when the audit has no verified `1.21.11` NeoForge artifact.
- Bad: Import a client-only recipe viewer class from `Skyresources3.java`, registry classes, common block entities, or datagen providers.

---

## 6. Tests Required

For docs-only audit updates:

- `git diff --check`
- `git diff --cached --check`

For dependency or code integration:

- `./gradlew.bat compileJava`
- `./gradlew.bat build`
- `./gradlew.bat runData` if recipes, tags, generated assets, or guide data changed
- `./gradlew.bat runGameTestServer` when runtime machine behavior changes
- A local run with the optional mod absent
- A local run with the optional mod present when the integration is client-visible

---

## 7. Wrong vs Correct

Wrong:

```gradle
dependencies {
    implementation "maven.modrinth:crafttweaker:unknown"
}
```

Correct:

```gradle
dependencies {
    compileOnly "maven.modrinth:jei:27.4.0.22"
    runtimeOnly "maven.modrinth:jei:27.4.0.22"
}
```

The correct form is still only a candidate until the implementing task verifies the API package, Gradle resolution, and runtime behavior.
