# 迁移生存者钓鱼竿回归验证

## Goal

确认并锁定 Survivalist Fishing Rod 在 1.21.11/NeoForge 迁移后的运行时契约，避免旧计划仍把该功能标记为暂缓，同时为后续资源路线迁移提供可重复验证。

## What I already know

* 旧版 `ItemSurvivalFishingRod` 使用自定义 `EntitySurvivalistHook`，成功咬钩时读取 `skyresources:gameplay/fishingsurvivalist` 掉落表。
* 当前实现已有 `SurvivalistFishingRodItem` 和 `SurvivalistFishingEvents`，通过 NeoForge `ItemFishedEvent` 使用 `skyresources3:gameplay/fishingsurvivalist` 掉落表替换原版钓鱼掉落。
* 当前实现没有注册自定义鱼钩实体，保留原版 `FishingHook` 生命周期，仅在事件边界替换掉落行为，符合 KISS/YAGNI。
* `plans/stage-5-health-gem-checklist.md` 仍将 `survivalist_fishing_rod` 标记为依赖自定义鱼钩实体的暂缓项，需要与当前迁移状态同步。

## Requirements

* 为 Survivalist Fishing Rod 的 `ItemFishedEvent` 路径增加 GameTest，覆盖自定义掉落替换、事件取消、原版输入掉落不生成、耐久损耗归零。
* 不引入 1.12.2 风格自定义鱼钩实体，除非测试暴露无法通过事件层达成的行为缺口。
* 同步阶段 checklist，明确当前 1.21.11 实现采用原版鱼钩加 NeoForge 钓鱼事件的迁移策略。

## Acceptance Criteria

* [x] GameTest 能证明 survivalist rod 持有者触发 `ItemFishedEvent` 后事件被取消。
* [x] GameTest 能证明事件原始掉落不会生成到世界。
* [x] GameTest 能证明 handler 会生成至少一个自定义掉落和经验球。
* [x] GameTest 能证明 handler 将事件钓竿损耗设为 `0`。
* [x] `plans/stage-5-health-gem-checklist.md` 不再把 `survivalist_fishing_rod` 标记为暂缓。
* [x] `./gradlew.bat compileJava`、`./gradlew.bat build`、`./gradlew.bat runGameTestServer` 通过。

## Definition of Done

* 测试已注册到现有 `ModGameTests`。
* 代码遵循现有 `committee.nova.mods.skyresources3.test` GameTest 组织方式。
* 不新增依赖或泛化抽象。
* 完成后按项目规则提交中文 git commit，并归档 Trellis 任务。

## Technical Approach

使用当前 `SurvivalistFishingEvents` 作为被测入口，构造带 Survivalist Fishing Rod 的 mock `ServerPlayer`、`FishingHook` 和包含哨兵原版掉落的 `ItemFishedEvent`。测试直接调用事件处理器，断言事件取消、损耗归零、哨兵掉落未出现、自定义掉落与经验实体已生成。

## Out of Scope

* 不迁移 1.12.2 `EntitySurvivalistHook` 的完整鱼群 AI 和私有计时状态。
* 不调整钓鱼掉落权重，除非现有 JSON 无法加载。
* 不处理 GUI、网络或其他资源机器迁移。

## Technical Notes

* 现有实现文件：`SurvivalistFishingRodItem.java`、`SurvivalistFishingEvents.java`、`ModGameTests.java`。
* NeoForge `ItemFishedEvent` 取消语义：取消后原版掉落不会生成，但会返回事件上的 rod damage。
* 旧版成功咬钩路径会生成掉落和 1-6 点经验，并不会在自定义钓鱼竿 override 中损耗物品耐久。
