# SkyResources3 迁移计划

> 来源需求：`plans/ask.md`
> 来源项目：`D:/workspace/minecraft/mods/4Github/SkyResources`
> 目标项目：`D:/workspace/minecraft/mods/3Nova/SkyResources3`

## 目标范围

- 将 SkyResources 从 Minecraft 1.12.2 + Forge 14.23.5.2860 迁移到 Minecraft 1.21.11 + NeoForge 21.11.42。
- 覆盖方块、物品、配方、网络、界面/GUI、多方块结构、命令、世界生成。
- 将 VoidIslandControl 的核心功能纳入本模组，而不是继续依赖 1.12.2 jar。
- 完善团队/岛屿协作功能。
- 联动模组尽力迁移；若 1.21.11 没有合适版本或 API，记录 TODO 和阻塞原因。

## 已盘点事实

- 旧项目源码：219 个 Java 文件，1197 个总文件。
- 旧项目许可文件为 ARR：`D:/workspace/minecraft/mods/4Github/SkyResources/LICENSE`。
- 旧项目主体包：`com.bartz24.skyresources`。
- 旧项目主要模块：
  - `base`：基础方块、物品、实体、GUI、指南书。
  - `alchemy`：炼金方块、流体、效果、GUI、方块实体。
  - `technology`：机器、工具、GUI、方块实体、多方块结构。
  - `recipe`：自定义处理配方。
  - `network`：旧 `SimpleNetworkWrapper`，目前至少有 `DumpMessage`。
  - `registry`：方块、物品、流体、实体/方块实体、GUI、配方、渲染、指南页注册。
  - `plugin`：JEI、CraftTweaker、Forestry、Binnie's、The One Probe、VoidIslandControl 等联动。
- 旧项目资源：
  - `assets/skyresources/blockstates`：44 个。
  - `assets/skyresources/models`：66 个。
  - `assets/skyresources/textures`：146 个。
  - `assets/skyresources/lang`：`en_US.lang`、`fr_FR.lang`、`zh_CN.lang`。
- 旧项目显式注册内容：
  - 方块约 36 个。
  - 显式物品约 28 个。
  - 宝石配置 40+ 个。
  - 方块实体/实体约 21 个。
  - GUI/Menu 约 17 个。
  - VoidIslandControl jar 存在于 `libs/voidislandcontrol-1.5.3.jar`，本地未找到对应源码。

## 基本迁移原则

- 先建立 NeoForge 1.21.11 可编译骨架，再分批恢复玩法。
- 只迁移当前阶段需要的实现，避免一次性复制 1.12.2 架构导致大面积不可编译。
- 旧 Forge API 必须替换为 NeoForge 1.21.11 API：
  - `GameData.register_impl` -> `DeferredRegister`。
  - `IInventory`/旧 `Container` -> 1.21.11 菜单/容器体系。
  - `IGuiHandler`/`openGui` -> `MenuType` + `SimpleMenuProvider`/网络额外数据。
  - `SimpleNetworkWrapper` -> NeoForge custom payload。
  - `TileEntity` -> `BlockEntity`。
  - `IWorldGenerator`/旧 `WorldType` -> 1.21.11 世界预设、维度、chunk generator 或 server lifecycle 实现。
- 资源迁移时统一命名到 1.21.11 约定：
  - 资源命名空间使用 `skyresources3`。
  - 旧 `lang/*.lang` 转为 `lang/*.json`，至少维护 `en_us.json`，后续补 `zh_cn.json`。
  - 旧 `textures/blocks`/`textures/items` 迁移为 `textures/block`/`textures/item`。
  - 能 datagen 的模型、方块状态、配方、战利品表优先 datagen。
- 每完成一个可验证阶段，准备中文 commit；执行 `git commit` 前必须先给出提交计划并等待确认。

## 阶段计划与提交点

### 0. 基础骨架与清理

- 移除 NeoForge 模板示例方块/物品/日志。
- 建立 `registry`、`data`、`config`、`network`、`island` 等基础包。
- 更新模组元数据、描述和创造模式标签。
- 验证：`gradlew.bat build`。
- 建议提交：`初始化迁移骨架`

### 1. 基础注册与静态内容

