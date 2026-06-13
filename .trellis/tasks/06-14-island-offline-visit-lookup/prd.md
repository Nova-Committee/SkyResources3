# 空岛离线访问查找

## Goal

补齐 `/island visit <player>` 的离线查找缺口：当目标玩家不在线时，命令应能通过 `IslandSavedData.ownerName` 或 `TeamSavedData` 中保存的成员名找到可访问岛屿并传送访问者，而不是直接报“玩家不在线”。

## What I already know

* `/island visit` 已经使用 `StringArgumentType.word()`，命令语法不强制在线实体参数。
* `VoidIslandCommands.visitIsland` 当前先用 `PlayerList#getPlayerByName` 查在线玩家；查不到就失败。
* `IslandSavedData.IslandRecord` 已持久化 `ownerName`。
* `TeamSavedData.TeamRecord` 已持久化 `ownerName` 与 `members` 名称。
* 在线 visit 现有语义是：先访问目标自己的岛，再访问目标所在团队 owner 的岛。

## Requirements

* 在线目标行为保持不变：自我访问仍失败，目标自己的岛优先于团队 owner 岛。
* 离线目标名匹配某个 island owner name 时，访问该 owner 岛。
* 离线目标名匹配某个 team owner/member name 且该 team owner 有岛时，访问 team owner 岛。
* 名称匹配使用大小写不敏感比较，并只基于当前 SavedData 中已经存在的最后记录名。
* 不引入 profile cache、离线 UUID 解析、离线邀请或离线 trust；这些仍是后续更完整玩家名历史层的范围。

## Acceptance Criteria

* [x] `IslandSavedData` 支持按 owner name 查找岛屿。
* [x] `TeamSavedData` 支持按 owner/member name 查找团队。
* [x] `/island visit <name>` 在目标离线但 SavedData 有记录时可传送到对应岛屿。
* [x] 新增 GameTest 覆盖离线 owner name 与离线 team member name 两类访问。
* [x] Stage 20 / Stage 68 / island command spec 不再把 offline visit lookup 标记为 deferred；仍保留 profile cache、离开事件和事件广播等真实剩余项。
* [x] `./gradlew.bat compileJava`、`./gradlew.bat runGameTestServer`、`./gradlew.bat build` 通过。
* [x] `git diff --check` 和 `git diff --cached --check` 通过。

## Definition of Done

* 实现复用现有 SavedData，不新增外部依赖或新的持久化系统。
* 命令失败继续使用现有本地化消息，不新增不必要文案。
* 完成后中文提交、归档任务并记录会话。

## Technical Approach

* 在 `IslandSavedData` 增加 `findIslandByOwnerName(String)`。
* 在 `TeamSavedData` 增加 `findTeamByPlayerName(String)` 和 `TeamRecord.includesName(String)`。
* 在 `VoidIslandCommands.visitIsland` 中将在线/离线解析统一到一个小型 `VisitTarget` helper，在线路径仍用 UUID，离线路径按保存名 fallback。
* 在 `IslandCommandGameTests` 新增离线访问测试，并在 `ModGameTests` 注册。

## Out of Scope

* 离线玩家 UUID/profile cache。
* 离线 invite / trust。
* 多个历史同名玩家的冲突解决。
* 旧 VoidIslandControl 离开事件钩子和完整事件广播。

## Technical Notes

* 相关源码：`VoidIslandCommands.java`、`IslandSavedData.java`、`TeamSavedData.java`、`IslandCommandGameTests.java`、`ModGameTests.java`。
* 相关文档：`plans/stage-20-island-reset-visit-checklist.md`、`plans/stage-68-island-reset-full-wipe-checklist.md`、`.trellis/spec/backend/island-command-guidelines.md`。
