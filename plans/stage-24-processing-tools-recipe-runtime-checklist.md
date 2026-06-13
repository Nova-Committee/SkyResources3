# Stage 24 - Processing Tools Recipe Runtime Checklist

> Scope: make migrated hand-processing tools consume the `skyresources3:process` recipe type at runtime.

## Completed

- [x] Read current cutting knife and rock grinder event implementations.
- [x] Compared old 1.12.2 `ItemKnife` and `ItemRockGrinder` recipe-manager behavior.
- [x] Switched cutting knife block output lookup from hardcoded tables to `ProcessRecipes.find`.
- [x] Switched rock grinder drops from hardcoded tables to `ProcessRecipes.findAll`, preserving multi-output inputs such as gravel.
- [x] Kept item destroy-speed checks as narrow built-in hints because `Item#getDestroySpeed` has no `Level` recipe context.
- [x] Recorded the runtime recipe lookup rule in `.trellis/spec/backend/recipe-guidelines.md`.
- [x] Added command-free GameTests for cutting knife and rock grinder block-break event handlers, proving migrated runtime outputs come from the process recipe set.

## Verification

- [x] `./gradlew.bat compileJava`
- [x] `./gradlew.bat build`
- [x] `./gradlew.bat runGameTestServer`
- [x] `git diff --check`
- [x] `git diff --cached --check`

## Deferred

- [ ] Switch infusion stone runtime behavior to `skyresources3:process` in a focused follow-up slice.
