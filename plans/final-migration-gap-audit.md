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
| Blocks and items | Main families, tools, components, fluids, machine blocks, and island templates are migrated in staged checklists. | ARR resource reuse policy and final asset/model/lang audit still need a completion pass. Variant tinting/per-family textures are enhancements. | Must decide license; visual work is enhancement | `plans/migration-plan.md`, `plans/stage-46-ore-alchemy-dust-checklist.md`, `plans/stage-49-dirty-gem-item-family-checklist.md` |
| Recipes and data generation | Custom process recipe foundation, core fusion/combustion/crucible/condenser data, stable dirty gem recipes, and JEI displays are present. | Dynamic old ore-dictionary/tagged modded outputs wait for compatibility policy; fluid-capable process data only matters if a concrete machine or recipe needs it. | Allowed deferral unless a target integration is selected | `plans/stage-23-process-recipe-foundation-checklist.md`, `plans/stage-27-fusion-process-data-checklist.md`, `.trellis/spec/backend/recipe-guidelines.md` |
| Network | Old Fusion Table dump behavior is migrated through current networking. | Final validation should verify no remaining old network message surface exists beyond migrated GUI actions. | Must audit/verify | `plans/stage-29-fusion-table-runtime-checklist.md`, `src/main/java/committee/nova/mods/skyresources3/network/` |
| Menus and GUI | Multiple machine menus/screens and the guide screen are migrated; guide search/actions/structure preview and JEI recipe action are complete. | RunClient manual smoke test is still needed for core GUI surfaces. REI/EMI duplicate integrations are not required while JEI is active. | Must validate; alternate viewers deferred | `plans/stage-55-guide-page-foundation-checklist.md`, `plans/stage-58-guide-rich-text-actions-checklist.md`, `plans/stage-60-jei-recipe-viewer-integration-checklist.md` |
| Multiblock structures and machines | Life Infuser, Fusion Table, combustion machines, condenser, and other core machines have runtime slices and GameTests. | Focused controller priority/collector overflow GameTests remain enhancement coverage; RF/fluid heater variants require an energy/fluid compatibility decision. | Enhancement / allowed deferral | `plans/stage-17-life-infuser-runtime-checklist.md`, `plans/stage-29-fusion-table-runtime-checklist.md`, `plans/stage-39-combustion-automation-checklist.md` |
| Commands | Island/team command surface is migrated, including create/home/spawn/visit/reset/invite/accept/leave/trust/trusted/disband and saved offline identities. | Old VoidIslandControl event broadcast hooks need a decision: implement a small internal event API or document as intentionally dropped because no external VIC dependency remains. | Must decide | `plans/stage-18-void-island-commands-checklist.md`, `plans/stage-71-offline-identity-team-commands-checklist.md`, `.trellis/spec/backend/island-command-guidelines.md` |
| World generation | `skyresources3:void_island`, world preset, starter templates, magma Crystal Fluid, and spawn platform are migrated. | Decide whether existing overworld island records should be migrated to the void dimension. | Must decide for save compatibility; optional for new worlds | `plans/stage-22-void-island-world-checklist.md`, `plans/stage-70-magma-island-crystal-fluid-checklist.md` |
| VoidIslandControl built-in | Core gameplay is built in and no longer depends on the old 1.12.2 jar. | External profile lookup, rename conflict resolution, and full event broadcast remain out of the local-cache MVP. | Event/save decisions must be resolved; external lookup is enhancement | `plans/stage-68-island-reset-full-wipe-checklist.md`, `plans/stage-69-island-offline-visit-checklist.md`, `plans/stage-71-offline-identity-team-commands-checklist.md` |
| Team features | Team membership, shared home/info, configurable protection, trusted visitors, offline cached invite/trust are migrated. | Role matrix, shared death-home behavior, and richer cross-dimensional protection are enhancements. | Enhancement | `plans/stage-19-team-island-collaboration-checklist.md`, `plans/stage-67-team-island-trust-protection-checklist.md` |
| Optional integrations | JEI, Jade, and Integrated Dynamics minimum useful paths are implemented. | CraftTweaker, Forestry/Binnie's/Extra Bees, AE2, TConstruct, Thermal, IC2/Tech Reborn, and similar old integrations lack verified 1.21.11 NeoForge targets. | Allowed deferral with TODO | `plans/integration-availability.md` |

## Must Finish Before Calling Migration Complete

1. **Final validation pass**
   - Run `./gradlew.bat runData`, `./gradlew.bat runGameTestServer`, and `./gradlew.bat build` in the final state.
   - Run a targeted `runClient` smoke test for guide, JEI action fallback, and representative machine GUIs.

2. **Resource/license decision**
   - Decide whether legacy ARR resources can be reused in the MIT target project, must be replaced, or need a license note.
   - Record the decision in `plans/migration-plan.md` before completion.

3. **VoidIslandControl compatibility decisions**
   - Decide whether old event broadcast hooks are needed now that VIC is built in.
   - Decide whether existing overworld island saved records should migrate to `skyresources3:void_island`.
   - If either is intentionally deferred, record the reason as a non-blocking compatibility TODO.

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

1. **Final validation and stale-checklist cleanup**: run final gates, clean stale checklist language, and produce completion evidence.
2. **VoidIslandControl compatibility decision**: implement or explicitly defer event broadcast and old save migration.
3. **Resource/license closeout**: decide legacy ARR resource policy and update migration documentation.
