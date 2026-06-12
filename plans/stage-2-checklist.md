# 阶段 2 实施清单：配方、标签与战利品表

> 对应总计划：`plans/migration-plan.md` 的「2. 配方与数据生成」。
> 状态：已完成第一批 server-side datagen，`runData` 与 `build` 通过。

## 目标

- 建立 NeoForge 1.21.11 的数据生成入口。
- 为已注册的基础内容生成可验证的合成配方、方块掉落和基础标签。
- 只迁移不依赖旧机器、旧 meta 物品或未注册内容的配方。

## 新增 datagen 入口

- `SkyResources3Data`
  - 监听 `GatherDataEvent.Client`。
  - 注册配方、方块标签、物品标签、战利品表 provider。
- `SkyResources3RecipeProvider`
  - 使用 `RecipeProvider.Runner` 适配 1.21.11 的配方生成签名。
- `SkyResources3BlockLootProvider`
  - 为当前所有基础方块生成 `dropSelf` 战利品表。
- `SkyResources3BlockTagsProvider`
  - 生成挖掘工具标签。
- `SkyResources3ItemTagsProvider`
  - 将 `petrified_planks` 加入 `minecraft:planks` 物品标签。

## 已生成配方

- `compressed_coal_block`
- `sandy_netherrack`
- `petrified_planks_from_petrified_wood`
- `plant_matter_from_cactus_fruit`
- `fleshy_snow_nugget`
- `dark_matter_block`
- `dark_matter_from_block`
- `light_matter_block`
- `light_matter_from_block`
- `charcoal_from_petrified_wood`

## 已生成标签

- `minecraft:mineable/pickaxe`
- `minecraft:mineable/axe`
- `minecraft:mineable/shovel`
- `minecraft:needs_stone_tool`
- `minecraft:planks`

## 暂缓迁移

- `coal_infused_block`：旧配方依赖 `alchemyComponent` meta 6，等炼金组件拆分注册后迁移。
- `heavy_snow`：旧配方依赖 `heavySnowball`，等实体/特殊物品阶段迁移。
- `cactus_fruit` 旧刀具处理配方：依赖 `ItemKnife`/knife recipe 类型，等特殊物品和自定义处理配方阶段迁移。
- 旧 `ProcessRecipeManager` 系列：需要先设计 1.21.11 recipe type，不在本阶段混入。

## 验证

- `gradlew.bat build`：通过。
- `gradlew.bat runData`：通过，生成 `src/generated/resources`。
- `gradlew.bat build`：在生成资源参与主资源集后再次通过。

## 后续 TODO

- 将模型、方块状态、物品定义和语言文件逐步迁移到 datagen，替换阶段 1 的手写 JSON。
- 设计自定义机器处理 recipe type，再迁移 `ProcessRecipeManager`。
- 为关键配方补 GameTest 或更高层的加载验证。
