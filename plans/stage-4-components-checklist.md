# 阶段 4 实施清单：组件物品拆分与基础配方

> 对应总计划：`plans/migration-plan.md` 的“基础注册与静态内容”“配方与数据生成”“特殊物品逻辑”交叉部分。
> 状态：已将旧版三个 meta component 的剩余基础条目拆分为 1.21.11 独立物品，并迁移可用 vanilla crafting/smelting 表达的配方。

## 目标

- 将旧版 `alchemyitemcomponent`、`baseitemcomponent`、`TechItemComponent` 的剩余基础条目从 meta item 拆成独立注册项。
- 复用旧版贴图并改为 `skyresources3` 命名空间下的小写 snake case。
- 保持当前注册结构简单，不重新引入旧版 meta item 容器。
- 优先迁移不依赖机器、矿辞或旧 `ProcessRecipeManager` 的配方。

## 已注册物品

- 炼金组件：
  - `primus_alchemical_dust`
  - `secundus_alchemical_dust`
  - `tertius_alchemical_dust`
  - `quartus_alchemical_dust`
  - `alchemical_coal`
  - `alchemical_gold_ingot`
  - `alchemical_iron_ingot`
  - `alchemical_gold_needle`
  - `alchemical_diamond`
- 基础组件：
  - `advanced_power_component`
  - `frozen_iron_cooling_component`
  - `enriched_bonemeal`
  - `quartz_amplification_component`
- 技术组件：
  - `radioactive_mix`
  - `frozen_iron_ingot`

## 已迁移行为

- `plant_matter` 改为 `InstantBonemealItem`，受配置 `plantMatterBonemealCapability` 控制，默认启用。
- `enriched_bonemeal` 使用同一瞬间骨粉实现，始终启用。
- 瞬间骨粉行为最多尝试 100 次生长，与旧版 `BaseItemComponent.applyBonemeal` 的上限保持一致。

## 已迁移资源

每个新增物品均补齐：

- `assets/skyresources3/items/<id>.json`
- `assets/skyresources3/models/item/<id>.json`
- `assets/skyresources3/textures/item/<id>.png`
- `assets/skyresources3/lang/en_us.json`

## 已迁移配方

- `coal_infused_block` 与 `alchemical_coal` 九合一/拆分。
- `alchemical_gold_needle` 由两个 `alchemical_gold_ingot` 合成。
- `alchemical_infusion_stone` 由 `alchemical_gold_needle` 和 `alchemical_diamond` 合成。
- `enriched_bonemeal` 由腐肉和 3 个骨粉合成 4 个。
- `frozen_iron_cooling_component` 由 `frozen_iron_ingot`、萤石粉和青金石合成。
- `quartz_amplification_component` 由下界石英、萤石粉和青金石合成。
- `plant_matter` 熔炼为木炭。

## 暂缓迁移

- 四级炼金粉尘的生成仍依赖旧 `combustionRecipes`/`fusionRecipes`。
- `alchemical_coal`、炼金铁锭、炼金金锭、炼金钻石的生成仍依赖旧 `fusionRecipes`。
- `radioactive_mix` 的生成仍依赖旧 `combustionRecipes`。
- `frozen_iron_ingot` 的生成仍依赖旧 `freezerRecipes`。
- `advanced_power_component` 的旧配方依赖矿辞候选材料和外部模组材料，后续在标签/联动策略明确后迁移。
- 旧 `oreAlchDust` 和 `dirtyGem` 是动态列表物品，不在本批拆分。

## 验证

- `gradlew.bat build`：通过。
- `gradlew.bat runData`：通过，生成新增配方与进度。
- `gradlew.bat build`：生成资源参与主资源集后再次通过。
- JSON 解析与 item model 贴图引用检查：通过。
