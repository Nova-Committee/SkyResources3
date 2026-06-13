# 阶段 17 实施清单：生命灌注器运行时逻辑

> 对应总计划：`plans/migration-plan.md` 的“方块实体与机器逻辑”和“网络、菜单和 GUI”迁移。

## 目标

- 为 `life_infuser` 接入 1.21.11 方块实体。
- 恢复旧版 `LifeInfuserTile` 的核心运行时行为：红石脉冲触发生命灌注、多方块校验、消耗健康宝石生命值和输入物品。
- 在 GUI/Menu 迁移前提供最小交互闭环：手动放入健康宝石和输入物品，空手取回内部物品。

## 已迁移行为

- 新增 `LifeInfuserBlock`，`life_infuser` 现在创建 `LifeInfuserBlockEntity`。
- 新增 `ModBlockEntityTypes.LIFE_INFUSER`，绑定 `life_infuser` 方块。
- 方块实体持久化：
  - 单个健康宝石 `ItemStack`
  - 单个输入物品 `ItemStack`
- 红石信号从未供电变为供电时尝试执行一次灌注。
- 多方块校验迁移为：
  - 四角同层和下一层必须为 `minecraft:logs` 标签方块。
  - 上方中心必须为 `dark_matter_block`。
  - 上方其余 8 格必须为 `minecraft:leaves` 标签方块。
- 灌注复用 `InfusionRecipes`，通过 `skyresources3:process` NeoForge recipe type 解析运行时配方。
- 配方匹配成功时：
  - 校验健康宝石已有生命值不少于配方消耗。
  - 移除核心下方目标方块且不掉落原方块。
  - 消耗输入物品数量。
  - 消耗健康宝石中存储的生命值。
  - 在目标方块位置掉落灌注产物。
- 破坏方块时掉落内部健康宝石和输入物品。
- 客户端交互采用乐观返回，服务端仍校验内部槽位，避免 GUI/Menu 同步迁移前交互被客户端旧状态阻断。
- `LifeInfuserMenu` / `LifeInfuserScreen` 已接入：普通空手右键打开界面，蹲下空手右键仍按输入物品优先、健康宝石其次的顺序快速取回。

## 后续阶段已补齐

- 物品 handler capability 自动化插入/抽出已在生命机器物品自动化能力任务中迁移。
- 生命灌注 recipe type 迁移已在 Stage 25 完成，运行时不再依赖内置硬编码配方列表。
- JEI 生命灌注分类联动已在配方查看器集成阶段恢复；REI/EMI 暂不重复实现。

## 验证

- [x] `./gradlew.bat compileJava`
- [x] `./gradlew.bat runData`
- [x] `./gradlew.bat build`
- [x] `./gradlew.bat runGameTestServer`
- [x] `git diff --check`
