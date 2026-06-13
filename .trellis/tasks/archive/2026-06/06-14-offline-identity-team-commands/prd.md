# 离线身份与团队岛屿命令补齐

## Goal

补齐内置 VoidIslandControl/团队迁移中仍被标记为 deferred 的离线身份层：用 Minecraft SavedData 保存玩家最近已知名称和 UUID，使 `/island visit`、团队邀请和信任命令可以基于本模组已见过或已保存的玩家身份工作，而不是只依赖在线玩家对象。

## What I already know

- `plans/ask.md` 要求完成 VoidIslandControl 内置化并完善团队功能。
- `plans/migration-plan.md` 当前仍把 profile cache、离线 invite/trust、旧离开事件/事件广播列为 VoidIslandControl 风险 TODO。
- Stage 69 已完成 `/island visit` 对 `IslandSavedData.ownerName` / `TeamSavedData` 保存名的离线查找，但明确把 profile cache 与离线 invite/trust 留作后续。
- `IslandSavedData` 和 `TeamSavedData` 已持久化 owner/member 名称，但没有单独的玩家身份索引。
- `/island trust <player>` 和 `/skyresources3 team invite <player>` 当前使用 `PlayerList#getPlayerByName`，目标离线时直接失败。
- 用户已要求后续自动确认，因此本阶段按推荐 MVP 直接实施。

## Requirements

- 新增一个简单、无外部依赖的 SavedData 身份缓存，记录本模组已观察到或已保存过的玩家 `UUID -> lastKnownName`。
- 在玩家上线、创建岛屿、团队邀请、接受邀请、信任访客等已知身份边界刷新缓存。
- 保持在线命令行为优先：目标在线时使用在线 UUID 和当前名称。
- 当目标离线但身份缓存中存在唯一名称匹配时，团队邀请和 island trust 可以解析到该 UUID。
- 离线团队邀请仍必须遵守现有校验：目标已有岛屿、已在团队、已有待处理邀请时拒绝。
- 离线 trust 仍必须遵守现有校验：不能 trust 自己、不能 trust 已是团队成员、不能重复 trust。
- 离线名称匹配大小写不敏感；若没有缓存命中则继续返回本地化失败消息。
- 将旧 “profile cache / offline invite / offline trust” TODO 更新为已实现的有限本地缓存语义，保留 rename conflict 和跨服务器全量 profile lookup 的真实 TODO。
- 添加 GameTest 覆盖离线团队邀请/接受和离线 trust 的持久化效果。

## Acceptance Criteria

- [x] 新 SavedData 能持久化并查找玩家最近已知名称。
- [x] `/skyresources3 team invite <离线已缓存玩家名>` 创建待处理邀请，玩家稍后上线后可 `/island accept`。
- [x] `/island trust <离线已缓存玩家名>` 可持久化访客 UUID 和最近名称。
- [x] 在线目标路径保持现有行为不退化。
- [x] 新增或更新 GameTest 覆盖离线 invite/accept 和离线 trust。
- [x] 更新 island command spec、migration plan 和相关 stage checklist。
- [x] `./gradlew.bat compileJava`、`./gradlew.bat runGameTestServer`、`./gradlew.bat build` 通过。
- [x] `git diff --check` 和 `git diff --cached --check` 通过。
- [x] 修改 Serena 配置/脚本或遇到语言识别异常时运行 Serena Java 检查；本阶段完成前也运行一次项目要求的 Serena Java 检查。

## Definition of Done

- 只使用 Minecraft/NeoForge SavedData，不引入数据库、网络查询或新依赖。
- 命令文案继续使用 `Component.translatable` 和 `en_us.json`。
- 行为、测试、计划和 spec 同步。
- 完成后中文提交、归档 Trellis 任务并记录会话。

## Technical Approach

新增 `PlayerIdentitySavedData`，使用 `Codec.unboundedMap(UUIDUtil.STRING_CODEC, Codec.STRING)` 存储最近名称。`VoidIslandCommands` 提供一个小型目标解析 helper：在线优先，否则查身份缓存。为避免过度设计，缓存只解决本模组已见过玩家的离线命令，不做 Mojang profile API 查询、不保留多历史名称索引。

## Decision (ADR-lite)

Context: 旧计划中的 “profile cache” 可无限扩大到外部 Mojang profile 查询和历史重名冲突处理，但当前迁移目标只需要让已见过/已保存玩家的离线团队与信任流程闭环。

Decision: 采用本地 SavedData 身份缓存，在线优先、离线按最近名称唯一解析。更完整的跨服务器 profile lookup 和重命名冲突处理继续作为风险 TODO。

Consequences: 该方案可被 GameTest 可靠验证，和现有 island/team SavedData 一致，避免引入外部网络依赖；代价是不能解析从未进入过服务器、也未被本模组记录过的玩家名。

## Out of Scope

- Mojang profile service 查询。
- 多历史同名/重命名冲突解决。
- 跨服务器或跨存档身份同步。
- 完整旧 VoidIslandControl 事件 API 复刻。
- 团队角色矩阵和共享死亡回家点。

## Technical Notes

- 相关代码：`VoidIslandCommands.java`、`IslandSavedData.java`、`TeamSavedData.java`、`IslandCommandGameTests.java`、`ModGameTests.java`。
- 相关规范：`.trellis/spec/backend/island-command-guidelines.md`、`.trellis/spec/backend/database-guidelines.md`、`.trellis/spec/backend/error-handling.md`、`.trellis/spec/backend/quality-guidelines.md`。
- 相关计划：`plans/migration-plan.md`、`plans/stage-20-island-reset-visit-checklist.md`、`plans/stage-68-island-reset-full-wipe-checklist.md`、`plans/stage-69-island-offline-visit-checklist.md`。
