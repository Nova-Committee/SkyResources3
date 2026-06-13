# Stage 40 - Aqueous Concentrator / Deconcentrator Checklist

## Scope

- Migrate the legacy Aqueous Concentrator and Aqueous Deconcentrator blocks.
- Preserve the legacy two-slot item inventory, 4000 mB water tank, 100000 FE energy buffer, and 100 progress operation.
- Share water extraction/insertion rules with the hand-held Water Extractor.
- Keep fluid process recipes out of datapack JSON until the project has a fluid process recipe contract.

## Legacy Behavior Ported

- Aqueous Concentrator:
  - Accepts water through the fluid capability.
  - Dirt + 200 mB water -> Clay.
  - Dehydrated Cactus + 1200 mB water -> Cactus.
  - Default speed 5, power usage 80 FE/t.
- Aqueous Deconcentrator:
  - Exposes stored water for extraction through the fluid capability.
  - Cactus -> Dehydrated Cactus + 50 mB water.
  - Snow -> 50 mB water with no item output.
  - Leaf block items -> 20 mB water with no item output.
  - Default speed 10, power usage 80 FE/t.

## Validation

- [x] `./gradlew.bat compileJava`
- [x] `./gradlew.bat runData`
- [x] `./gradlew.bat build`
- [x] `./gradlew.bat runGameTestServer`
- [x] `git diff --check`

## Notes

- The current thread's Serena MCP transport remains closed, but the configured wrapper command responds to an external JSONL MCP probe.
- A new Codex thread should be able to use Serena with the updated `cmd` wrapper.
