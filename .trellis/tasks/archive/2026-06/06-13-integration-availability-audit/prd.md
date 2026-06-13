# 审计联动模组可用性

## 背景

`plans/migration-plan.md` 的阶段 9 要求迁移旧版 `plugin` 包内的联动功能；若 Minecraft 1.21.11 + NeoForge 没有合适版本或公开 API，则必须记录 TODO、阻塞原因和候选替代方案。

当前目标不是实现 JEI/EMI/REI、Jade/WTHIT、CraftTweaker 或其他生态联动，而是先建立可追踪的可用性审计，避免后续盲目新增依赖。

## 范围

- 盘点旧版 SkyResources 的主要联动入口和对应目标生态。
- 使用可验证来源确认目标模组是否存在 Minecraft 1.21.11 + NeoForge 可用版本。
- 记录可确认的依赖坐标、发布来源、状态和后续 TODO。
- 将审计结果沉淀到 `plans/integration-availability.md`。
- 更新迁移计划中阶段 9 的状态引用。

## 非目标

- 不新增 Gradle 依赖。
- 不实现 recipe viewer、探针、脚本或其他运行时代码。
- 不修改现有注册表、配方、GUI 或资源生成逻辑。
- 不把无法双源确认的信息写成已完成迁移。

## 验收标准

- `plans/integration-availability.md` 包含每个旧联动族的目标候选、1.21.11 NeoForge 证据、状态和 TODO。
- Trellis 任务下有研究记录，保留来源 URL 和查询限制。
- `plans/migration-plan.md` 阶段 9 指向审计文档，并说明当前阶段只完成可用性审计。
- 文档不声明未实现的代码能力。
- `git diff --check` 和 `git diff --cached --check` 通过。
