# 迁移生命机器菜单界面

## Goal

补齐 `Life Injector` 和 `Life Infuser` 的 1.21.11 Menu/Screen 迁移，让这两个已经具备运行时逻辑的生命机器拥有可打开的容器界面、服务端槽位同步和客户端屏幕注册，推进 `plans/ask.md` 中 GUI/网络迁移目标。

## Requirements

- 为 `LifeInjectorBlockEntity` 暴露一个健康宝石槽，保留当前右键插入/空手取回行为。
- 为 `LifeInfuserBlockEntity` 暴露健康宝石槽和输入槽，保留当前红石触发灌注运行逻辑。
- 新增 `LifeInjectorMenu` 和 `LifeInfuserMenu`，支持玩家背包槽、shift-click 转移、服务端真实槽位和客户端占位槽。
- 新增 `LifeInjectorScreen` 和 `LifeInfuserScreen`，使用现有 `blank_inventory.png` 风格，不新增纹理资产。
- 在 `ModMenuTypes` 与客户端 `SkyResources3Client` 中注册两个菜单/屏幕。
- 方块右键空手时打开菜单；现有持物插入/空手取回语义只在玩家潜行时作为快捷操作保留，避免界面入口被取回逻辑吞掉。
- 同步 Stage 15/16/17 checklist 中 Menu/Screen deferred 状态。

## Acceptance Criteria

- [x] `LifeInjectorBlock` 空手普通右键打开菜单。
- [x] `LifeInfuserBlock` 空手普通右键打开菜单。
- [x] 潜行空手仍可取回已有内部物品，兼容原最小交互闭环。
- [x] 菜单槽位限制与 block entity 当前插入规则一致。
- [x] `./gradlew.bat compileJava` 通过。
- [x] `./gradlew.bat build` 通过。
- [x] `./gradlew.bat runGameTestServer` 通过。
- [x] `git diff --check` 与 `git diff --cached --check` 通过。

## Out of Scope

- 不新增物品 automation capability。
- 不改变生命抽取、生命灌注配方或红石触发语义。
- 不新增专用 GUI 纹理或复杂状态条。
- 不迁移旧版完整 GUI 视觉细节之外的网络包。

## Technical Notes

- 现有菜单参考：`QuickDropperMenu`、`CrucibleInserterMenu`、`MachineCasingMenu`。
- 现有屏幕参考：`QuickDropperScreen`、`MachineCasingScreen`。
- 两个生命机器当前直接持有 `ItemStack` 字段，需要用轻量 `Container` 包装到菜单槽位。
