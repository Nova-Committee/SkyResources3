# 同步迁移计划完成状态

## Goal

把迁移计划中已经由后续阶段完成、但仍被早期清单标记为暂缓或 TODO 的事项同步为当前事实，减少后续继续迁移时的重复排查。

## What I already know

* 当前工作区干净，最新完成了 Survivalist Fishing Rod 回归验证。
* `process/infusion/*.json`、`InfusionRecipes`、`LifeInfuserBlockEntity` 已经证明生命灌注运行时读取 NeoForge recipe manager。
* `IslandTemplate`、`VoidIslandWorld`、`VoidIslandCommands` 已经覆盖岛屿模板、magma starter、void dimension / world preset / spawn platform。
* `RuntimeMigrationGameTests`、`LifeInfusionGameTests` 和 `ModGameTests` 已经覆盖 cutting knife、rock grinder、infusion stone、Life Infuser 运行时。

## Requirements

* 更新阶段清单中与当前代码事实不一致的“暂缓迁移”条目。
* 同步总计划风险 TODO，只保留真实未完成或仍需决策的事项。
* 不修改运行时代码，不改变已迁移行为。
* 保留真正仍未完成的 TODO，例如 magma island 是否放置 Crystal Fluid 源、Garden of Glass 外部生态、团队权限深化等。

## Acceptance Criteria

* [x] Stage 1/3 不再把已实现的工具运行时、处理配方基础或 survivalist rod 误标为静态壳子。
* [x] Stage 17 不再把 Life Infuser recipe type / item automation 误标为暂缓。
* [x] Stage 18/21 不再把 void dimension、world preset、island templates 和 magma starter 误标为待实现。
* [x] `plans/migration-plan.md` 风险 TODO 与当前 Stage 22 / Stage 19 状态一致。
* [x] `git diff --check` 通过。

## Definition of Done

* 文档引用当前真实代码/资源文件。
* 不新增假完成项，不删除仍有阻塞原因的 TODO。
* 完成后中文提交、归档任务并记录会话。

## Technical Notes

* 目标文件：`plans/stage-1-tools-checklist.md`、`plans/stage-3-projectiles-checklist.md`、`plans/stage-17-life-infuser-runtime-checklist.md`、`plans/stage-18-void-island-commands-checklist.md`、`plans/stage-21-island-templates-checklist.md`、`plans/migration-plan.md`。
* 证据文件：`InfusionRecipes.java`、`LifeInfuserBlockEntity.java`、`IslandTemplate.java`、`VoidIslandWorld.java`、`RuntimeMigrationGameTests.java`、`LifeInfusionGameTests.java`。
