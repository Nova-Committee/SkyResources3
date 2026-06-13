# 阶段 16 实施清单：生命汲取器运行时逻辑

> 对应总计划：`plans/migration-plan.md` 的“方块实体与机器逻辑”和“网络、菜单和 GUI”迁移。

## 目标

- 为 `life_injector` 接入 1.21.11 方块实体。
- 恢复旧版 `LifeInjectorTile` 的核心运行时行为：周期性抽取上方实体生命并存入健康宝石。
- 在 GUI/Menu 迁移前提供最小交互闭环：手持健康宝石放入机器，空手取回健康宝石。

## 已迁移行为

- 新增 `ModBlockEntityTypes.LIFE_INJECTOR`，绑定 `life_injector` 方块。
- `life_injector` 现在创建 `LifeInjectorBlockEntity` 并在服务端 tick。
- 方块实体持久化：
  - 单个健康宝石 `ItemStack`
  - 旧版冷却字段 `cooldown`
- 机器每 60 tick 扫描正上方 `1x1x1` 区域内的存活 `LivingEntity`。
- 每个实体每次最多抽取 2 点生命值。
- 健康宝石容量不足时不伤害实体。
- 伤害成功后通过 `HealthGemItem.addStoredHealth` 写回健康宝石，避免机器直接拼写 NBT。
- 破坏方块时掉落内部健康宝石。
- 客户端交互采用乐观返回，服务端仍校验内部宝石槽，避免 GUI/Menu 同步迁移前空手取回被客户端旧状态阻断。
- `LifeInjectorMenu` / `LifeInjectorScreen` 已接入：普通空手右键打开界面，蹲下空手右键仍可快速取回健康宝石。

## 暂缓迁移

- 物品 handler capability 自动化插入/抽出已在生命机器物品自动化能力任务中迁移。
- `LifeInfuser` 方块实体、多方块校验和生命灌注执行逻辑留待后续阶段。

## 验证

- [x] `./gradlew.bat compileJava`
- [x] `./gradlew.bat runData`
- [x] `./gradlew.bat build`
- [x] `./gradlew.bat runGameTestServer`
- [x] `git diff --check`
