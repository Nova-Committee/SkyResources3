# 空岛重置完整清理

## Goal

补齐 VoidIslandControl 迁移中仍被标记为 deferred 的 full island wipe：执行 `/island reset ... confirm` 时，不再只清理 5x5 starter footprint，而是清理该岛屿受保护范围内的旧方块后再重建选定模板，减少玩家重置后旧平台、测试方块或误放置方块残留。

## What I already know

* `VoidIslandCommands.resetIsland` 当前在 `island.home().below()` 调用 `clearStarterIslandArea`，只清理 `STARTER_RESET_RADIUS` 范围。
* `Config.islandProtectionRadius` 已经是岛屿交互保护边界的中心配置，`IslandProtectionEvents` 和 `IslandSavedData.findIslandAt` 都以该半径作为岛屿水平范围。
* `IslandCommandGameTests.createInfoAndReset` 已验证 reset 命令和模板类型持久化，但尚未断言实际清理范围。
* Stage 20 原先记录 “Full island wipe remains deferred”；Stage 67 已提供可配置保护边界。

## Requirements

* `/island reset confirm` 和 `/island reset <type> confirm` 必须清理当前岛屿中心周围 `Config.islandProtectionRadius` 水平范围内的方块，并用岛屿间距推导出的安全上限避免误清邻岛。
* 清理必须复用现有 reset 垂直范围，避免全世界高度扫描和不必要的性能/破坏面。
* 清理后继续按选定 `IslandTemplate` 重建 starter，并继续持久化模板类型。
* 保留现有安全边界：只有个人岛 owner 可 reset；team member 不能 reset team owner 岛屿；目标维度缺失时失败。
* 不新增危险命令、不新增整维度清空、不改变访问/信任/团队保护语义。

## Acceptance Criteria

* [x] `VoidIslandCommands` 的 reset 清理范围从 starter footprint 扩展到岛屿保护半径，并保留岛屿间距安全上限。
* [x] 新增或更新 GameTest：在保护半径内放置残留方块，reset 后被清空；保护半径外哨兵方块保留。
* [x] Stage 20 checklist 不再把 full island wipe 标记为 deferred，并说明离线访问等真实剩余项仍 deferred。
* [x] `./gradlew.bat compileJava` 通过。
* [x] `./gradlew.bat runGameTestServer` 通过。
* [x] `./gradlew.bat build` 通过。
* [x] `git diff --check` 和 `git diff --cached --check` 通过。

## Definition of Done

* 实现保持 KISS：复用现有 config 和 reset helper，不新增独立配置或复杂队列。
* 行为由命令级 GameTest 覆盖，避免只验证 helper 细节。
* 更新迁移计划/阶段清单，任务完成后中文提交、归档并记录会话。

## Technical Approach

* 将 `clearStarterIslandArea` 调整为 `clearIslandResetArea` 一类的 helper，水平半径使用 `Config.islandProtectionRadius` 与岛屿间距安全上限的较小值，垂直范围继续使用 `STARTER_RESET_MIN_Y_OFFSET` 到 `STARTER_RESET_MAX_Y_OFFSET`。
* 在 `IslandCommandGameTests.createInfoAndReset` 中将 `Config.islandProtectionRadius` 暂设为较小值，放置内部残留和外部哨兵，执行 reset 后断言内部清空、外部保留。
* 注册项可复用现有 `island_create_reset` GameTest，不新增测试 fixture。

## Out of Scope

* 离线 player visit/profile cache。
* 旧 VoidIslandControl 离开事件钩子和完整事件广播。
* 旧存档主世界岛屿到 `skyresources3:void_island` 的迁移工具。
* Magma island Crystal Fluid 源放置决策。

## Technical Notes

* 相关源码：`VoidIslandCommands.java`、`IslandSavedData.java`、`IslandProtectionEvents.java`、`IslandCommandGameTests.java`、`ModGameTests.java`。
* 相关文档：`plans/stage-20-island-reset-visit-checklist.md`、`plans/migration-plan.md`、`.trellis/spec/backend/island-command-guidelines.md`。
