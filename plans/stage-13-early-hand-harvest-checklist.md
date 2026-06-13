# Stage 13 - Early Hand Harvest Checklist

- [x] Read old `EventHandler#onPlayerRightClick` empty-hand sneaking behavior.
- [x] Add event handling for sneaking empty-hand cactus needle harvesting.
- [x] Add event handling for sneaking empty-hand snow and snow layer harvesting.
- [x] Register the event handler on the NeoForge event bus.
- [x] Run build, GameTest server, and diff checks.
- [x] Commit this migration slice with a Chinese message.

Deferred:
- [x] Manual water-cauldron cleaning now consumes `ProcessRecipes.CAULDRON_CLEAN` through the Rock Cleaner slice.
- [x] Stable vanilla Dirty Gem `cauldronclean` recipes were generated in Stage 50.
- [ ] Dynamic/tagged cauldron-clean outputs remain deferred until the modded output/tag compatibility policy is
  explicit.
