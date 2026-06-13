# 客户端冒烟与资源授权说明收口

## Goal

把最终迁移剩余的发布前缺口继续向可证明状态推进：补齐目标项目 MIT 许可证文件与旧 ARR 资源边界说明，并尝试在受控时间内运行 `runClient`，至少获得客户端启动/加载层面的当前证据；如果无法自动完成可视 GUI 操作，则把人工 GUI 冒烟项清楚记录为发布前验证项。

## What I Already Know

* `plans/ask.md` 要求完成 SkyResources 到 NeoForge 1.21.11 的完整迁移，并尽力处理联动，无法迁移的联动写 TODO。
* `plans/final-validation-compatibility-closeout-checklist.md` 已记录 `runData`、`runGameTestServer`、`build`、空白检查、Serena Java/LSP 检查和旧网络/资源扫描通过。
* `plans/final-migration-gap-audit.md` 仍把 `runClient` GUI 冒烟作为发布前人工验证项。
* `gradle.properties` 声明 `mod_license=MIT`，但当前仓库根目录没有 `LICENSE` 文件。
* 旧项目许可证为 ARR；本项目已记录旧资源不能自动视作 MIT 重授权。

## Requirements

* 添加根目录 `LICENSE`，与 `gradle.properties` 的 `mod_license=MIT` 保持一致。
* 添加资源授权边界说明，明确：
  * 新迁移/新编写代码按仓库 MIT 许可证发布。
  * 旧项目 ARR 派生或复制资源不因迁入而自动变成 MIT。
  * 发布前若包含旧资源，应由项目所有者确认重授权、保留说明或替换资源。
* 更新最终 checklist、缺口审计和迁移计划，让许可证说明从“需要 license note”推进为“仓库已有 license note，仍需所有者最终授权确认/替换”。
* 尝试运行 `./gradlew.bat runClient` 的受控启动冒烟：
  * 不让客户端无限期占用会话。
  * 捕获是否进入客户端启动和模组加载阶段。
  * 不把未实际点击验证的指南/JEI/机器 GUI 说成已通过。
* 保持任务范围为发布前收口，不新增玩法功能。

## Acceptance Criteria

* [ ] 根目录存在 `LICENSE`，且与 `mod_license=MIT` 一致。
* [ ] 存在旧 ARR 资源边界说明文件，并被迁移计划/checklist 引用。
* [ ] `runClient` 尝试有当前会话证据；若只能证明启动加载，不覆盖 GUI 点击，则文档明确区分。
* [ ] `./gradlew.bat build` 通过。
* [ ] `git diff --check` 和 `git diff --cached --check` 通过。
* [ ] `scripts/check-serena-java.ps1` 通过。
* [ ] 中文 commit，随后归档任务并记录会话。

## Definition of Done

* 许可证元数据、根 LICENSE 和资源授权说明不互相矛盾。
* 发布前剩余人工项被明确列出，不伪造自动验证结论。
* 工作树最终干净。

## Technical Approach

* 许可证正文使用标准 MIT 文本，版权人采用当前项目作者 `cnlimiter`。
* 资源说明使用单独 Markdown 文件，避免把旧 ARR 边界塞进许可证正文导致许可证语义混乱。
* `runClient` 使用超时/进程清理策略，只记录可证明的启动加载证据；如客户端不能在自动化窗口中完成交互，GUI 点击继续保留为人工验证。

## Out of Scope

* 不替用户作法律结论或代表旧项目所有者完成重授权。
* 不实现额外 GUI 自动化框架。
* 不新增新机器、配方、命令或联动功能。

## Technical Notes

* 参考：`plans/final-validation-compatibility-closeout-checklist.md`
* 参考：`plans/final-migration-gap-audit.md`
* 参考：`gradle.properties`
* MIT 许可证文本以 SPDX/OSI 标准文本为基准。
