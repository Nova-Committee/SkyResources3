# Stage 14 - Grass Seed Drops Checklist

- [x] Read old `ConfigOptions.MiscSettings` grass drop switches.
- [x] Read old `ModCrafting.init` `MinecraftForge.addGrassSeed` registrations.
- [x] Confirm NeoForge 1.21.11 has no direct `addGrassSeed` replacement and inspect `BlockDropsEvent`.
- [x] Add common config switches for beetroot, melon, pumpkin, cocoa bean, carrot, and potato grass drops.
- [x] Add grass block drop handling for `short_grass`, `tall_grass`, `fern`, and `large_fern`.
- [x] Preserve the old weighted pool behavior with vanilla wheat seeds included at weight 10.
- [x] Run build, GameTest server, and diff checks.
- [x] Commit this migration slice with a Chinese message.