- 迁移基础材料方块、装饰/资源方块、基础食品、工具、组件物品。
- 建立方块/物品定义表，减少重复注册代码。
- 初步迁移语言键和可直接复用的纹理。
- 验证：`gradlew.bat build`，必要时 `gradlew.bat runData`。
- 建议提交：`迁移基础方块和物品`

### 2. 配方与数据生成

- 迁移 `ProcessRecipe`/`ProcessRecipeManager` 为数据驱动或 NeoForge recipe 类型。
- 迁移普通合成、机器处理配方、战利品表、标签。
- 建立 datagen provider。
- 验证：`gradlew.bat runData` + `gradlew.bat build`。
- 建议提交：`迁移配方和数据生成`

### 3. 方块实体与机器逻辑

- 迁移基础机器：
  - Crucible / Fluid Dropper / Freezer / Dirt Furnace。
  - Rock Crusher / Rock Cleaner。
  - Combustion Collector / Controller。
  - Aqueous Concentrator / Deconcentrator。
  - Life Infuser / Life Injector。
  - End Portal Core / Dark Matter Warper / Quick Dropper。
- 将旧能量、流体、库存逻辑迁移到 1.21.11 能力/附件/容器模式。
- 验证：`gradlew.bat build`，核心机器补 GameTest。
- 建议提交：`迁移机器和方块实体`

### 4. 网络、菜单和 GUI

- 迁移旧 `DumpMessage` 及所有 GUI 交互网络包。
- 将 17 个旧 GUI 从 `IGuiHandler` 改为 `MenuType` + client screen 注册。
- 保持客户端类和服务端逻辑分离。
- 验证：`gradlew.bat build`，手动 `runClient` 验证至少一个 GUI。
- 建议提交：`迁移网络和界面`

### 5. 实体、渲染与特殊物品

- 迁移重雪球、爆炸重雪球等实体。
- 迁移刀、研磨器、鱼竿、注入石、宝石等特殊物品逻辑。
- 迁移必要渲染器、模型、客户端事件。
- 验证：`gradlew.bat build`，实体行为补 GameTest 或手测记录。
- 建议提交：`迁移实体和特殊物品`

### 6. 指南书与玩家引导

- 迁移旧 `SkyResourcesGuide` 页面结构。
- 决策是否保留自研 GUI，或迁移为 Patchouli/原生书/自定义 screen。
- 迁移 VoidIslandControl 相关指南页。
- 已迁移自研指南 Screen、按键入口、页面搜索、跨分类搜索、可点击动作、可滚动结果/动作/结构列表、轻量 `{action:n}` 内联动作标记，以及基于 `GuideStructure` 坐标的 2D 等距结构预览。
- 验证：`gradlew.bat build`，手动打开指南。
- 建议提交：`迁移指南系统`

### 7. VoidIslandControl 内置化

- 重实现岛屿核心模型：
  - 岛屿类型注册。
  - 玩家岛屿数据保存。
  - 岛屿创建、重置、回家、访问、邀请、离开事件。
  - 起始岛、砂岛、雪岛、木岛、草岛、magma island。
  - Void 世界/维度或空岛世界预设。
- 当前状态：岛屿创建、回家、在线/保存名离线访问、本地缓存身份的离线 invite/trust、重置确认、模板切换、保护半径内重置清理、起始模板、magma island Crystal Fluid 源、void 维度/world preset 和共享 spawn 平台已完成；旧离开事件钩子、完整事件广播、外部 profile 查询和重命名冲突处理仍按风险 TODO 保留。
- 旧 jar 仅作为行为参考，不作为运行依赖。
- 迁移 `VICPlugin` 中的 magma island 生成逻辑。
- 验证：GameTest 或专用 server run，至少验证创建岛屿、传送、重置。
- 建议提交：`内置空岛控制功能`

### 8. 团队功能

- 明确团队数据模型：队伍、成员、权限、邀请、共享岛屿、共享出生点。
- 实现团队命令和持久化。
- 已接入可配置半径岛屿权限边界：岛主、队员和可信访客可破坏/放置/右键交互，未信任访客不可修改受保护岛屿。
- 验证：多人/模拟玩家测试或命令级单元测试。
- 建议提交：`完善团队岛屿功能`

### 9. 联动模组

Stage 59 已完成可用性审计，详见 `plans/integration-availability.md`。JEI、Jade 和 Integrated Dynamics 的可用部分已分阶段迁移；剩余目标继续按矩阵记录阻塞原因和 TODO。

