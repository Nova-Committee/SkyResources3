# Final Migration Gap Audit

> Audit date: 2026-06-14
> Source requirement: `plans/ask.md`
> Current plan baseline: `plans/migration-plan.md`

## Summary

SkyResources3 has migrated the main NeoForge 1.21.11 mod body far enough that most remaining items are no longer
core runtime implementation gaps. The remaining work falls into three groups:

1. **Must finish before declaring full migration complete**: final validation, stale checklist cleanup, license/resource
   decision, and explicit decisions for old VoidIslandControl compatibility behavior.
2. **Allowed deferrals under `plans/ask.md`**: optional integrations without verified 1.21.11 NeoForge artifacts, dynamic
   ore-dictionary/tagged modded recipes that depend on those integrations, and external profile lookup.
3. **Enhancements**: visual variants, richer team roles, old save migration, additional focused GameTests, and deeper
   compatibility APIs.

## Requirement Coverage

| `plans/ask.md` scope | Current status | Remaining gap | Classification | Evidence |
|---|---|---|---|---|
| Blocks and items | Main families, tools, components, fluids, machine blocks, and island templates are migrated in staged checklists. | ARR resource reuse policy is recorded; final asset/model/lang audit remains part of validation. Variant tinting/per-family textures are enhancements. | License decision recorded; visual work is enhancement | `plans/migration-plan.md`, `plans/stage-46-ore-alchemy-dust-checklist.md`, `plans/stage-49-dirty-gem-item-family-checklist.md`, `plans/final-validation-compatibility-closeout-checklist.md` |
| Recipes and data generation | Custom process recipe foundation, core fusion/combustion/crucible/condenser data, stable dirty gem recipes, and JEI displays are present. | Dynamic old ore-dictionary/tagged modded outputs wait for compatibility policy; fluid-capable process data only matters if a concrete machine or recipe needs it. | Allowed deferral unless a target integration is selected | `plans/stage-23-process-recipe-foundation-checklist.md`, `plans/stage-27-fusion-process-data-checklist.md`, `.trellis/spec/backend/recipe-guidelines.md` |
| Network | Old Fusion Table dump behavior is migrated through current networking. | Final validation found no old Forge network API/message names or old package references in runtime Java/resources. | Verified in closeout | `plans/stage-29-fusion-table-runtime-checklist.md`, `src/main/java/committee/nova/mods/skyresources3/network/`, `plans/final-validation-compatibility-closeout-checklist.md` |
| Menus and GUI | Multiple machine menus/screens and the guide screen are migrated; guide search/actions/structure preview and JEI recipe action are complete. | RunClient manual smoke test is still needed for core GUI surfaces. REI/EMI duplicate integrations are not required while JEI is active. | Must validate; alternate viewers deferred | `plans/stage-55-guide-page-foundation-checklist.md`, `plans/stage-58-guide-rich-text-actions-checklist.md`, `plans/stage-60-jei-recipe-viewer-integration-checklist.md` |
| Multiblock structures and machines | Life Infuser, Fusion Table, combustion machines, condenser, and other core machines have runtime slices and GameTests. | Focused controller priority/collector overflow GameTests remain enhancement coverage; RF/fluid heater variants require an energy/fluid compatibility decision. | Enhancement / allowed deferral | `plans/stage-17-life-infuser-runtime-checklist.md`, `plans/stage-29-fusion-table-runtime-checklist.md`, `plans/stage-39-combustion-automation-checklist.md` |
| Commands | Island/team command surface is migrated, including create/home/spawn/visit/reset/invite/accept/leave/trust/trusted/disband and saved offline identities. | Old VoidIslandControl event broadcast hooks are intentionally not implemented without a verified current consumer. | Resolved non-blocking compatibility enhancement | `plans/stage-18-void-island-commands-checklist.md`, `plans/stage-71-offline-identity-team-commands-checklist.md`, `.trellis/spec/backend/island-command-guidelines.md`, `plans/final-validation-compatibility-closeout-checklist.md` |
| World generation | `skyresources3:void_island`, world preset, starter templates, magma Crystal Fluid, and spawn platform are migrated. | Existing overworld island records should not be silently migrated; add an explicit migration tool only if old-save support becomes a release requirement. | Resolved non-blocking save-compatibility enhancement | `plans/stage-22-void-island-world-checklist.md`, `plans/stage-70-magma-island-crystal-fluid-checklist.md`, `plans/final-validation-compatibility-closeout-checklist.md` |
| VoidIslandControl built-in | Core gameplay is built in and no longer depends on the old 1.12.2 jar. | External profile lookup, rename conflict resolution, and full event broadcast remain out of the local-cache MVP. | Resolved as enhancements | `plans/stage-68-island-reset-full-wipe-checklist.md`, `plans/stage-69-island-offline-visit-checklist.md`, `plans/stage-71-offline-identity-team-commands-checklist.md`, `plans/final-validation-compatibility-closeout-checklist.md` |
| Team features | Team membership, shared home/info, configurable protection, trusted visitors, offline cached invite/trust are migrated. | Role matrix, shared death-home behavior, and richer cross-dimensional protection are enhancements. | Enhancement | `plans/stage-19-team-island-collaboration-checklist.md`, `plans/stage-67-team-island-trust-protection-checklist.md` |
| Optional integrations | JEI, Jade, and Integrated Dynamics minimum useful paths are implemented. | CraftTweaker, Forestry/Binnie's/Extra Bees, AE2, TConstruct, Thermal, IC2/Tech Reborn, and similar old integrations lack verified 1.21.11 NeoForge targets. | Allowed deferral with TODO | `plans/integration-availability.md` |

