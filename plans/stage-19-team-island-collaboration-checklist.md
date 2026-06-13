# 阶段 19 实施清单：团队岛屿协作骨架

> 对应总计划：`plans/migration-plan.md` 的“团队功能”和 VoidIslandControl 协作命令迁移。

## 目标

- 建立空岛团队持久化数据。
- 恢复旧 VoidIslandControl `invite` 类协作语义的最小闭环。
- 让队员可以共享队长空岛 home，为后续权限、访问和团队岛屿交互打基础。

## 已迁移行为

- 新增 `TeamSavedData`，使用 Minecraft `SavedDataType` 持久化队伍。
- 队伍以岛主 UUID 为根，保存：
  - 队长 UUID 和名称
  - 成员 UUID 到名称映射
  - 待接受邀请 UUID 到名称映射
- 新增 `/skyresources3 team create`。
- 新增 `/island invite <player>` 和 `/skyresources3 team invite <player>`。
- 新增 `/island accept` 和 `/skyresources3 team accept`。
- 新增 `/island leave`，以及 `/skyresources3 team leave|disband|home|info`。
- 避免注册顶层 `/team`，防止与原版计分板队伍命令冲突。
- `/island home` 和 `/island info` 在玩家没有个人空岛时会解析其团队队长空岛。
- 玩家已有个人空岛或已在队伍中时不能接受邀请，避免归属冲突。
- 队伍成员不能创建个人空岛，避免一个玩家同时绑定两个空岛归属。
- 命令消息改为 `Component.translatable` 并补充 `en_us.json`。

## Stage 66 已补齐

- 固定半径岛屿保护已接入：队长和队员可在队长岛屿范围内破坏、放置和右键交互。
- 访客和无关玩家在受保护岛屿范围内的破坏、放置和右键交互会被拒绝。

## Stage 67 已补齐

- 岛屿保护半径已改为 `islandProtectionRadius` 配置，默认保留 128。
- 岛主可通过 trust/untrust/trusted 命令维护可信访客白名单。
- 可信访客可在受保护岛屿范围内交互；未信任访客仍会被拒绝。

## Stage 68 已补齐

- `/island visit <player>` 已补充 GameTest，验证在线玩家可访问目标玩家空岛 home。
- `/island invite`、`/island accept`、`/island home`、`/island leave`、`/skyresources3 team home` 和 `/skyresources3 team disband` 已补充团队闭环 GameTest。
- `/island trust`、`/island trusted` 和 `/island untrust` 已补充可信访客持久化 GameTest。

## 暂缓迁移

- 细分角色矩阵仍留待后续权限深化阶段。
- 离线邀请、玩家名历史解析和 UUID 缓存留待账户数据阶段。
- 团队共享出生点、死亡回家点和跨维度保护留待空岛系统深化阶段。
- 旧 VIC 完整离开事件、更完整 profile cache 和更细粒度访问控制留待后续阶段；保存名离线 visit 查找已在 Stage 69 完成。

## 验证

- [x] `./gradlew.bat compileJava`
- [x] `./gradlew.bat runData`
- [x] `./gradlew.bat build`
- [x] `./gradlew.bat runGameTestServer`
- [x] `git diff --check`
