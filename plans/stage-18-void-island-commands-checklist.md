# 阶段 18 实施清单：VoidIslandControl 命令和持久化骨架

> 对应总计划：`plans/migration-plan.md` 的“VoidIslandControl 内置化”和“团队功能”前置数据层。

## 目标

- 建立内置空岛功能的服务端持久化数据入口。
- 提供最小可运行命令：创建空岛、返回 home、查看个人空岛信息。
- 为后续岛屿类型、重置、访问、邀请和团队权限保留清晰数据边界。

## 已迁移行为

- 新增 `IslandSavedData`，通过 1.21.11 `SavedDataType` 持久化玩家空岛记录。
- 每个空岛记录保存：
  - 所有者 UUID
  - 所有者名称
  - 维度键
  - home 坐标
- 新增 `/island create` 和 `/skyresources3 island create`。
- 新增 `/island home` 和 `/skyresources3 island home`。
- 新增 `/island spawn`。
- 新增 `/island visit <player>`。
- 新增 `/island reset [type] confirm`。
- 新增 `/island info` 和 `/skyresources3 island info`。
- 创建空岛时在主世界高空生成 5x5 草方块起始平台，并放置一棵橡树树苗。
- 空岛命令受 `enableVoidIslandFeatures` 配置开关控制。

## 暂缓迁移

- 自定义 void 维度、世界预设和 chunk generator 留待世界生成阶段。
- 旧 VoidIslandControl 离线访问、离开事件钩子和完整事件广播留待后续空岛阶段。
- 起始箱、岛屿模板和 magma island 生成逻辑留待岛屿类型阶段。

## 验证

- [x] `./gradlew.bat compileJava`
- [x] `./gradlew.bat runData`
- [x] `./gradlew.bat build`
- [x] `./gradlew.bat runGameTestServer`
- [x] `git diff --check`
