# 阶段 1 实施清单：基础资源可视化

> 对应总计划：`plans/migration-plan.md` 的「1. 基础注册与静态内容」。
> 状态：已完成，`gradlew.bat build` 与资源引用校验通过。

## 目标

- 为阶段 0 已注册的基础方块和物品补齐可见资源。
- 将旧项目第一批安全贴图迁移到 `skyresources3` 命名空间。
- 使用 Minecraft 1.21.11 的 `assets/<namespace>/items/*.json` 物品模型定义格式。

## 已迁移资源

### 方块

- `compressed_coal_block`
- `coal_infused_block`
- `sandy_netherrack`
- `petrified_wood`
- `petrified_planks`
- `magmafied_stone`
- `heavy_snow`
- `dark_matter_block`
- `light_matter_block`
- `alchemical_glass`

每个方块均补齐：

- `blockstates/<id>.json`
- `models/block/<id>.json`
- `items/<id>.json`
- `textures/block/<id>.png`

`magmafied_stone` 同步迁移了 `textures/block/magmafied_stone.png.mcmeta`。

### 物品

- `cactus_fruit`
- `fleshy_snow_nugget`
- `cactus_needle`
- `crystal_shard`
- `plant_matter`
- `dark_matter`
- `light_matter`
- `sawdust`
- `crushed_stone`
- `crushed_netherrack`

每个普通物品均补齐：

- `items/<id>.json`
- `models/item/<id>.json`
- `textures/item/<id>.png`

## 格式决策

- 方块模型使用最小 `minecraft:block/cube_all`，对应旧项目第一批方块的 `cube_all` 资源。
- 方块物品的 `items/<id>.json` 直接引用 `skyresources3:block/<id>`，对齐 1.21.11 vanilla 方块物品格式。
- 普通物品的 `items/<id>.json` 引用 `skyresources3:item/<id>`，再由 `models/item/<id>.json` 指向贴图。
- 阶段 1 暂不引入 datagen；阶段 2 再统一建立模型、语言、配方、战利品表 provider。

## 验证

- `node -e "<json parse check>"`：所有 `assets/skyresources3/**/*.json` 可解析。
- `node -e "<asset reference check>"`：blockstate、item definition、model texture 与 `.png.mcmeta` 引用均存在。
- `gradlew.bat build`：通过。

## 后续 TODO

- 用 datagen 接管可生成的 blockstate/model/item definition，减少手写 JSON 长期维护成本。
- 许可策略仍需确认：旧项目贴图来自 ARR 项目，目标项目当前标注 MIT。
- 后续迁移复杂资源前需单独处理多状态模型、透明渲染、GUI 贴图和联动资源。
