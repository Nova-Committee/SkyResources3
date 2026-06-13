# Client Smoke and Resource License Closeout Checklist

> Task: `.trellis/tasks/06-14-client-smoke-resource-license-closeout`
> Date: 2026-06-14

## Scope

- [x] Add repository MIT license text to match `gradle.properties` `mod_license=MIT`.
- [x] Add a conservative resource license note for legacy ARR-derived assets.
- [x] Attempt a controlled `runClient` startup smoke without leaving the client running indefinitely.
- [x] Keep visual GUI interaction as a separate manual release-prep item.

## License Closeout

- [x] `LICENSE` added with MIT license text for new project code/documentation.
- [x] `RESOURCE_LICENSE.md` added to document that legacy ARR-derived textures, models, language, guide, or other assets are not automatically relicensed as MIT.
- [x] `plans/migration-plan.md`, `plans/final-migration-gap-audit.md`, and `plans/final-validation-compatibility-closeout-checklist.md` reference the license/resource boundary.

## Client Startup Smoke

- [x] Controlled command: `./gradlew.bat runClient --no-daemon`, started through a PowerShell watchdog on 2026-06-14.
- [x] The watchdog stopped the process after startup evidence was captured, so no Gradle/Java client process was intentionally left running.
- [x] Startup evidence was refreshed on 2026-06-14 at 04:12; see `plans/client-manual-smoke-runbook.md` for the manual follow-up package.
- [x] Evidence observed in `run/logs/latest.log`:
  - `Mod List` included `SkyResources3 1.0.0 (skyresources3)`.
  - Client environment reached `Render thread`.
  - Minecraft reported `Backend library: LWJGL version 3.3.3+5`.
  - ResourceManager reloaded `mod/skyresources3`, `mod/jade`, `mod/neoforge`, and `mod/jei`.
  - `SkyResources3 common setup complete` was logged.
  - Jade loaded `committee.nova.mods.skyresources3.integration.jade.SkyResourcesJadePlugin`.
  - OpenAL initialized and the sound engine started.
  - Minecraft and JEI texture atlases were created, including `minecraft:textures/atlas/gui.png-atlas` and `jei:textures/atlas/gui.png-atlas`.
- [x] No crash marker was observed in the captured log tail.

## Client GUI Smoke Execution Attempt

- [x] A later desktop attempt on 2026-06-14 reached an existing local singleplayer world; see
  `plans/client-gui-smoke-execution-evidence.md`.
- [x] The attempt found that the guide default key `G` conflicted with Minecraft 1.21.11 `key.quickActions`.
  The default guide key was moved to `Y`.
- [x] No intentional Gradle/Java client process was left running after the GUI attempt.

## Still Manual Before Release

- [ ] Follow `plans/client-manual-smoke-runbook.md` in a focused local client world and record the remaining result.
- [ ] Open guide screen and verify search/action links by clicking them.
- [ ] Verify JEI recipe action opens recipes when JEI is present.
- [ ] Verify JEI absence/fallback behavior in a separate no-JEI client run if packaging a no-JEI profile.
- [ ] Open representative machine menus and confirm layout/slot behavior visually.
- [ ] Sanity-check island/team flows in a local client world.

## Later Automation Evidence

- [x] `GuideMenuGameTests.guide_data_integrity` now covers guide page/action/structure/translation consistency without claiming visual click coverage.
- [x] `GuideMenuGameTests.menu_type_registration` now covers all migrated machine menu type registry ids without loading client-only screen classes.

## Validation

- [x] `./gradlew.bat build`
- [x] `git diff --check`
- [x] `git diff --cached --check`
- [x] `powershell -ExecutionPolicy Bypass -File "scripts/check-serena-java.ps1"`
