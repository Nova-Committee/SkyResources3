# 阶段 5 实施清单：健康宝石基础行为

> 对应总计划：`plans/migration-plan.md` 的“实体、渲染与特殊物品”和“方块实体与机器逻辑”前置迁移。
> 状态：已迁移健康宝石注册、资源、基础数据存储、潜行右键注入生命行为和最大生命值加成；生命灌注机器运行时联动留待后续阶段。

## 目标

- 将旧版 `ItemHealthGem` 迁移为 1.21.11 独立物品 `health_gem`。
- 使用数据组件保存旧版 NBT 中的 `health` 数值。
- 保留旧版潜行右键将玩家 2 点生命注入宝石的核心行为。
- 保留旧版配置默认值：最大存储 100，生命加成比例 0.02。

## 已迁移行为

- `health_gem` 最大堆叠数为 1。
- 潜行右键时：
  - 若未进入冷却且未超过最大存储，玩家受到 2 点通用伤害。
  - 宝石的 `CUSTOM_DATA.health` 增加 2。
  - 玩家获得 20 tick 物品冷却。
- tooltip 保留旧版 Shift 展开信息：
  - 未按 Shift 时提示 `Hold Shift for info.`
  - 按 Shift 时显示注入操作、已注入生命与可获得生命加成。
- 玩家主背包中的健康宝石每 20 tick 汇总一次生命加成，并通过 `MAX_HEALTH` transient modifier 应用到玩家。
- 当健康宝石移除或加成降低时，移除/更新本模组的生命加成并夹取玩家当前生命值。
- `HealthGemItem.getHealthInjected` 提供后续生命灌注机器读取入口。
- `HealthGemItem.getHealthBoost` 保留旧版按配置比例计算最大生命加成的入口。

## 已迁移资源

- `assets/skyresources3/items/health_gem.json`
- `assets/skyresources3/models/item/health_gem.json`
- `assets/skyresources3/textures/item/health_gem.png`
- `assets/skyresources3/lang/en_us.json`

## 暂缓迁移

- `LifeInfuser` 和 `LifeInjector` 已完成静态方块、资源、掉落和合成表迁移；健康宝石与机器的自动注入/消耗逻辑留待机器运行时阶段。
- 健康宝石旧配方来自 `infusionRecipes`，依赖生命灌注 recipe type，未在本批迁移。
- `survivalist_fishing_rod` 依赖自定义鱼钩实体，留待实体批次单独迁移。

## 验证

- `gradlew.bat build`：通过。
- JSON 解析与 item model 贴图引用检查：通过。
