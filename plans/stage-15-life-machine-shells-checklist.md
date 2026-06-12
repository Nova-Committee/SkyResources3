# 阶段 15 实施清单：生命机器静态注册

> 对应总计划：`plans/migration-plan.md` 的“方块实体与机器逻辑”和“网络、菜单和 GUI”前置迁移。

## 目标

- 将旧版 `LifeInfuser` 迁移为 1.21.11 注册方块 `life_infuser`。
- 将旧版 `LifeInjector` 迁移为 1.21.11 注册方块 `life_injector`。
- 先接入方块、方块物品、创造模式标签、语言、模型、纹理、掉落和合成表。
- 不在本阶段伪造方块实体、菜单、Screen 或生命处理逻辑。

## 已迁移行为

- `life_infuser` 使用旧版木质机器的强度与爆炸抗性：6.0 / 12.0。
- `life_injector` 使用旧版木质机器的强度与爆炸抗性：6.0 / 12.0。
- 两个方块都使用木质声音并关闭遮挡，以匹配旧版非完整方块模型。
- 两个方块物品已加入 `SkyResources3` 创造模式标签。
- 两个方块均可掉落自身。

## 已迁移资源

- `assets/skyresources3/blockstates/life_infuser.json`
- `assets/skyresources3/blockstates/life_injector.json`
- `assets/skyresources3/items/life_infuser.json`
- `assets/skyresources3/items/life_injector.json`
- `assets/skyresources3/models/block/life_infuser.json`
- `assets/skyresources3/models/block/life_injector.json`
- `assets/skyresources3/textures/block/life_infuser.png`
- `assets/skyresources3/lang/en_us.json`

## 已迁移配方

- `life_infuser`：
  - 旧版配方 `XXX /  X  /  Y`
  - `X` 从旧 `logWood` 映射为 `minecraft:logs`
  - `Y` 为 `alchemical_infusion_stone`
- `life_injector`：
  - 旧版配方 ` Y  /  X  / XXX`
  - `X` 从旧 `logWood` 映射为 `minecraft:logs`
  - `Y` 为 `minecraft:diamond_sword`

## 暂缓迁移

- `LifeInfuser` 方块实体、红石脉冲触发、生命灌注配方执行和多方块校验留待机器运行时阶段。
- `LifeInjector` 方块实体、实体抽血、健康宝石注入和菜单同步留待机器运行时阶段。
- 旧 GUI/Menu 与客户端 Screen 留待网络与界面阶段。
- JEI 生命灌注分类联动留待联动阶段。

## 验证

- [x] `./gradlew.bat runData`
- [x] `./gradlew.bat build`
- [x] `./gradlew.bat runGameTestServer`
- [x] `git diff --check`
