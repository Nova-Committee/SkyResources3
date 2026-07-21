# 发布到 Modrinth 与 CurseForge

本分支通过 GitHub Actions 工作流 `.github/workflows/publish.yml`，将一次 Gradle 构建得到的同一个主 JAR 手动发布到以下项目：

- Modrinth：`RgaJkHX3`
- CurseForge：`1575615`

工作流只有 `workflow_dispatch` 手动入口，不响应分支推送、标签推送或拉取请求，也不会创建 GitHub Release。

## 配置发布凭据

在 GitHub 仓库的 **Settings → Secrets and variables → Actions** 中创建两个 Repository secrets：

- `MODRINTH_TOKEN`：有权向 Modrinth 项目 `RgaJkHX3` 创建版本并上传文件的令牌。
- `CURSEFORGE_TOKEN`：有权向 CurseForge 项目 `1575615` 上传文件的 API 令牌。

令牌名称必须完全一致。工作流只通过 `secrets.MODRINTH_TOKEN` 和 `secrets.CURSEFORGE_TOKEN` 将令牌传给固定版本的发布 Action，不从仓库文件、普通变量或手动输入读取令牌。

## 准备版本

发布提交必须先更新并检查 `gradle.properties`：

- `minecraft_version`：本分支目标 Minecraft 版本，当前为 `1.21.1`。
- `mod_loader`：发布平台加载器，当前为 `neoforge`。
- `mod_name`：发布名称和主 JAR 文件名前缀，当前为 `SkyResources3`。
- `mod_version`：模组版本，例如 `1.0.1`。
- `mod_version_tag`：版本后缀，例如 `release`、`beta` 或 `alpha`。

工作流与 `build.gradle` 使用同一版本公式：

```text
${minecraft_version}-${mod_version}-${mod_version_tag}
```

当前归档名规则为 `${mod_name}-${mod_loader}-${project.version}.jar`。提交版本变更前应在本地运行：

```powershell
./gradlew.bat build --no-daemon
```

确认构建成功，并且 `build/libs/` 中只有一个排除 `sources`、`javadoc` 和 `dev` 辅助包后的主 JAR。建议让 `mod_version_tag` 与手动选择的发布类型保持一致，避免版本号后缀和平台分类表达不同含义。

## 手动运行

1. 打开仓库的 **Actions** 页面，选择 **Publish to Modrinth and CurseForge**。
2. 选择 **Run workflow**，确认运行分支为要发布的 `neo/1.21.1` 提交。
3. 选择 `release_type`：`release`、`beta` 或 `alpha`。
4. 填写本次版本的 `changelog`；不要在更新日志中写入令牌或其他敏感信息。
5. 再次核对分支、版本属性和更新日志，然后启动工作流。

一次运行只允许一个发布任务执行；新的运行不会取消正在执行的发布任务。工作流不会修改版本、创建提交、推送标签或创建 GitHub Release。

## 失败行为

工作流按以下顺序快速失败：

1. 使用 Java 21 执行 `./gradlew build --no-daemon`。构建失败时不会调用发布 Action。
2. 从 `gradle.properties` 读取发布元数据。必需属性缺失、重复或为空时停止。
3. 严格筛选 `build/libs/`。主 JAR 不是恰好一个，或文件名与当前 Gradle 版本及归档公式不一致时停止。
4. 在发布 Action 启动前同时确认两个 Repository secrets 均非空；缺少任意一个时停止，避免只尝试单个平台。
5. 将已验证的同一个 JAR 交给 Modrinth 和 CurseForge。任一令牌无效、项目拒绝上传、网络请求失败，或者任一平台发布失败，整个任务都会失败。

双平台发布不是跨平台事务：一个平台成功后，另一个平台仍可能失败。遇到发布步骤失败时，先检查 Modrinth 和 CurseForge 是否已经出现该版本，再决定是否重试，避免重复版本或重复文件。

## 安全注意事项

- 只授予令牌发布对应项目所需的最小权限，并按平台策略定期轮换。
- 不要把令牌写入 `gradle.properties`、工作流、提交、日志、更新日志或截图。
- 仓库内所有第三方 Actions 均固定到完整提交 SHA；升级时应先核对上游源码和发布记录，再更新 SHA 注释。
- checkout 不持久化 GitHub 凭据；工作流权限固定为 `contents: read`，且未向发布 Action 提供 GitHub 令牌，因此不能创建 GitHub Release。
- 只从受信任、已审查的分支和提交手动运行发布；手动触发会真实上传文件，不能作为测试命令使用。
