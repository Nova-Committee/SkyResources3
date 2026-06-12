# 资源迁移映射笔记

> 来源：`D:/workspace/minecraft/mods/4Github/SkyResources/src/main/resources/assets/skyresources`

## 目录映射

| 旧目录 | 新目录 |
|---|---|
| `assets/skyresources/blockstates` | `assets/skyresources3/blockstates` |
| `assets/skyresources/models/block` | `assets/skyresources3/models/block` |
| `assets/skyresources/models/item` | `assets/skyresources3/models/item` |
| `assets/skyresources/textures/blocks` | `assets/skyresources3/textures/block` |
| `assets/skyresources/textures/items` | `assets/skyresources3/textures/item` |
| `assets/skyresources/textures/gui` | `assets/skyresources3/textures/gui` |
| `assets/skyresources/lang/*.lang` | `assets/skyresources3/lang/*.json` |

## 第一批安全迁移资源

这些资源对应阶段 0 的无方块实体基础块/物品，可在阶段 0 或阶段 1 优先迁移。

### 方块纹理

| 旧文件 | 建议新文件 |
|---|---|
| `compressedCoalBlock.png` | `compressed_coal_block.png` |
| `coalInfusedBlock.png` | `coal_infused_block.png` |
| `sandyNetherrack.png` | `sandy_netherrack.png` |
| `petrifiedwood.png` | `petrified_wood.png` |
| `petrifiedplanks.png` | `petrified_planks.png` |
| `magmafiedstone.png` | `magmafied_stone.png` |
| `magmafiedstone.png.mcmeta` | `magmafied_stone.png.mcmeta` |
| `heavySnow.png` | `heavy_snow.png` |
| `darkmatter.png` | `dark_matter_block.png` |
| `lightmatter.png` | `light_matter_block.png` |
| `alchemicalglass.png` | `alchemical_glass.png` |

### 物品纹理

| 旧文件 | 建议新文件 |
|---|---|
| `cactusFruit.png` | `cactus_fruit.png` |
| `fleshySnowNugget.png` | `fleshy_snow_nugget.png` |
| `cactusNeedle.png` | `cactus_needle.png` |
| `crystalshard.png` | `crystal_shard.png` |
| `plantMatter.png` | `plant_matter.png` |
| `darkMatter.png` | `dark_matter.png` |
| `lightmatter.png` | `light_matter.png` |
| `sawdust.png` | `sawdust.png` |
| `stoneCrushed.png` | `crushed_stone.png` |
| `netherrackcrushed.png` | `crushed_netherrack.png` |

## 需要后续专项处理

- `WaterExtractor.*`：多状态模型/纹理，需配合物品组件或自定义模型逻辑。
- `casing.json` / casing 系列翻译：旧 meta block，1.21.11 应拆注册项或使用数据组件/方块状态重新设计。
- `crystalfluid_*` / `dirtycrystalfluid_*` / `moltencrystalfluid_*`：流体资源，等待流体注册阶段处理。
- GUI 纹理：等待 Menu/Screen 迁移阶段处理。
- JEI 纹理：等待 JEI/EMI/REI 联动调研后处理。
- 旧 blockstates/models 存在大量大小写混合文件名，迁移时必须统一为小写 snake case 并更新 JSON 引用。

## 许可提醒

旧项目 `LICENSE` 为 ARR，目标项目当前 `gradle.properties` 标注 MIT。迁移旧贴图/模型/文本前需要明确许可策略：

- 如果目标继续使用旧资源，建议将项目许可证或资源许可证与旧项目 ARR 兼容化。
- 如果保持 MIT，应重新制作或确认这些资源允许迁移。
- 代码迁移也应以“重实现行为”为主，避免未经确认地复制大量旧实现。
