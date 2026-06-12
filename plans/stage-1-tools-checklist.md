# 阶段 1 补充清单：基础工具与特殊物品壳子

> 对应总计划：`plans/migration-plan.md` 的“基础注册与静态内容”和“配方与数据生成”前置补齐。
> 状态：已完成静态注册、可视资源和普通 crafting 配方；行为类、实体和机器处理配方留待后续阶段。

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

## 暂缓迁移

- `heavy_snowball` 的来源仍依赖旧版 freezer 处理配方，等待自定义机器 recipe type 设计。
- `alchemical_infusion_stone` 的旧版配方依赖尚未拆分的 `alchemyComponent` meta 9/10。
- cutting knife、rock grinder、infusion stone 目前只是静态物品壳子；耐久、工具属性、右键/处理逻辑和实体行为后续单独迁移。
- `survivalist_fishing_rod` 需要 fishing rod 行为和 cast 模型覆盖，未混入本轮静态工具批次。

## 验证

- `gradlew.bat build`
- `gradlew.bat runData`
- 生成资源参与主资源集后再次执行 `gradlew.bat build`
