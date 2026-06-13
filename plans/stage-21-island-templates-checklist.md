# Stage 21 - Island Templates Checklist

## Scope

- Add built-in island starter template ids: `grass`, `sand`, `snow`, `wood`, `gog`, and `magma`.
- Support `/island create <type>` while keeping `/island create` as the grass default.
- Support `/island reset <type>` and `/island reset <type> confirm` while keeping the stored-type reset flow.
- Persist the selected island type in `IslandSavedData` with old records defaulting to `grass`.
- Port the SkyResources VoidIslandControl magma starter shape using current `petrified_wood` and `magmafied_stone` blocks.

## Evidence

- `IslandTemplate` centralizes starter island generation and command suggestions.
- `IslandSavedData.IslandRecord` stores `type` through the saved-data codec.
- `VoidIslandCommands` parses, validates, suggests, creates, resets, and reports island types through localized messages.
- `island-command-guidelines.md` documents command signatures and saved-data compatibility.

## TODO

- Crystal fluid placement from the original magma island is skipped until the SkyResources3 fluid registry migrates crystal fluid.
- Garden of Glass uses a vanilla placeholder until a compatible Botania/Garden of Glass integration is available.
- Void world/preset and true spawn island dimension handling remain pending for a later Stage 7 slice.

## Verification

- [x] `./gradlew.bat compileJava`
- [x] `./gradlew.bat runData`
- [x] `./gradlew.bat build`
- [x] `./gradlew.bat runGameTestServer`
- [x] `git diff --check`
- [x] `git diff --cached --check`
