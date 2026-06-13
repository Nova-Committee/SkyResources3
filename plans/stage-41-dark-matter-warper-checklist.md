# Stage 41 - Dark Matter Warper Checklist

## Scope

- Migrate the legacy Dark Matter Warper block, block entity, menu, and screen.
- Preserve the one-slot dark matter fuel inventory and 3600 tick default fuel time.
- Preserve the legacy four-block effect radius and powered entity conversion rules.
- Keep automation item extraction disabled so dark matter fuel is not pulled out by external handlers.

## Legacy Behavior Ported

- Consumes one Dark Matter as fuel, then burns for `darkMatterWarperFuelTime` ticks.
- Converts nearby Skeletons to Wither Skeletons while preserving held and armor equipment.
- Converts nearby Spiders to Cave Spiders, Squids to Blazes, and Zombie Villagers to Evokers or Vindicators.
- Applies the legacy dark matter negative effects to nearby animals and configured non-creative players while fueled.
- Applies short negative effects to nearby non-creative players while unfueled when configured.

## Validation

- [x] `./gradlew.bat compileJava`
- [x] `./gradlew.bat runData`
- [x] `./gradlew.bat build`
- [x] `./gradlew.bat runGameTestServer`
- [x] `git diff --check`

## Notes

- The current thread's Serena MCP transport remains closed, but the configured wrapper command responds to an external JSONL MCP probe and can parse Java symbols.
