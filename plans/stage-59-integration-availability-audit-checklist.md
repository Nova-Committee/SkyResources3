# Stage 59 - 联动模组可用性审计清单

## 目标

为旧版 `plugin` 包迁移建立 Minecraft 1.21.11 + NeoForge 可用性矩阵，明确哪些联动可后续实现、哪些需要暂缓或放弃。

## 清单

- [x] 创建 Trellis PRD，限定本阶段只做审计与计划更新。
- [x] 盘点旧版联动族：recipe viewer、探针、CraftTweaker、Forestry/Binnie's、AE2、TConstruct、Thermal、IC2、Actually Additions 等。
- [x] 查询可验证来源，优先使用 Modrinth/CurseForge/Maven/官方仓库或发布页。
- [x] 对每个候选记录：
  - 目标模组是否有 1.21.11 NeoForge 可用版本。
  - 已验证依赖坐标或发布来源。
  - 迁移状态：可后续实现、暂缓、放弃。
  - 暂缓/放弃原因和后续 TODO。
- [x] 写入 `plans/integration-availability.md`。
- [x] 更新 `plans/migration-plan.md` 阶段 9 和风险 TODO。
- [x] 运行文档验证命令。
- [x] 中文提交并归档 Trellis 任务；见提交 `6c2a205` 和 `9b4c9b4`。
