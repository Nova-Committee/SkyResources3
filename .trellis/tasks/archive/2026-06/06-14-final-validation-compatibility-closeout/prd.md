# 最终验证与兼容性收口

## Goal

基于 `plans/ask.md` 的完整迁移目标和 `plans/final-migration-gap-audit.md` 的缺口审计，把当前迁移主体从“功能基本完成”推进到“可证明的最终收口状态”：运行最终质量门禁，记录资源许可证策略，明确 VoidIslandControl 兼容取舍，并清理仍会误导后续工作的旧 checklist/TODO。

## What I Already Know

* 目标项目是 Minecraft 1.21.11 + NeoForge 21.11.42 的 SkyResources3 迁移工程。
* 主体功能已覆盖方块、物品、配方、网络、GUI、机器、多方块、指南、空岛命令、团队、离线身份，以及 JEI/Jade/Integrated Dynamics 的最小有效联动。
* `plans/final-migration-gap-audit.md` 已把剩余项分成阻塞收口、允许延期和增强项。
* 用户已要求后续自动确认，因此本轮按推荐的保守兼容策略推进，不再停顿等待每个决策确认。

## Requirements

* 运行最终验证门禁：`runData`、`runGameTestServer`、`build`，并记录结果。
* 评估 `runClient` 冒烟测试可行性；若不能在自动化会话中安全完成，记录为人工验证项，不伪造通过结果。
* 明确旧 ARR 资源迁入 MIT 目标项目的策略，避免把未重授权资源错误声明为 MIT 覆盖内容。
* 明确 VoidIslandControl 兼容策略：
  * 旧事件广播 hook 若无当前内部/外部消费者，不作为本轮阻塞实现。
  * 旧主世界空岛记录不静默迁移到 void 维度，后续如需要提供显式迁移工具。
* 清理或重分类旧 checklist 中已经由后续阶段完成的 stale TODO。
* 更新 `plans/migration-plan.md` 和 `plans/final-migration-gap-audit.md`，让后续判断不再依赖过时缺口。
* 不引入新的大范围玩法功能；只在验证发现真实构建/数据问题时做必要修复。

## Acceptance Criteria

* [ ] `plans/final-validation-compatibility-closeout-checklist.md` 或等价 checklist 记录本轮范围、决策和验证证据。
* [ ] `plans/migration-plan.md` 记录资源许可证、VoidIslandControl 兼容和最终验证状态。
* [ ] `plans/final-migration-gap-audit.md` 中的“Must Finish”项被更新为已处理、需人工验证或非阻塞延期。
* [ ] `./gradlew.bat runData` 完成，或失败原因被修复/记录。
* [ ] `./gradlew.bat runGameTestServer` 完成，或失败原因被修复/记录。
* [ ] `./gradlew.bat build` 完成，或失败原因被修复/记录。
* [ ] `git diff --check` 与 `git diff --cached --check` 无空白错误。
* [ ] `scripts/check-serena-java.ps1` 通过，确认 Serena 仍按 Java/LSP 项目识别。
* [ ] 只提交本任务相关文件，不混入运行目录、IDE 文件或无关本地改动。

## Definition of Done

* 文档中的剩余迁移状态与实际验证结果一致。
* 所有自动化验证命令都有当前会话证据。
* 若存在需要人工 GUI 验证或许可证负责人确认的事项，必须清楚标为“人工/授权确认项”，不得把它们说成已自动完成。
* 完成中文 commit；随后归档 Trellis 任务并记录会话。

## Technical Approach

* 采用文档优先的收口方式：先把兼容/许可证决策固化，再跑最终验证。
* 对许可证采用保守策略：旧 ARR 资源不得因为源码迁入 MIT 仓库而自动视作 MIT 重授权；除非项目所有者明确授权，否则应保留来源/授权说明或后续替换。
* 对 VoidIslandControl 采用内置功能优先策略：当前没有旧 1.12.2 jar 运行依赖，也没有已验证的外部消费者时，不新增兼容事件 API；旧存档迁移必须显式触发，避免破坏玩家现有位置和维度语义。
* 对 `runClient` 采用非交互会话安全策略：不让长期交互客户端阻塞自动化流程；若需要人工点击 GUI，则写入 checklist。

## Out of Scope

* 新增 CraftTweaker、Forestry/Binnie’s/Extra Bees、AE2、TConstruct、Thermal、IC2/Tech Reborn 等未验证 1.21.11 NeoForge 目标版本的联动。
* 实现旧存档自动迁移工具。
* 新增完整 VoidIslandControl 事件兼容 API。
* 扩展团队角色矩阵、共享死亡回家点、更多视觉变体或额外 GameTest 覆盖。

## Technical Notes

* 参考文件：
  * `plans/ask.md`
  * `plans/migration-plan.md`
  * `plans/final-migration-gap-audit.md`
  * `plans/integration-availability.md`
  * `.trellis/spec/backend/index.md`
* 本轮按 Codex inline Trellis 工作流执行：Phase 1 补 PRD，Phase 2 直接读取 spec 并实施，Phase 3 验证、提交、归档。
