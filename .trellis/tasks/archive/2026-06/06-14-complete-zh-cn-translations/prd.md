# 补全中文翻译键值

## 目标

为 SkyResources3 补齐简体中文语言资源，使 `zh_cn.json` 覆盖 `en_us.json` 的全部用户可见翻译键。

## 范围

- 以 `src/main/resources/assets/skyresources3/lang/en_us.json` 为英文基准。
- 新建或更新 `src/main/resources/assets/skyresources3/lang/zh_cn.json`。
- 保留所有翻译键名、JSON 顺序和格式的可读性。
- 将英文值翻译为简体中文；物品、方块、机器、JEI/Jade、指南、岛屿与队伍消息都纳入范围。
- 保留占位符和控制标记，例如 `%s`、`%s%%`、`{action:1}`、`HU`、`FE`、`mB`。

## 非目标

- 不修改英文翻译。
- 不更改注册名、资源路径、配方、GUI 逻辑或运行时行为。
- 不引入语言生成器、外部翻译依赖或新构建插件。

## 验收标准

- `zh_cn.json` 存在且 JSON 可解析。
- `zh_cn.json` 与 `en_us.json` 键集合完全一致。
- 每个对应值的占位符集合一致。
- `./gradlew.bat build` 通过，确认资源可被打包。
- `git diff --check` 通过；若出现 CRLF 提示，只记录为行尾噪声。
