# 联动模组可用性研究记录

日期：2026-06-13
目标环境：Minecraft 1.21.11 + NeoForge 21.11.42

## 查询方法

- 旧源码盘点：
  - `D:/workspace/minecraft/mods/4Github/SkyResources/src/main/java/com/bartz24/skyresources/jei/**`
  - `D:/workspace/minecraft/mods/4Github/SkyResources/src/main/java/com/bartz24/skyresources/plugin/**`
  - `ModPlugins.java` 注册旧联动：Actually Additions、AE2、ArmorPlus、CraftTweaker、Draconic Evolution、Embers、Extra Bees、Extreme Reactors、Forestry、IC2、Integrated Dynamics、Rock Candy、Tech Reborn、TConstruct、The One Probe、Thermal Foundation、VoidIslandControl。
- 主要版本证据：
  - Modrinth API 精确过滤：`https://api.modrinth.com/v2/project/{slug}/version?game_versions=%5B%221.21.11%22%5D&loaders=%5B%22neoforge%22%5D`
  - Modrinth API slug 搜索：`https://api.modrinth.com/v2/search?query={query}&limit=5&facets=%5B%5B%22project_type%3Amod%22%5D%5D`
  - Modrinth Maven 说明：`https://support.modrinth.com/en/articles/8801191-modrinth-maven`
  - Modrinth Maven POM HEAD 探针：`https://api.modrinth.com/maven/maven/modrinth/{slug}/{version}/{slug}-{version}.pom`
- 补充网页搜索：
  - Grok 搜索返回空正文，仅返回少量来源；未作为主证据。
  - MiniMax 搜索结果噪声较高，仅用于确认需要依赖源站 API。
  - Web 搜索命中 CurseForge/官方页面片段时，只作为“未命中”的辅助信号，不覆盖 Modrinth 精确过滤结果。

## 精确命中：1.21.11 + NeoForge

| slug | 项目 | 命中数 | 最新版本 | 版本页 | 候选坐标 | POM |
|------|------|--------|----------|--------|----------|-----|
| `jei` | Just Enough Items (JEI) | 14 | `27.4.0.22` | `https://modrinth.com/mod/jei/version/PtmKIfIA` | `maven.modrinth:jei:27.4.0.22` | 200 OK |
| `rei` | Roughly Enough Items (REI) | 1 | `21.11.814+neoforge` | `https://modrinth.com/mod/rei/version/PYNLOMi4` | `maven.modrinth:rei:21.11.814+neoforge` | 200 OK |
| `jade` | Jade | 5 | `21.1.7+neoforge` | `https://modrinth.com/mod/jade/version/LDFqgwEA` | `maven.modrinth:jade:21.1.7+neoforge` | 200 OK |
| `wthit` | WTHIT | 7 | `neo-18.2.2` | `https://modrinth.com/mod/wthit/version/9kLa6aJn` | `maven.modrinth:wthit:neo-18.2.2` | 200 OK |
| `integrated-dynamics` | Integrated Dynamics | 25 | `1.21.11-1.24.1-1692` | `https://modrinth.com/mod/integrated-dynamics/version/LyoRdjuN` | `maven.modrinth:integrated-dynamics:1.21.11-1.24.1-1692` | 200 OK |

置信度：高。限制：主要证据来自 Modrinth 及其 Maven，同属一个发布平台；后续真正接入 API 时仍需查各项目开发者文档或源码。

## 精确未命中或未确认

| slug/查询 | 项目 | Modrinth 结果 | 辅助信号 | 结论 |
|-----------|------|---------------|----------|------|
| `emi` | EMI | 0 个 1.21.11 + NeoForge 文件 | CurseForge 页面主文件片段为 `emi-1.1.24+1.21.1+neoforge` | 暂缓；JEI/REI 更适合当前目标版本 |
| `crafttweaker` | CraftTweaker | 0 个 1.21.11 + NeoForge 文件 | Modrinth 页面兼容范围显示到 1.21-1.21.1 | 暂缓 |
| `ae2` | Applied Energistics 2 | 0 个 1.21.11 + NeoForge 文件 | AE2 官方下载页/CurseForge 片段显示 26.1.2 或 1.21.1 路线 | 暂缓 |
| `the-one-probe` | The One Probe | 0 个 1.21.11 + NeoForge 文件 | CurseForge 主文件片段为 1.21.1 NeoForge | 暂缓，优先 Jade/WTHIT |
| `tinkers-construct` | Tinkers' Construct | 0 个 1.21.11 + NeoForge 文件 | 未找到精确替代 API | 暂缓 |
| `thermal-foundation` / `thermal-expansion` | Thermal 系列 | 0 个 1.21.11 + NeoForge 文件 | 未找到精确版本 | 暂缓 |
| `actually-additions` | Actually Additions | 0 个 1.21.11 + NeoForge 文件 | 未找到精确版本 | 暂缓 |
| `forestry` / `binnies-mods` | Forestry / Binnie's Mods | 0 个 1.21.11 + NeoForge 文件 | `extra-bees` slug 未找到可信项目 | 暂缓；养蜂生态待定 |
| `ic2classic` | IC2Classic | 0 个 1.21.11 + NeoForge 文件 | 搜索结果最高到 1.19.2 | 暂缓/放弃旧 IC2 直连 |
| `techreborn` | Tech Reborn | 0 个 1.21.11 + NeoForge 文件 | 项目偏 Fabric 生态 | 暂缓 |
| `draconic-evolution` | Draconic Evolution | 0 个 1.21.11 + NeoForge 文件 | 未找到精确版本 | 暂缓 |
| `embers` | Embers Rekindled | 0 个 1.21.11 + NeoForge 文件 | 搜索结果显示 1.20.1 | 暂缓 |
| `extreme-reactors` | Extreme Reactors | 0 个 1.21.11 + NeoForge 文件 | 未找到精确版本 | 暂缓 |
| `armorplus` | ArmorPlus | 0 个 1.21.11 + NeoForge 文件 | 未找到精确版本 | 暂缓 |
| `rock-candy` | Rock Candy | 0 个 1.21.11 + NeoForge 文件 | Modrinth slug 命中疑似非旧联动目标 | 暂缓；需人工确认项目身份 |
| `voidislandcontrol` | VoidIslandControl | 项目未找到 | 本迁移已要求内置功能而非外部依赖 | 不作为外部联动 |

置信度：中。限制：未命中只说明本次检查的 Modrinth/公开搜索没有确认 1.21.11 + NeoForge 文件，不等于所有发布渠道绝对不存在。

## 迁移建议

1. Recipe viewer 后续优先实现 JEI 集成；REI 可作为备选；EMI 暂缓。
2. 方块/机器信息探针不继续押注 The One Probe；后续优先评估 Jade 或 WTHIT。
3. Integrated Dynamics 有明确目标版本，但旧联动内容较小，应该在具体功能任务中先确认 API 是否仍覆盖旧行为。
4. CraftTweaker、Forestry/Binnie's、AE2、TConstruct、Thermal、IC2、Actually Additions 等保持 TODO，不新增依赖。
5. 后续任何联动实现都必须先验证开发者 API 文档或源码，再修改 `build.gradle`。
