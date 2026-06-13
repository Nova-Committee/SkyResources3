# Stage 39 - Combustion Automation

## Scope

- Migrate the 1.12.2 Combustion Collector.
- Migrate the 1.12.2 Smart Combustion Controller.
- Preserve the old five-slot inventory/filter layout and priority behavior.
- Wire block/entity/menu/screen registration, resources, recipes, loot, tags, and config.

## Acceptance Checklist

- [x] Collector stores combustion outputs in a five-slot inventory.
- [x] Controller stores one item per filter slot and crafts by left-to-right output priority.
- [x] Controller respects redstone disable and configurable cooldown ticks.
- [x] Advanced combustion structures accept collector/controller as metal-tier side/top blocks.
- [x] Wood and stone combustion structures do not accept collector/controller blocks.
- [x] Manual combustion routes outputs into an adjacent collector when one is present.
- [x] Recipes, block loot, block tags, language keys, item definitions, models, blockstates, and textures are present.
- [x] `compileJava`, `runData`, `build`, `runGameTestServer`, and `git diff --check` pass.

## Deferred

- Add focused GameTests for exact controller priority and collector overflow behavior when the project has reusable machine test helpers.
