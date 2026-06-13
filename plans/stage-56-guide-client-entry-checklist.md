# Stage 56 - Guide Client Entry

> Scope: connect migrated guide page data to a minimal client-only screen and keybinding.

## Planned

- [x] Inspect current client setup and old guide/keybinding behavior.
- [x] Verify NeoForge 1.21.11 key mapping and client tick APIs against local classpath.
- [x] Add a client guide screen backed by `GuidePages`.
- [x] Register a default `G` guide keybinding.
- [x] Add English translations for screen labels and keybinding names.
- [x] Keep rich text, search, recipes, and image widgets deferred.

## Verification

- [x] `./gradlew.bat compileJava`
- [x] `./gradlew.bat runData`
- [x] `./gradlew.bat build`
- [x] `./gradlew.bat runGameTestServer`
- [x] `git diff --check`
- [x] `git diff --cached --check`
