# 空岛访问与团队命令回归覆盖

## Goal

补齐已迁移空岛访问、访客信任和团队协作命令的 GameTest 覆盖，并同步 Stage 18/19 checklist，防止后续迁移重复实现已经存在的 `/island visit`、`/island leave`、`trust/untrust/trusted` 和团队 home/leave/disband 行为。

## Requirements

- 为 `/island visit <player>` 添加 GameTest，验证玩家可访问另一个在线玩家的空岛 home。
- 为团队最小闭环添加 GameTest：
  - 岛主创建空岛并邀请成员。
  - 成员接受邀请后可通过 `/island home` 或 `/skyresources3 team home` 回到队长空岛。
  - 成员执行 `/island leave` 后退出队伍，队伍记录不再包含该成员。
  - 岛主可通过 `/skyresources3 team disband` 解散队伍。
- 为可信访客命令添加 GameTest：
  - `/island trust <player>` 持久化可信访客。
  - `/island trusted` 可成功列出可信访客。
  - `/island untrust <player>` 移除可信访客。
- 清理 `VoidIslandCommands#islandNode` 中重复注册的 `trust/untrust/trusted` literal，保持命令树单一来源。
- 同步 `plans/stage-18-void-island-commands-checklist.md` 与 `plans/stage-19-team-island-collaboration-checklist.md` 中已过期的命令暂缓项。

## Acceptance Criteria

- [x] 新增 GameTest 覆盖访问、团队 home/leave/disband 和 trust/untrust/trusted。
- [x] `VoidIslandCommands#islandNode` 不再重复声明相同的 trust/untrust/trusted literal。
- [x] Stage 18/19 checklist 不再把已实现且已测试的命令标记为“留待后续”。
- [x] `./gradlew.bat compileJava` 通过。
- [x] `./gradlew.bat build` 通过。
- [x] `./gradlew.bat runGameTestServer` 通过。
- [x] `git diff --check` 与 `git diff --cached --check` 通过。

## Out of Scope

- 不新增离线邀请、玩家名历史解析或 UUID 缓存。
- 不实现细分角色矩阵、跨维度保护或共享死亡回家点。
- 不改变现有岛屿模板、void world 生成或保护半径语义。

## Technical Approach

- 复用现有 `IslandCommandGameTests` 的 mock player 和 dispatcher 执行方式。
- 测试直接断言 `IslandSavedData` / `TeamSavedData` 的持久化状态与玩家传送位置。
- 对命令树只做重复 literal 删除，不改变命令名称、参数或消息 key。
