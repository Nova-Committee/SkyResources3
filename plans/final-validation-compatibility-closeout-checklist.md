# Final Validation and Compatibility Closeout Checklist

> Task: `.trellis/tasks/06-14-final-validation-compatibility-closeout`
> Date: 2026-06-14
> Scope source: `plans/ask.md`, `plans/final-migration-gap-audit.md`

## Scope

- [x] Treat the migrated main mod body as feature-complete enough for final closeout review.
- [x] Keep optional integrations without verified Minecraft 1.21.11 NeoForge artifacts as documented TODOs.
- [x] Avoid adding new gameplay scope unless final validation reveals a real build, datagen, or runtime startup defect.

## Compatibility Decisions

- [x] Legacy ARR resources are not automatically relicensed by being present in the MIT target project. Until the project owner records an explicit relicensing decision, migrated legacy assets must be treated as project-provided/legacy-derived assets that need a license note or later replacement.
- [x] Repository license files are now present: `LICENSE` contains the MIT license matching `gradle.properties`, and `RESOURCE_LICENSE.md` documents the conservative boundary for legacy ARR-derived resources.
- [x] Old VoidIslandControl event broadcast hooks are not a migration blocker for this task. The current mod has the core behavior built in and no verified external 1.12.2 VIC API consumers in the 1.21.11 target.
- [x] Existing overworld island saved records should not be silently moved to `skyresources3:void_island`. If old-save support becomes required, add an explicit migration command/tool so the owner can control the operation.
- [x] External Mojang profile lookup, rename history, and duplicate cached-name conflict handling stay outside the local-cache MVP.

## Final Validation

- [x] `./gradlew.bat runData` passed again on 2026-06-14 after the combustion automation closeout; data generator reported `BUILD SUCCESSFUL` and `written: 0`.
- [x] `./gradlew.bat runGameTestServer` passed again on 2026-06-14 with Gradle exit code 0.
- [x] `./gradlew.bat build` passed again on 2026-06-14; final task result was `BUILD SUCCESSFUL`.
- [x] Controlled `./gradlew.bat runClient --no-daemon` startup smoke captured client loading evidence on 2026-06-14; see `plans/client-smoke-resource-license-closeout-checklist.md`.
- [x] A later desktop GUI smoke attempt on 2026-06-14 reached an existing local singleplayer world and found a guide-key
  default conflict with Minecraft 1.21.11 `key.quickActions`; the guide default key was moved from `G` to `Y`.
- [x] Guide/menu automated integrity coverage was added on 2026-06-14:
  - `GuideMenuGameTests.guide_data_integrity` verifies guide translation keys, inline action markers, link targets, recipe targets, structure targets, and structure block icons.
  - `GuideMenuGameTests.menu_type_registration` verifies all migrated machine menu type ids resolve through the runtime registry.
- [x] Combustion automation regression coverage was added and passed on 2026-06-14:
  - `MachineRuntimeGameTests.combustionControllerUsesFilterPriority` verifies Smart Combustion Controller left-to-right filter priority when multiple chamber recipes are possible.
  - `MachineRuntimeGameTests.combustionCollectorDropsOverflow` verifies Combustion Collector partial insertion and chamber drop fallback when output capacity is blocked.
- [x] `git diff --check` passed on 2026-06-14 during the refreshed final validation run.
- [x] `git diff --cached --check` passed on 2026-06-14 during the refreshed final validation run.
- [x] `powershell -ExecutionPolicy Bypass -File "scripts/check-serena-java.ps1"` passed on 2026-06-14; Serena resolved the project as Java with LSP configuration.
- [x] Legacy network/resource scan passed again on 2026-06-14:
  - No `SimpleNetworkWrapper`, `IMessage`, old `DumpMessage`, `NetworkRegistry`, `PacketBuffer`, `com.bartz24.skyresources`, or `voidislandcontrol` references were found under runtime Java/resources.
  - No non-`skyresources3` `skyresources` namespace references were found in runtime/generated JSON/TOML/MCMeta resources.

## Manual Validation

- [ ] `./gradlew.bat runClient` GUI interaction smoke test is still required before a release tag because a timed automated startup can prove client boot/loading, but not user-facing clicks:
  - Follow `plans/client-manual-smoke-runbook.md` and record the result.
  - Use the configured `key.skyresources3.guide` key; the default is now `Y`.
  - Guide screen opens and search/action links work.
  - JEI recipe action degrades safely when JEI is absent or opens recipes when JEI is present.
  - Representative machine menus open without client-only classloading or layout errors.
  - Island/team command flows are manually sanity-checked in a local world.
  - Headless Gradle gates and GameTests must not be treated as proof for this visual/click-through item.

## Remaining Non-Blocking TODOs

- Optional integrations listed in `plans/integration-availability.md` may continue once compatible target artifacts and APIs are verified.
- Dynamic modded ore/tag recipe output remains deferred until a target integration policy exists.
- Richer team role matrix, shared death-home behavior, old-save migration tooling, and additional targeted GameTests beyond current guide/menu/combustion coverage are enhancements rather than blockers for the main migration body.
