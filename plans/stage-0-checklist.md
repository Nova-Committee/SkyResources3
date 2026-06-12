# 阶段 0 实施清单：基础骨架与清理

> 对应总计划：`plans/migration-plan.md` 的「0. 基础骨架与清理」。
> 状态：已完成，`gradlew.bat build` 通过。

## 目标

- 将目标项目从 NeoForge 示例模板整理为 SkyResources3 迁移骨架。
- 建立后续迁移所需的注册入口和命名规范。
- 保持本阶段简单可编译，不迁移机器、GUI、网络、世界生成等复杂行为。

## 修改文件清单

### 需要重写/更新

- `src/main/java/committee/nova/mods/skyresources3/Skyresources3.java`
  - 移除示例 `EXAMPLE_BLOCK`、`EXAMPLE_ITEM`、`EXAMPLE_TAB`。
  - 移除模板日志：`HELLO FROM COMMON SETUP`、`DIRT BLOCK`、`MINECRAFT NAME`。
  - 注册新建的 `ModBlocks`、`ModItems`、`ModCreativeTabs`。
  - 保留 `Config.SPEC` 注册。

- `src/main/java/committee/nova/mods/skyresources3/Config.java`
  - 移除模板配置：`logDirtBlock`、`magicNumber`、`items`。
  - 建立迁移期最小配置：
    - `enableMigrationDebugLogging`
    - `enableVoidIslandFeatures`
    - `enableMagmaIsland`
  - 后续 VoidIslandControl 内置化时再扩展岛屿/团队配置。

- `src/main/resources/assets/skyresources3/lang/en_us.json`
  - 移除示例翻译。
  - 添加创造标签、第一批基础方块/物品翻译。

- `gradle.properties`
  - 补充 `mod_description`，不改版本/加载器/命名空间。

- `src/main/templates/META-INF/neoforge.mods.toml`
  - 保持模板展开结构。
  - 如需更新主页 URL，单独确认后再改；阶段 0 不强行改远端地址。

### 新增

- `src/main/java/committee/nova/mods/skyresources3/registry/ModBlocks.java`
- `src/main/java/committee/nova/mods/skyresources3/registry/ModItems.java`
- `src/main/java/committee/nova/mods/skyresources3/registry/ModCreativeTabs.java`

## 第一批注册内容

阶段 0 只注册无复杂行为、无方块实体、无自定义渲染的基础内容，用于证明 NeoForge 注册链路。

### 方块

| 新 registry id | 旧名称/来源 | 说明 |
|---|---|---|
| `compressed_coal_block` | `compressedCoalBlock` | 旧 Hardened Coal Block |
| `coal_infused_block` | `coalInfusedBlock` | 旧 Alchemical Coal Block |
| `sandy_netherrack` | `sandyNetherrack` | 旧 Sandy Netherrack |
| `petrified_wood` | `petrifiedWood` | 旧 Petrified Wood |
| `petrified_planks` | `petrifiedPlanks` | 旧 Petrified Wood Planks |
| `magmafied_stone` | `magmafiedStone` | 后续 magma island 需要 |
| `heavy_snow` | `heavySnow` | 后续重雪球/雪类逻辑需要 |
| `dark_matter_block` | `darkMatterBlock` | 基础材料块 |
| `light_matter_block` | `lightMatterBlock` | 基础材料块 |
| `alchemical_glass` | `alchemicalGlass` | 阶段 0 先用普通 glass-like block，不实现透明渲染细节 |

### 物品

| 新 registry id | 旧名称/来源 | 说明 |
|---|---|---|
| `cactus_fruit` | `cactusFruit` | 旧食物，先迁移基础食物属性 |
| `fleshy_snow_nugget` | `fleshySnowNugget` | 旧食物，先迁移基础食物属性 |
| `cactus_needle` | `alchemyItemComponent.cactusNeedle` | 基础组件 |
| `crystal_shard` | `alchemyItemComponent.crystalShard` | 基础组件 |
| `plant_matter` | `baseItemComponent.plantMatter` | 基础组件 |
| `dark_matter` | `baseItemComponent.darkMatter` | 基础组件 |
| `light_matter` | `baseItemComponent.lightMatter` | 基础组件 |
| `sawdust` | `baseItemComponent.sawdust` | 基础组件 |
| `crushed_stone` | `techItemComponent.stoneCrushed` | 基础组件 |
| `crushed_netherrack` | `techItemComponent.netherrackCrushed` | 基础组件 |

### 暂不注册

- 所有机器方块和方块实体。
- 所有 GUI/Menu。
- 所有自定义实体。
- 所有联动物品/方块。
- 流体与 bucket。
- Casing 多 meta 内容。
- Water Extractor 多状态模型。
- 旧 `ItemKnife`、`ItemRockGrinder` 等工具行为。

## 资源迁移规则

- 旧命名空间 `skyresources` -> 新命名空间 `skyresources3`。
- 旧 `textures/blocks` -> 新 `textures/block`。
- 旧 `textures/items` -> 新 `textures/item`。
- 旧 `tile.skyresources.<name>.name` -> 新 `block.skyresources3.<new_registry_id>`。
- 旧 `item.skyresources.<name>.name` -> 新 `item.skyresources3.<new_registry_id>`。
- 旧 camelCase/大小写混合文件名必须改为小写 snake case。

示例：

- `compressedCoalBlock.png` -> `compressed_coal_block.png`
- `sandyNetherrack.png` -> `sandy_netherrack.png`
- `petrifiedwood.png` -> `petrified_wood.png`
- `cactusFruit.png` -> `cactus_fruit.png`
- `stoneCrushed.png` -> `crushed_stone.png`

## API 决策

- 使用 NeoForge `DeferredRegister.Blocks` 和 `DeferredRegister.Items`。
- 使用 `registerSimpleBlock` / `registerSimpleBlockItem` / `registerSimpleItem`，已在本地 `neoforge-21.11.42` 依赖中通过 `javap` 确认存在。
- 创造模式标签使用 `DeferredRegister<CreativeModeTab>` 和 `CreativeModeTab.builder()`。
- 阶段 0 不引入新的外部依赖。

## 验收标准

- `src/main/java/.../Skyresources3.java` 不再包含模板示例注册。
- 新注册类均位于 `committee.nova.mods.skyresources3.registry`。
- `en_us.json` 不再包含 `Example Mod Tab` / `Example Block` / `Example Item`。
- `gradlew.bat build` 通过。
- 若只有 deprecation warning 且不来自阶段 0 新代码，可记录但不阻塞。

## 提交计划

完成并验证后，建议中文提交：

```text
初始化迁移骨架
```

提交前必须再次展示实际 staged 文件列表，并等待用户确认。