优先级：

1. JEI -> 已实现配方查看器和指南 recipe action；REI/EMI 暂不重复实现。
2. The One Probe -> 未恢复旧 TOP API；已用 Jade 迁移热源、温度和多方块探针提示，WTHIT 仅保留备选。
3. Integrated Dynamics -> 已用条件 Life Infusion 配方恢复 Menril Berries/Sapling 获取；更深 API 交互暂不需要。
4. CraftTweaker -> 未确认 1.21.11 NeoForge 文件，脚本入口暂缓。
5. Forestry/Binnie's/Extra Bees -> 未确认稳定 1.21.11 NeoForge 生态，保留 TODO。
6. AE2、TConstruct、Thermal、IC2、Actually Additions 等 -> 当前审计多数未确认 1.21.11 NeoForge 版本。

每个联动必须记录：

- 目标模组是否有 1.21.11 可用版本。
- 使用的依赖坐标。
- 迁移状态：完成 / 暂缓 / 放弃。
- 暂缓原因和后续 TODO。

建议提交：`迁移可用模组联动`

### 10. 最终验证与收尾

- `gradlew.bat build`。
- `gradlew.bat runData`。
- `gradlew.bat runGameTestServer`，如果 GameTest 已建立。
- `runClient` 手测核心 GUI、物品栏、机器、岛屿命令。
- 检查旧资源/翻译/配方遗漏。
- 建议提交：`完成迁移验证收尾`

## 当前第一批实施范围

为保证每步可编译，第一批只做阶段 0：

- 更新元数据和入口类，移除模板示例内容。
- 建立基础注册包：
  - `registry/ModItems.java`
  - `registry/ModBlocks.java`
  - `registry/ModCreativeTabs.java`
- 先注册少量无复杂逻辑的基础块/物品，证明 NeoForge 1.21.11 注册链路。
- 保留复杂机器、GUI、网络、VoidIslandControl 为后续阶段。

## 风险与 TODO

- TODO：确认旧项目 ARR 资源迁移到目标 MIT 项目的许可策略。
- TODO：VoidIslandControl 只有 1.12.2 jar，本地未找到源码；完整内置化需要先提取行为清单，必要时反编译仅作理解参考。
- TODO：按 `plans/integration-availability.md` 逐项实现剩余可用联动；已完成 JEI、Jade 和 Integrated Dynamics 最小联动，其余等待目标版本/API。
- TODO：确认是否需要把旧存档中的主世界空岛迁移到 `skyresources3:void_island`；新建空岛、world preset 和共享 spawn 平台已在 Stage 22 完成。
- TODO：团队权限后续可继续细化角色矩阵和共享死亡回家点；当前已实现成员共享岛屿交互、可信访客白名单、可配置保护范围和本地缓存身份的离线 invite/trust。

## 最终缺口审计

详见 `plans/final-migration-gap-audit.md`。当前剩余工作应优先按该审计文档分类推进：先处理最终验证、资源许可决策和 VoidIslandControl 兼容性决策；联动版本阻塞、动态 modded recipe、视觉增强和更深团队权限不应混作主体迁移阻塞项。

## 2026-06-14 最终收口决策

- 资源许可：旧项目许可证为 ARR，目标项目为 MIT；除非项目所有者后续明确重授权，迁入或参考的旧资源不能自动视为 MIT 覆盖内容。发布前应保留来源/授权说明，或替换为可明确按目标许可证发布的新资源。
- VoidIslandControl 事件兼容：当前目标项目已内置空岛核心流程，且没有已验证的 1.21.11 外部消费者依赖旧 VIC 事件广播 API；本轮不新增完整事件兼容层，后续如出现真实消费方再按小范围 API 设计处理。
- 旧空岛存档迁移：新建空岛使用 `skyresources3:void_island`；旧主世界空岛记录不做静默迁移，避免改变玩家现有维度/坐标语义。若需要旧档升级，应另做显式迁移命令或工具。
- 最终验证记录：本轮收口证据写入 `plans/final-validation-compatibility-closeout-checklist.md`；`runData`、`runGameTestServer`、`build`、空白检查、Serena Java/LSP 检查和旧网络/资源命名扫描均已通过，`runClient` 视觉/GUIs 冒烟作为发布前人工验证项。
