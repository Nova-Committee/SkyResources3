# 迁移指南富文本入口

## Goal

Stage 55-57 已经迁移了指南页面数据、客户端入口、搜索和目录，但旧版 `<recipe>`、`<link>`、`<image>` 标记仍没有可交互替代。本阶段为指南页面补上一个 1.21.11 友好的动作模型，并在 `GuideScreen` 中渲染可点击入口，让玩家能从页面跳转到相关页面、查看结构预览入口，并看到配方目标提示。

## Requirements

- 在 `guide` 包中新增指南动作数据模型，覆盖旧版 `recipe`、`link`、`image` 三类语义。
- `GuidePage` 可以携带动作列表；没有动作的页面保持当前行为不变。
- `GuideScreen` 在正文区域下方渲染动作列表，显示图标、标题和类型提示。
- 点击 link 动作时跳转到目标指南页面；若当前搜索会隐藏目标页面，跳转应清空搜索以保证页面可见。
- 点击 image 动作时打开结构预览视图；本阶段预览以结构条目列表/方块图标呈现，不实现旧版旋转 3D 渲染。
- 点击 recipe 动作时显示本地化提示，说明配方查看等待 JEI/EMI 集成；不能崩溃或静默无反馈。
- 至少迁移旧版高价值结构入口：`ironFreezer`、`combustion`、`lava`、`crystalSetup`、`end`、`end2`、`infuser`。
- 更新指南文本中明确说“旧链接和图片稍后返回”的占位说明。
- 新增用户可见文本必须写入 `en_us.json`。

## Acceptance Criteria

- [ ] 指南页面可以声明 0 个或多个动作。
- [ ] 页面有动作时，客户端指南显示动作区；无动作页面仍只显示正文。
- [ ] link 动作能跳到目标页面，并同步分类、页码和搜索状态。
- [ ] image 动作能打开结构预览，显示结构标题、条目数量和可见方块/坐标行。
- [ ] recipe 动作能显示“配方查看待集成”的本地化反馈。
- [ ] 缺失目标页面或结构时不抛异常，并显示本地化反馈。
- [ ] `./gradlew.bat compileJava`、`./gradlew.bat runData`、`./gradlew.bat build`、`./gradlew.bat runGameTestServer` 通过。
- [ ] `git diff --check` 与 `git diff --cached --check` 通过。

## Definition of Done

- 代码保持 common/client 边界清晰：指南数据可以在 common 包声明，渲染和点击反馈只在 client screen 内处理。
- 不引入新依赖，不接入 JEI/EMI，不实现完整 3D 结构渲染。
- Trellis 任务、阶段清单、实现和归档均使用中文 commit 记录。

## Technical Approach

- 新增 `GuideAction` record 表达动作类型、目标 id、可选标签 key 和图标 supplier。
- 新增 `GuideStructure` / `GuideStructures` 作为旧版 `ModGuidePages.imageDesigns` 的轻量数据入口。
- 扩展 `GuidePage` record，保留原构造器并新增动作列表，降低对现有页面定义的改动成本。
- 在 `GuideScreen` 的正文底部渲染最多若干行动作；鼠标悬停显示类型提示，点击按类型分派。
- 结构预览先显示列表而非 3D 渲染，为后续专门的结构渲染阶段留下明确边界。

## Decision (ADR-lite)

**Context**: 旧版内联按钮依赖 1.12 GUI 和 JEI 插件；当前项目没有 recipe viewer 集成，也没有 3D 结构渲染契约。

**Decision**: 本阶段先迁移富文本语义为页面动作模型，并实现可用的 link 跳转、image 结构摘要、recipe 待集成反馈。

**Consequences**: 玩家会重新获得指南内导航和结构信息入口；配方入口不是最终 JEI/EMI 行为，后续联动阶段需要把 recipe 动作接到选定的配方查看器。

## Out of Scope

- 不解析旧版 lang 字符串中的 `<recipe>/<link>/<image>` 标记。
- 不迁移所有旧版内联按钮到精确原文位置。
- 不接入 JEI/EMI/REI。
- 不实现旋转 3D 结构渲染或滚动结构列表。
- 不新增服务端网络包或持久化状态。

## Technical Notes

- 旧版按钮来源：
  - `D:/workspace/minecraft/mods/4Github/SkyResources/src/main/java/com/bartz24/skyresources/base/guide/GuideRecipeButton.java`
  - `D:/workspace/minecraft/mods/4Github/SkyResources/src/main/java/com/bartz24/skyresources/base/guide/GuideLinkPageButton.java`
  - `D:/workspace/minecraft/mods/4Github/SkyResources/src/main/java/com/bartz24/skyresources/base/guide/GuideImageButton.java`
  - `D:/workspace/minecraft/mods/4Github/SkyResources/src/main/java/com/bartz24/skyresources/registry/ModGuidePages.java`
- 目标页面数据：
  - `src/main/java/committee/nova/mods/skyresources3/guide/GuidePage.java`
  - `src/main/java/committee/nova/mods/skyresources3/guide/GuidePages.java`
- 目标客户端：
  - `src/main/java/committee/nova/mods/skyresources3/client/GuideScreen.java`
- 本地 API 探针确认 `GuiGraphics` 支持 `setTooltipForNextFrame(Font, Component, int, int)`。
