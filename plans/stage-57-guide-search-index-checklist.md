# Stage 57 - Guide Search Index

> Scope: add current-category search filtering and a clickable page index to the migrated guide screen.

## Planned

- [x] Inspect old `GuideGUI` search/list behavior.
- [x] Verify Minecraft 1.21.11 `EditBox` and click handling APIs against local classpath.
- [x] Add guide-page filtering support without duplicating page data.
- [x] Add search input and result count to `GuideScreen`.
- [x] Render clickable guide-page result entries.
- [x] Add English translations for search and empty-result labels.

## Completed Follow-up

- [x] Cross-category search.
- [x] Scrollable result list.

## Deferred

- [x] Old rich-text recipe/link/image buttons have a lightweight `{action:n}` inline action replacement for selected migrated pages.

## Verification

- [x] `./gradlew.bat compileJava`
- [x] `./gradlew.bat runData`
- [x] `./gradlew.bat build`
- [x] `./gradlew.bat runGameTestServer`
- [x] `git diff --check`
- [x] `git diff --cached --check`
