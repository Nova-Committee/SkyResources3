# Stage 55 - Guide Page Data Foundation

> Scope: migrate the old `SkyResourcesGuide` / `ModGuidePages` page registry into a 1.21.11-friendly data foundation.

## Completed

- [x] Read old `SkyResourcesGuide`, `ModGuidePages`, and guide language entries.
- [x] Confirmed target project had no guide-page data model yet.
- [x] Added immutable `GuidePage` metadata with category/title/text translation keys and lazy icon stacks.
- [x] Added `GuidePages` as the central page index for migrated core pages.
- [x] Migrated stage 1 through stage 4 core page ids that reference target-available items or blocks.
- [x] Added English category, title, and body translations for migrated pages.

## Deferred

- [ ] Full client guide GUI, search, navigation, and keybinding remain for a focused UI slice.
- [x] Old rich text markers such as `<recipe>`, `<link>`, and `<image>` now have a lightweight `{action:n}` inline action replacement for selected migrated pages.
- [ ] Structure image data from old `ModGuidePages.imageDesigns` waits for a renderable guide-screen contract.
- [ ] Integration guide pages wait for the 1.21.11 mod compatibility policy.

## Verification

- [x] `./gradlew.bat compileJava`
- [x] `./gradlew.bat runData`
- [x] `./gradlew.bat build`
- [x] `./gradlew.bat runGameTestServer`
- [x] `git diff --check`
- [x] `git diff --cached --check`
