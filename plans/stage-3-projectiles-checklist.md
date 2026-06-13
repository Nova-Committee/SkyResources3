# 阶段 3 实施清单：重雪球实体与工具耐久

> 对应总计划：`plans/migration-plan.md` 的“基础注册与静态内容”和“特殊物品/实体行为”前置迁移。
> 状态：已完成重雪球投掷实体、客户端渲染注册、伤害配置项，以及基础工具耐久值迁移；后续 Stage 24/25 已将自定义处理配方接入运行时。

## 目标

- 将旧版 `ItemHeavySnowball` 与 `ItemHeavyExplosiveSnowball` 从静态物品壳升级为可投掷物品。
- 按旧版配置默认值迁移重雪球伤害：普通重雪球 8，爆炸重雪球 12。
- 保留旧版 Blaze 额外伤害倍率和爆炸重雪球的极小冲击爆炸。
- 将 cutting knife、rock grinder、infusion stone 从单堆叠壳补齐为耐久物品。

## 已迁移行为

- `heavy_snowball`
  - 最大堆叠数改为 8。
  - 右键投掷实体 `skyresources3:heavy_snowball`。
  - 命中实体造成配置伤害，Blaze 目标按旧版倍率提升。
  - 命中后广播物品粒子并移除。
- `heavy_explosive_snowball`
  - 最大堆叠数改为 8。
  - 右键投掷实体 `skyresources3:heavy_explosive_snowball`。
  - 命中实体造成配置伤害，Blaze 目标按旧版倍率提升。
  - 命中后在目标位置创建半径 `0.01F`、不破坏方块的冲击爆炸。

## 已迁移注册

- `ModEntityTypes`
  - `heavy_snowball`
  - `heavy_explosive_snowball`
- `SkyResources3Client`
  - 使用 `ThrownItemRenderer` 渲染两个投掷实体。
- `Config`
  - `heavySnowballDamage = 8`
  - `explosiveHeavySnowballDamage = 12`

## 已迁移耐久

- `cactus_cutting_knife`：2
- `stone_cutting_knife`：91
- `iron_cutting_knife`：175
- `diamond_cutting_knife`：1092
- `stone_grinder`：104
- `iron_grinder`：200
- `diamond_grinder`：1248
- `sandstone_infusion_stone`：100，不允许铁砧合并修复
- `red_sandstone_infusion_stone`：80，不允许铁砧合并修复
- `alchemical_infusion_stone`：1500，不允许铁砧合并修复

## 后续阶段已补齐

- cutting knife 的切割处理逻辑已迁移为 `skyresources3:process` recipe runtime，并由 `cutting_knife_process` GameTest 覆盖。
- rock grinder 的研磨处理逻辑已迁移为 `skyresources3:process` recipe runtime，并由 `rock_grinder_process` GameTest 覆盖。
- infusion stone 的合成/转化行为已迁移为 `InfusionRecipes` + `skyresources3:process` infusion runtime，并由 `infusion_stone_process` GameTest 覆盖。

## 仍可补充验证

- 爆炸重雪球的完整交互效果后续可用 GameTest 或最小运行端加载测试补充验证。

## 验证

- `gradlew.bat build`：通过。
