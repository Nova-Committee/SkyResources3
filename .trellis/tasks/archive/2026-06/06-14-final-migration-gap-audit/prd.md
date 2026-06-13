# 最终迁移缺口审计

## Goal

基于 `plans/ask.md` 的完整迁移目标，对当前 `plans/`、`.trellis/spec/` 和已实现代码状态做一次收口审计，产出一份后续执行用的权威缺口清单，避免继续在大量 stage checklist 中重复扫描。

## What I already know

- 模组主体已经完成大量阶段迁移，并持续通过 `compileJava`、`runGameTestServer` 和 `build`。
- 最新阶段已完成本地离线身份缓存、离线团队邀请和离线 trust。
- `plans/migration-plan.md` 仍保留风险 TODO：旧 ARR 资源策略、VoidIslandControl 旧事件/广播、剩余联动、旧主世界空岛迁移、团队权限增强。
- `plans/integration-availability.md` 已把 JEI、Jade、Integrated Dynamics 列为已实现，把 CraftTweaker、Forestry/Binnie's、AE2、TConstruct、Thermal、IC2/Tech Reborn 等列为目标版本/API 不明确的暂缓项。
- `rg` 扫描显示大量 `Deferred` 条目，其中一部分已被后续 stage 完成但旧 checklist 未同步，一部分是明确版本/策略阻塞，一部分是可选增强。

## Requirements

- 汇总 `plans/ask.md` 明确要求的范围：方块、物品、网络、界面/GUI、多方块结构、命令、世界生成、VoidIslandControl、团队功能、联动模组。
- 从当前 `plans/` 与 spec 中提取仍未关闭的 TODO/deferred 项。
- 将缺口分为三类：
  - 必须完成：阻碍宣布主体迁移完成的功能或验证。
  - 可暂缓但需记录：目标版本/API/策略阻塞，符合用户“没有合适版本可以搁置但写 TODO”的要求。
  - 增强项：视觉、资源、角色矩阵、外部查询等不阻塞主体迁移的后续工作。
- 识别明显已完成但旧 checklist 仍误报 deferred 的条目，并更新这些 checklist 或在审计文档中标记为“历史残留”。
- 产出 `plans/final-migration-gap-audit.md`，作为后续大阶段选择依据。
- 更新 `plans/migration-plan.md` 的当前状态/后续顺序，使下一步实现不依赖零散 grep。

## Acceptance Criteria

- [x] `plans/final-migration-gap-audit.md` 存在，并按 ask.md 范围列出完成状态和剩余缺口。
- [x] 每个剩余缺口都有分类、证据来源、建议下一步。
- [x] 明确列出“主体迁移完成前必须处理”的最小剩余集合。
- [x] 已完成但历史 checklist 仍误报的条目被同步修正或在审计中标记。
- [x] `plans/migration-plan.md` 指向最终审计文档。
- [x] `git diff --check` 与 `git diff --cached --check` 通过。

## Definition of Done

- 这是文档/计划任务，不改运行时代码。
- 审计结论必须基于当前文件证据，不凭记忆宣布完成。
- 完成后中文提交、归档任务并记录会话。

## Technical Approach

使用 `plans/ask.md` 作为顶层要求，结合 `plans/migration-plan.md`、`plans/integration-availability.md`、stage checklist 的 `TODO` / `Deferred` 扫描结果，整理成一个稳定审计文档。只对明显过期的 checklist 状态做同步，不展开新实现。

## Out of Scope

- 本任务不实现新的机器、GUI、联动或资源。
- 不重新搜索互联网确认每个暂缓联动的最新版本；版本可用性刷新可作为后续专门任务。
- 不宣布全迁移完成，除非审计证据能覆盖全部 ask.md 要求。

## Technical Notes

- 初始扫描命令：`rg -n "TODO|Deferred Migration|Deferred|deferred|暂缓|放弃|缺|未完成|\\[ \\]" plans .trellis/spec/backend`
- 重点文件：`plans/ask.md`、`plans/migration-plan.md`、`plans/integration-availability.md`、`plans/stage-*.md`。
