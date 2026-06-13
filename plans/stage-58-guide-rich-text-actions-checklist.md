# Stage 58 - Guide Rich Text Actions

> Scope: restore old guide `<recipe>`, `<link>`, and `<image>` semantics as a simple page action strip.

## Planned

- [x] Inspect old guide button behavior and structure image registrations.
- [x] Verify local 1.21.11 tooltip/click APIs.
- [x] Add guide action data model.
- [x] Add lightweight guide structure preview data.
- [x] Attach high-value link, recipe, and image actions to migrated guide pages.
- [x] Render and click guide actions in `GuideScreen`.
- [x] Add English translations for action labels, tooltips, and feedback.
- [x] Record the guide action contract in `.trellis/spec/backend/guide-guidelines.md`.

## Deferred

- [x] Inline placement matching old text markers through lightweight `{action:n}` markers.
- [x] JEI recipe viewer integration was completed in Stage 60; EMI/REI duplicate viewer support remains optional.
- [x] Rotating 3D structure renderer replaced for this port by a lower-risk 2D isometric `GuideScreen` structure preview.
- [x] Scrollable action/structure lists.

## Verification

- [x] `./gradlew.bat compileJava`
- [x] `./gradlew.bat runData`
- [x] `./gradlew.bat build`
- [x] `./gradlew.bat runGameTestServer`
- [x] `git diff --check`
- [x] `git diff --cached --check`
