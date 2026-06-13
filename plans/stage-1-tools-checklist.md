# 阶段 1 补充清单：基础工具与特殊物品壳子

> 对应总计划：`plans/migration-plan.md` 的“基础注册与静态内容”和“配方与数据生成”前置补齐。
> 状态：本阶段已完成静态注册、可视资源和普通 crafting 配方；后续 Stage 3/6/23/24/25 已补齐实体、工具运行时、处理配方基础和灌注运行时。

## 目标

- 迁移旧版早期流程依赖的基础工具、重雪球和灌注石注册项。
- 补齐 1.21.11 物品定义、模型、贴图和英文语言键，避免创造物品栏出现缺失资源。
- 只迁移旧版可直接映射到 vanilla crafting 的配方，不提前发明自定义机器 recipe type。

## 已注册物品

- `heavy_snowball`
- `heavy_explosive_snowball`
- `cactus_cutting_knife`
- `stone_cutting_knife`
- `iron_cutting_knife`
- `diamond_cutting_knife`
- `stone_grinder`
- `iron_grinder`
- `diamond_grinder`
- `sandstone_infusion_stone`
- `red_sandstone_infusion_stone`
- `alchemical_infusion_stone`

## 已迁移资源

每个物品均补齐：

- `assets/skyresources3/items/<id>.json`
- `assets/skyresources3/models/item/<id>.json`
- `assets/skyresources3/textures/item/<id>.png`

刀和研磨器使用 `minecraft:item/handheld` 模型 parent；雪球和灌注石使用 `minecraft:item/generated`。

## 已迁移配方

- `heavy_snow`：4 个 `heavy_snowball` 合成。
- `heavy_explosive_snowball`：3 个 `heavy_snowball` 加 1 个火药合成 3 个。
- 四种 cutting knife 的旧版 crafting 配方。
- 三种 rock grinder 的旧版 crafting 配方。
- `sandstone_infusion_stone` 与 `red_sandstone_infusion_stone` 的旧版 crafting 配方。

## 后续阶段已补齐

- `heavy_snowball` 的 freezer 来源已通过 `skyresources3:process` freezer 配方生成：`process/freezer/heavy_snowball.json`。
- `alchemical_infusion_stone` 的旧版配方已通过独立物品和 datagen 配方恢复：`alchemical_infusion_stone.json`。
- cutting knife、rock grinder 和 infusion stone 的耐久、工具属性、运行时处理逻辑已在 Stage 3/24/25 补齐。
- `survivalist_fishing_rod` 的 fishing rod 行为、cast 模型、配方和自定义钓鱼掉落已在 Stage 6 及后续回归任务补齐。

## 验证

- `gradlew.bat build`
- `gradlew.bat runData`
- 生成资源参与主资源集后再次执行 `gradlew.bat build`