## 2026-06-14 Closeout Decisions

- **Resource/license policy**: legacy ARR resources are not automatically relicensed as MIT by being migrated into the target project. Treat copied or legacy-derived assets as requiring an explicit owner relicensing note or later replacement before a public release claim.
- **VoidIslandControl event hooks**: full old VIC event-broadcast compatibility is intentionally not implemented in this closeout because the core gameplay is now built in and no current 1.21.11 external consumer has been verified.
- **Old overworld island records**: do not silently migrate saved island records to `skyresources3:void_island`. If old-save migration becomes required, add a deliberate command/tool so the owner controls the change.
- **Manual GUI validation**: `runClient` remains a release-prep manual smoke test for guide actions, JEI behavior, and representative machine screens; automated CLI validation must not claim visual GUI coverage.

## 2026-06-14 Validation Evidence

- `./gradlew.bat runData` passed; data generation reported `BUILD SUCCESSFUL` and did not write generated file changes.
- `./gradlew.bat runGameTestServer` passed with Gradle exit code 0.
- `./gradlew.bat build` passed; Gradle reported `BUILD SUCCESSFUL`.
- `git diff --check`, `git diff --cached --check`, and `scripts/check-serena-java.ps1` passed.
- Targeted scans found no old Forge network API/message names, old `com.bartz24.skyresources` package references, `voidislandcontrol` runtime references, or non-`skyresources3` resource namespace references in runtime/generated JSON/TOML/MCMeta resources.

## Must Finish Before Calling Migration Complete

1. **Final validation pass**
   - Run `./gradlew.bat runData`, `./gradlew.bat runGameTestServer`, and `./gradlew.bat build` in the final state.
   - Run or explicitly track a targeted `runClient` smoke test for guide, JEI action fallback, and representative machine GUIs. In this automated closeout, this remains a manual release-prep item unless an interactive client session is completed.

2. **Resource/license decision**
   - Resolved in `plans/migration-plan.md`: legacy ARR resources need explicit owner relicensing, a license note, or replacement before public MIT-release claims.

3. **VoidIslandControl compatibility decisions**
   - Resolved in `plans/migration-plan.md`: old event broadcast hooks and silent old-save dimension migration are intentionally deferred/non-blocking unless a real consumer or upgrade requirement appears.

4. **Checklist cleanup**
   - Remove or reclassify stale deferred items that were completed by later stages.
   - Keep true version/API blockers in `plans/integration-availability.md` instead of scattering them across old stage files.

## Allowed Deferrals

- Optional mod integrations without verified 1.21.11 NeoForge artifacts.
- Dynamic old ore-dictionary/tagged recipes that depend on unavailable target mods or an explicit compatibility policy.
- CraftTweaker scripting until a compatible target artifact/API is verified.
- External Mojang profile lookup, rename history, and duplicate cached-name conflict resolution.
- RF/fluid heater variants unless the target energy/fluid integration policy requires them.

## Enhancement Backlog

- Per-ore/per-gem tinting or separate textures for migrated dust/gem families.
- Additional GameTests for combustion controller priority and collector overflow.
- Richer team role matrix and shared death-home behavior.
- Old save migration from overworld islands into `skyresources3:void_island`.
- Alternative recipe viewers such as REI/EMI or WTHIT duplicate probe support.

## Recommended Next Large Tasks

1. **Final validation evidence**: run final gates and fill `plans/final-validation-compatibility-closeout-checklist.md`.
2. **Manual client smoke test before release**: verify guide actions, JEI behavior, representative machine GUIs, and island/team flows through `runClient`.
3. **Optional enhancement/integration backlog**: continue only after compatible target artifacts or a concrete release requirement exists.
