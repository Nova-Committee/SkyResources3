# Backend Development Guidelines

> Project-specific backend conventions for this NeoForge mod.

---

## Overview

SkyResources3 is currently a single-module Java 21 NeoForge mod. The codebase is small and still close to the NeoForge starter template, so these guidelines document the conventions that are present today instead of describing a larger architecture that does not exist yet.

Primary facts:

- Build system: Gradle with `net.neoforged.moddev` in `build.gradle`.
- Runtime target: Minecraft `1.21.11`, NeoForge `21.11.42`, Java `21`.
- Mod id: `skyresources3`, defined in `gradle.properties` and mirrored by `Skyresources3.MODID`.
- Base package: `committee.nova.mods.skyresources3`.
- Current code files: `Skyresources3.java` and `Config.java`.

---

## Pre-Development Checklist

Before editing backend code, read the relevant files below:

- Always read [Directory Structure](./directory-structure.md) for package, resource, registry, and metadata layout.
- Read [Database Guidelines](./database-guidelines.md) before adding persistence, config storage, generated data, or any external storage dependency.
- Read [Error Handling](./error-handling.md) before changing config validation, event handlers, startup logic, or registry code.
- Read [Logging Guidelines](./logging-guidelines.md) before adding or changing logs.
- Read [Recipe Guidelines](./recipe-guidelines.md) before adding or consuming custom processing recipes.
- Read [Island Command Guidelines](./island-command-guidelines.md) before changing VoidIslandControl, island, or team commands.
- Always read [Quality Guidelines](./quality-guidelines.md) before implementation and review.

Also read the shared thinking guides when a change spans multiple files or starts to repeat patterns:

- `.trellis/spec/guides/code-reuse-thinking-guide.md`
- `.trellis/spec/guides/cross-layer-thinking-guide.md`

---

## Guidelines Index

| Guide | Description | Status |
|-------|-------------|--------|
| [Directory Structure](./directory-structure.md) | Module organization, resource layout, naming, and examples | Baseline filled |
| [Database Guidelines](./database-guidelines.md) | Current no-database stance, config persistence, generated data, and dependency rules | Baseline filled |
| [Error Handling](./error-handling.md) | Config validation, event handling, startup failures, and API response stance | Baseline filled |
| [Recipe Guidelines](./recipe-guidelines.md) | Custom NeoForge process recipe type, JSON contract, matching, and datagen rules | Active |
| [Island Command Guidelines](./island-command-guidelines.md) | VoidIslandControl/team command signatures, saved data contracts, and validation matrix | Active |
| [Quality Guidelines](./quality-guidelines.md) | Code standards, forbidden patterns, tests, and review checks | Baseline filled |
| [Logging Guidelines](./logging-guidelines.md) | SLF4J usage, log levels, structured messages, and sensitive data rules | Baseline filled |

---

## Maintenance Rules

- Update these guidelines when the project establishes a real convention that differs from the current baseline.
- Prefer examples from existing files over hypothetical examples.
- Do not add aspirational architecture here until the codebase actually adopts it.
- Keep documentation in English so Trellis sub-agents receive consistent project instructions.
