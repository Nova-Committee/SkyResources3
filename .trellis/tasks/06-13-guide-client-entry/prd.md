# 迁移指南客户端入口

## 背景

Stage 55 已经把旧版 `SkyResourcesGuide` / `ModGuidePages` 的核心页迁移为 `GuidePage` 和 `GuidePages` 数据模型，并补充了英文翻译。当前缺口是玩家无法在客户端打开这些指南页。

本阶段只实现最小可用的客户端入口，不迁移旧版复杂富文本、配方按钮、图片结构设计或搜索系统。

## 目标

- 添加客户端指南屏幕，复用 `GuidePages` 作为唯一页面数据来源。
- 注册默认 `G` 键的客户端按键入口。
- 在屏幕中显示当前分类、页标题、图标和正文。
- 支持分类和页面前后切换。
- 保持客户端类与通用代码的 NeoForge 侧边界清晰。

## 非目标

- 不实现旧版 `<recipe>`、`<link>`、`<image>` 富文本语法。
- 不添加 Patchouli、JEI、EMI 或其他新依赖。
- 不新增服务端网络包。
- 不迁移 VoidIslandControl 专属指南页。
- 不做复杂视觉重构或可配置主题。

## 验收标准

- `Skyresources3Client` 或等价客户端入口注册指南按键。
- 按键触发逻辑只在客户端事件中运行，并打开指南屏幕。
- 指南屏幕能在无页面数据时安全显示空状态。
- 有页面数据时，默认选中第一页，并能在分类和页面间循环切换。
- 所有新增用户可见文本都有 `en_us.json` 翻译键。
- 新增或修改文件符合 `skyresources3` 命名空间和现有包结构。
- `./gradlew.bat compileJava`、`./gradlew.bat runData`、`./gradlew.bat build`、`./gradlew.bat runGameTestServer` 通过。

## 验证计划

1. 编译 Java，验证客户端 API、事件订阅和屏幕实现。
2. 运行数据生成，确认语言资源和生成资源不破坏。
3. 运行完整构建。
4. 运行 GameTest server，确认服务端路径不被客户端代码污染。
5. 使用 `git diff --check` 和 `git diff --cached --check` 检查空白问题。
