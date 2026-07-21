# 发布到 Modrinth 与 CurseForge

本分支通过 GitHub Actions 手动构建一次，并将同一个主 JAR 发布到以下项目：

- Modrinth：`RgaJkHX3`
- CurseForge：`1575615`

发布工作流只接受 `workflow_dispatch`，不会响应 push、标签或 pull request，也不会创建 GitHub Release。

## 配置发布令牌

在 GitHub 仓库的 **Settings → Secrets and variables → Actions** 中配置两个 Repository secret：

| Secret | 用途 |
| --- | --- |
| `MODRINTH_TOKEN` | 向 Modrinth 项目上传版本 |
| `CURSEFORGE_TOKEN` | 向 CurseForge 项目上传文件 |

令牌应只授予发布所需的最小权限。不要把令牌写入 `gradle.properties`、工作流、提交、Issue、PR 或日志，也不要使用普通 Actions variable 代替 secret。工作流仅通过 `secrets.MODRINTH_TOKEN` 与 `secrets.CURSEFORGE_TOKEN` 读取平台令牌。

## 准备版本

发布元数据来自当前分支的 `gradle.properties`：

- `minecraft_version`：Minecraft 版本；
- `mod_loader`：加载器，本分支为 Forge；
- `mod_name`：平台显示名称；
- `mod_version` 与 `mod_version_tag`：模组版本及版本后缀。

发布版本号与 `build.gradle` 中的 `project.version` 公式保持一致：

```text
${minecraft_version}-${mod_version}-${mod_version_tag}
```

发布前更新并检查 `mod_version` 和 `mod_version_tag`，确认 `minecraft_version`、`mod_loader` 与目标分支一致，然后提交并推送这些版本变更。建议先在本地运行：

```powershell
./gradlew.bat build --no-daemon
```

该命令只执行本地构建，不会上传任何文件。

## 手动运行

1. 打开仓库的 **Actions** 页面，选择“发布到 Modrinth 与 CurseForge”。
2. 点击 **Run workflow**，选择要发布的 `forge/1.20.1` 分支。
3. 选择 `release`、`beta` 或 `alpha`。
4. 填写本次发布的 changelog；内容应准确说明面向用户的变更。
5. 再次核对分支、版本和发布类型，然后启动工作流。

工作流依次配置 Java 17、执行 `./gradlew build --no-daemon`、读取发布元数据，并检查 `build/libs`。只有该目录中恰好存在一个排除 `sources`、`javadoc` 和 `dev` 分类器后的主 JAR，且文件名严格符合 `${mod_name}-${mod_loader}-${minecraft_version}-${mod_version}-${mod_version_tag}.jar` 时，发布步骤才会运行；这个唯一 JAR 会同时传给 Modrinth 和 CurseForge。

## 失败行为

- Gradle 构建失败时，元数据校验与上传均不会运行。
- 必需属性缺失、为空或重复定义时，工作流失败且不会上传。
- `build/libs` 不存在、主 JAR 数量不是一个或文件名不符合 Gradle 命名公式时，工作流失败且不会上传。
- 任一 Secret 缺失或为空时，上传前检查会失败，两个平台均不会收到文件。
- Secret 过期或权限不足时，发布 Action 会失败。
- 任一平台返回错误时，`fail-mode: fail` 会使工作流失败。

两个外部平台不提供跨平台事务：如果其中一端已经成功、另一端随后失败，成功的一端不会自动回滚。重新运行前应分别检查两个平台的版本状态；不要盲目重试相同版本，以免产生重复版本或再次失败。

## 安全注意事项

- 工作流权限限制为 `contents: read`，检出后不会持久化 GitHub 凭据。
- 所有外部 GitHub Actions 都固定到完整提交 SHA；更新 Action 时应先核验来源和目标提交。
- 固定并发组保证同一仓库不会同时运行两个平台发布任务，且正在发布的任务不会被新任务取消。
- 不要在本地命令、调试输出或截图中打印 Secret。GitHub 的日志遮罩不能替代正确的凭据处理。
- 发布属于不可自动回滚的外部写入。只有在确认构建产物、版本号、changelog 和两个平台状态后，才应手动启动。
