# 迁移指南搜索和目录

## 背景

Stage 55 已迁移核心指南页数据，Stage 56 已提供最小客户端指南入口。旧版 `GuideGUI` 还包含搜索框和页面目录：玩家可以输入关键字过滤当前分类下的页面，并从列表中直接打开目标页。当前 `GuideScreen` 只能按分类和页前后切换，浏览成本较高。

本阶段补齐搜索与目录浏览能力，保持当前原生客户端屏幕实现，不引入新依赖，不迁移旧版富文本按钮或图片渲染。

## 目标

- 在 `GuideScreen` 中添加搜索输入框。
- 根据搜索文本过滤当前分类下的指南页。
- 渲染当前筛选结果的页面目录，展示图标和标题。
- 支持点击目录项直接切换到对应指南页。
- 在搜索无结果时显示本地化空状态。
- 保持 Stage 56 的分类/页面翻页能力继续可用。

## 非目标

- 不实现跨全部分类搜索；本阶段只过滤当前分类，贴近旧版 `getPages(category, filter)` 的使用方式。
- 不迁移旧版 `<recipe>`、`<link>`、`<image>` 富文本语法。
- 不添加滚动列表框架；结果超出显示区域时只显示当前面板可容纳的前几项，完整分页/滚动后续处理。
- 不新增网络包或服务端状态。

## 验收标准

- 搜索框能接收键盘输入，输入变化后筛选结果立即刷新。
- 搜索匹配页标题和页面 id，大小写不敏感。
- 切换分类时保留搜索文本，但页面选择会落到当前筛选结果中的第一页。
- 搜索无结果时显示 `screen.skyresources3.guide.no_results` 文本，不抛异常。
- 目录项点击后切换到对应页，页计数与正文显示同步。
- 新增用户可见文本均在 `en_us.json` 中有翻译键。
- `./gradlew.bat compileJava`、`./gradlew.bat runData`、`./gradlew.bat build`、`./gradlew.bat runGameTestServer` 通过。

## 技术方案

- 使用 Minecraft 原生 `EditBox` 作为搜索输入控件。
- 在 `GuideScreen` 内计算当前分类和搜索文本下的 `visiblePages()`，用页面 id 与本地化标题匹配，避免新增持久缓存。
- 目录渲染继续使用 `GuiGraphics` 和现有原版按钮/点击事件，不新增 UI 框架。

## 技术说明

- 旧版行为来源：`D:/workspace/minecraft/mods/4Github/SkyResources/src/main/java/com/bartz24/skyresources/base/guide/SkyResourcesGuide.java`
- 旧版 GUI 来源：`D:/workspace/minecraft/mods/4Github/SkyResources/src/main/java/com/bartz24/skyresources/base/guide/gui/GuideGUI.java`
- 目标屏幕：`src/main/java/committee/nova/mods/skyresources3/client/GuideScreen.java`
- 目标数据：`src/main/java/committee/nova/mods/skyresources3/guide/GuidePages.java`
