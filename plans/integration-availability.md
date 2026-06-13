# 联动模组可用性矩阵

审计日期：2026-06-13
目标环境：Minecraft 1.21.11 + NeoForge 21.11.42
研究记录：`.trellis/tasks/06-13-integration-availability-audit/research/mod-compatibility.md`

## 判定规则

- “可后续实现”表示已找到 1.21.11 + NeoForge 发布文件，且候选 Maven POM 可访问；仍不代表本仓库已接入 API。
- “暂缓”表示未在本次精确条件下找到可用版本，或目标生态/API 需要进一步确认。
- “放弃”仅用于目标不再适合作为外部依赖的情况；当前多数旧联动先保持暂缓。
- Modrinth Maven 坐标只作为候选。真正实现时优先使用目标模组开发者推荐 Maven；若使用 Modrinth Maven，必须验证 Gradle 解析和缺失的传递依赖。

## 矩阵

| 旧联动范围 | 旧源码位置 | 1.21.11 目标候选 | 版本证据 | 候选坐标/来源 | 状态 | TODO |
|------------|------------|------------------|----------|---------------|------|------|
| JEI 配方展示与指南 recipe 按钮 | `jei/**`、`base/guide/GuideRecipeButton.java`、`recipe/ProcessRecipeManager.java` | JEI 27.4.0.22 | 官方 Maven 与 1.21.11 API 已验证；`runData` 日志确认发现 `skyresources3:jei` plugin | `mezz.jei:jei-1.21.11-common-api:27.4.0.22`、`mezz.jei:jei-1.21.11-neoforge-api:27.4.0.22`、`mezz.jei:jei-1.21.11-neoforge:27.4.0.22` | 已实现 | 已接入 Process/Crucible/Condenser/Heat Sources 分类和指南 recipe action；REI/EMI 暂不重复实现 |
| 探针信息显示 | `plugin/theoneprobe/**` | Jade 21.1.7+neoforge；WTHIT 备选 | The One Probe 精确过滤 0 个；Jade 1.21.11 NeoForge 文件和官方 1.21.6+ API 已验证；WTHIT `neo-18.2.2` 命中 7 个 | `maven.modrinth:jade:21.1.7+neoforge`；WTHIT 候选 `maven.modrinth:wthit:neo-18.2.2` | Jade 已实现 | 已接入热源有效性/热值、燃烧加热器热量、多方块状态提示；WTHIT 暂不重复实现 |
| CraftTweaker 脚本入口 | `plugin/ctweaker/**` | CraftTweaker | Modrinth 精确过滤 0 个；公开页面兼容范围未确认到 1.21.11 | 无已验证 1.21.11 NeoForge 坐标 | 暂缓 | 保留脚本 API TODO；待 CraftTweaker 提供 1.21.11 NeoForge 后再迁移 ZenCode 入口 |
| Forestry / Binnie's / Extra Bees 养蜂生态 | `plugin/forestry/**`、`plugin/extrabees/**` | Forestry、Binnie's Mods、Extra Bees 或替代养蜂模组 | Forestry/Binnie's 精确过滤 0 个；`extra-bees` 未找到可信项目 | 无已验证坐标 | 暂缓 | Bee Attractor 继续保留本模组玩法；外部蜂箱掉落联动等待生态稳定 |
| AE2 | `plugin/ae2/AE2Plugin.java` | Applied Energistics 2 | Modrinth slug `ae2` 精确过滤 0 个；官方/CurseForge 片段显示当前路线不等同 1.21.11 | 无已验证 1.21.11 NeoForge 坐标 | 暂缓 | 后续只在 AE2 发布 1.21.11 NeoForge 后接入特定物品/配方兼容 |
| Integrated Dynamics | `plugin/integdyn/IntegratedDynamicsPlugin.java` | Integrated Dynamics | `1.21.11-1.24.1-1692` 命中 25 个 NeoForge 文件 | `maven.modrinth:integrated-dynamics:1.21.11-1.24.1-1692`；POM 200 OK | 可后续实现 | 先确认当前 API 是否仍支持旧联动行为，再实现最小兼容 |
| TConstruct | `plugin/tconstruct/TConPlugin.java` | Tinkers' Construct | 精确过滤 0 个 | 无已验证坐标 | 暂缓 | 等待 1.21.11 NeoForge 或替代 API |
| Thermal 系列 | `plugin/thermal/ThermalPlugin.java` | Thermal Foundation / Thermal Expansion | 两者精确过滤均 0 个 | 无已验证坐标 | 暂缓 | 等待 Thermal 系列目标版本 |
| IC2 / Tech Reborn | `plugin/ic2/IC2Plugin.java`、`plugin/techreborn/TechRebornPlugin.java` | IC2Classic 或 Tech Reborn | 精确过滤均 0 个；IC2Classic 搜索结果最高到 1.19.2 | 无已验证坐标 | 暂缓 | 不直接移植旧 IC2 API；只保留配方/物品兼容 TODO |
| Actually Additions | `plugin/actuallyadditions/ActAddPlugin.java` | Actually Additions | 精确过滤 0 个 | 无已验证坐标 | 暂缓 | 等待目标版本 |
| Draconic Evolution | `plugin/dracevo/DEPlugin.java` | Draconic Evolution | 精确过滤 0 个 | 无已验证坐标 | 暂缓 | 等待目标版本 |
| Embers | `plugin/embers/EmbersPlugin.java` | Embers Rekindled | 精确过滤 0 个；搜索结果显示 1.20.1 路线 | 无已验证坐标 | 暂缓 | 等待目标版本 |
| Extreme Reactors | `plugin/extremereactors/ExtremeReactorsPlugin.java` | Extreme Reactors | 精确过滤 0 个 | 无已验证坐标 | 暂缓 | 等待目标版本 |
| ArmorPlus | `plugin/armorplus/ArmorPlusPlugin.java` | ArmorPlus | 精确过滤 0 个 | 无已验证坐标 | 暂缓 | 等待目标版本 |
| Rock Candy | `plugin/rockcandy/RockCandyPlugin.java` | Rock Candy | 精确过滤 0 个；Modrinth slug 命中疑似非旧目标 | 无已验证坐标 | 暂缓 | 先人工确认旧联动目标是否仍维护 |
| VoidIslandControl | `plugin/vic/VICPlugin.java` | 内置 SkyResources3 功能 | 未作为外部依赖审计；迁移要求已明确内置 | 本仓库源码 | 已内置化进行中 | 继续按岛屿/团队阶段推进，不恢复 1.12.2 jar 依赖 |

## 后续顺序

1. Integrated Dynamics 可在具体功能需要时单独开小任务。
2. WTHIT 仅在需要非 Jade 探针生态兼容时再评估，不和 Jade 同阶段重复实现。
3. 其他旧联动维持 TODO，直到有明确 1.21.11 NeoForge 版本和 API 证据。
