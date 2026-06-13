# 迁移生命机器物品自动化能力

## Goal

补齐 `Life Injector` 和 `Life Infuser` 的物品自动化能力，让这两个已经具备运行时逻辑和菜单界面的生命机器可以通过 NeoForge item transfer capability 被漏斗、管道或相邻机器插入/抽出物品，推进 `plans/ask.md` 中机器、方块实体和自动化迁移目标。

## Requirements

- `LifeInjectorBlockEntity` 暴露一个健康宝石槽的 item transfer capability。
- `LifeInfuserBlockEntity` 暴露健康宝石槽和输入槽的 item transfer capability。
- 自动化插入规则必须与当前手动插入和菜单槽位规则一致：
  - Life Injector 只接受 `HealthGemItem`，每次最多持有 1 个。
  - Life Infuser 的宝石槽只接受 `HealthGemItem`，输入槽接受非空且非健康宝石的物品。
- 自动化抽出必须能取回内部物品，但不能破坏当前红石触发、配方消耗和方块破坏掉落逻辑。
- capability 注册必须沿用项目现有 `RegisterCapabilitiesEvent` / `ResourceHandler<ItemResource>` 模式。
- 为两台生命机器添加 GameTest 覆盖：
  - 自动化插入健康宝石到 Life Injector。
  - 自动化插入健康宝石和输入物到 Life Infuser。
  - 自动化抽出对应内部物品。
- 同步 Stage 16/17 checklist 中“物品 handler capability 自动化插入/抽出”遗留状态。

## Acceptance Criteria

- [x] `LifeInjectorBlockEntity` 可通过 item capability 插入并抽出健康宝石。
- [x] `LifeInjectorBlockEntity` 拒绝非健康宝石插入。
- [x] `LifeInfuserBlockEntity` 可通过 item capability 插入并抽出健康宝石和输入物。
- [x] `LifeInfuserBlockEntity` 拒绝健康宝石进入输入槽、拒绝普通物品进入宝石槽。
- [x] 现有菜单、手动交互和红石灌注运行时仍通过回归测试。
- [x] `./gradlew.bat compileJava` 通过。
- [x] `./gradlew.bat build` 通过。
- [x] `./gradlew.bat runGameTestServer` 通过。
- [x] `git diff --check` 与 `git diff --cached --check` 通过。

## Out of Scope

- 不新增能源、流体或复杂自动化网络。
- 不改变生命抽取、生命灌注配方、健康消耗或红石触发语义。
- 不新增 GUI 元素、网络包或专用纹理。
- 不处理外部模组特定管道 API；只暴露 NeoForge 标准 capability。

## Technical Approach

- 复用现有机器的 `ResourceHandler<ItemResource>` / `ItemStacksResourceHandler` 习惯，将生命机器槽位迁移为 handler 持有的单一物品状态。
- `getStackInSlot`、`setStackInSlot`、`removeStack`、`mayPlaceInSlot` 等菜单/手动交互合同继续包裹同一个 handler，避免菜单、手动交互和 capability 出现三套验证逻辑。
- 读取旧 `gem` / `input` NBT 键作为兼容兜底，新的保存格式使用 `items` child，与 1.21.11 机器库存规范保持一致。
- 在现有 capability 注册入口中为 `LIFE_INJECTOR` 和 `LIFE_INFUSER` 注册 `Capabilities.Item.BLOCK`。

## Decision (ADR-lite)

**Context**: 生命机器已经有直接字段、手动交互和菜单容器包装；但项目 1.21.11 机器库存规范要求 item automation 使用 `ItemStacksResourceHandler` 并通过 child 结构序列化。

**Decision**: 本任务将 Life Injector / Life Infuser 的槽位状态迁移到各自内部 `ItemStacksResourceHandler`，菜单、手动交互、运行时逻辑和 capability 全部通过同一个 handler 访问，并保留旧 NBT 读取兼容。

**Consequences**: 库存存储与现有 NeoForge 机器一致，自动化和菜单不会状态分叉；旧存档首次读取后会按新 `items` 格式保存。

## Technical Notes

- 当前可参考模式：`QuickDropperBlockEntity#getItemHandler`、`MachineCasingBlockEntity#getItemHandler`、`AqueousMachineBlockEntity`、`ModCapabilities`。
- 当前 GameTest 参考：`LifeInfusionGameTests`、`IslandCommandGameTests`、`ModGameTests`。
- Stage 16/17 已有菜单同步；本任务只清理 capability 自动化遗留项。
