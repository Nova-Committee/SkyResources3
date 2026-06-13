# Stage 64 - Guide Inline Action Markers

## Scope

Restore the practical part of the old guide rich text marker behavior by rendering selected guide actions inline inside page body text.

## Checklist

- [x] Create Trellis task and PRD.
- [x] Read guide specs and current `GuideScreen` rendering/click paths.
- [x] Add a lightweight `{action:n}` inline marker contract.
- [x] Render inline action chips inside guide body text.
- [x] Reuse existing link/recipe/image action dispatch from inline chips.
- [x] Keep bottom action list as fallback.
- [x] Strip inline markers from search matching.
- [x] Add representative inline markers to migrated guide text.
- [x] Update guide spec and migration/checklist notes.
- [x] Run compileJava, runData, build, runGameTestServer.
- [x] Chinese commit and archive Trellis task.
