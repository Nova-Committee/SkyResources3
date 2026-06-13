# Stage 30 - Quick Dropper Checklist

> Scope: migrate the old `QuickDropper` block, one-slot GUI, inventory transfer, recipe, and redstone-stopped drop behavior.

## Plan

- Read old `BlockQuickDropper`, `TileQuickDropper`, container, GUI, registry, recipe, and resources.
- Keep the 1.21.11 id as `quick_dropper` while preserving the old user-facing name "Quick Dropper".
- Use the existing block entity, menu, screen, capability, datagen, and resource conventions from the current project.
- Preserve old runtime semantics: one slot, drops the whole stored stack below when unpowered and the block below is not sturdy.

## Verification Target

- [x] `./gradlew.bat compileJava`
- [x] `./gradlew.bat runData`
- [x] `./gradlew.bat build`
- [x] `./gradlew.bat runGameTestServer`
- [x] `git diff --check`

## Notes

- No dedicated GameTest class exists in the project yet; `runGameTestServer` was kept as the current server-side loading and registry verification gate for this slice.
