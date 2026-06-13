# GUI Menu and Guide Automation Closeout Checklist

> Task: `.trellis/tasks/06-14-gui-menu-guide-automation-closeout`
> Date: 2026-06-14

## Scope

- [x] Add automated guide metadata integrity coverage without loading client-only screen classes.
- [x] Add automated menu registry coverage for every migrated machine menu type.
- [x] Keep real guide clicking, JEI interaction, and machine screen layout checks as manual release-prep items.

## Automated Coverage Added

- [x] `GuideMenuGameTests.guideDataIntegrity` verifies:
  - Guide categories, page title keys, and page text keys exist in `assets/skyresources3/lang/en_us.json`.
  - Guide page ids are unique and resolve through `GuidePages.find`.
  - Page icons and action icons resolve to non-empty item stacks.
  - `LINK` actions target existing guide pages.
  - `RECIPE` actions either use item-stack lookup or one of the registered `GuideRecipeTargets` constants.
  - `IMAGE` actions target an existing `GuideStructure`, have translated labels, and contain non-empty block icons.
  - Inline `{action:n}` markers in translated guide body text point to existing 1-based action indexes.
- [x] `GuideMenuGameTests.menuTypesResolve` verifies all 16 migrated `ModMenuTypes` entries resolve through the runtime menu registry with the expected `skyresources3` ids.
- [x] `ModGameTests` registers both tests in the NeoForge function-style GameTest registry.

## Still Manual Before Release

- [ ] Open the guide screen and click representative search results, link actions, image actions, and recipe actions.
- [ ] Verify JEI recipe action opens expected categories when JEI is present.
- [ ] Verify JEI absence/fallback behavior in a no-JEI client profile if packaging one.
- [ ] Open representative machine screens and inspect layout, slot positions, progress bars, and tooltips.
- [ ] Sanity-check island/team command flows in an interactive local client world.

## Validation

- [x] `./gradlew.bat compileJava`
- [x] `./gradlew.bat runGameTestServer`
- [x] `./gradlew.bat runData`
- [x] `./gradlew.bat build`
- [x] `git diff --check`
- [x] `git diff --cached --check`
- [x] `powershell -ExecutionPolicy Bypass -File "scripts/check-serena-java.ps1"`
