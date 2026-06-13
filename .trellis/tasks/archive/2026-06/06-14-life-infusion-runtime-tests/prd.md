# 补充生命灌注运行时回归测试

## 背景

生命灌注已经迁移到 `ProcessRecipes.INFUSION`，手持灌注石和生命灌注器共用 `InfusionRecipes` 查表。当前已有配方、JEI 分类和向导动作，但缺少覆盖这两条运行时入口的 GameTest，迁移清单中也保留了部分已经过期的 JEI/向导待办。

## 目标

- 为手持灌注石添加 GameTest，验证配方输入、目标方块、输出掉落、目标清除、玩家生命消耗和工具耐久损耗。
- 为生命灌注器添加 GameTest，验证多方块结构、生命宝石消耗、输入消耗、目标清除和输出掉落。
- 注册新增 GameTest，沿用现有 `ModGameTests` 函数注册模式。
- 同步相关迁移计划，标记已经实现的 JEI 灌注分类/向导动作和新增回归覆盖。

## 非目标

- 不新增动态 ore-dictionary/tag 兼容配方。
- 不修改生命灌注配方语义或玩家交互规则。
- 不调整 JEI API 依赖或新增可选集成。

## 验收标准

- `./gradlew.bat compileJava` 通过。
- `./gradlew.bat runGameTestServer` 通过。
- `./gradlew.bat build` 通过。
- `git diff --check` 通过。
- `scripts/check-serena-java.ps1` 通过。
