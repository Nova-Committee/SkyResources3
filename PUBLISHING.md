# 发布到 Modrinth 与 CurseForge

本分支通过 GitHub Actions 的“发布到 Modrinth 和 CurseForge”工作流，将同一个 Gradle 主 JAR 手动发布到 Modrinth 项目 `RgaJkHX3` 与 CurseForge 项目 `1575615`。工作流不会由 push、标签或 pull request 自动触发，也不会创建 GitHub Release。

## 配置仓库 Secrets

在 GitHub 仓库的 **Settings → Secrets and variables → Actions → Repository secrets** 中配置：

- `MODRINTH_TOKEN`：具备向 Modrinth 项目 `RgaJkHX3` 创建版本权限的令牌。
- `CURSEFORGE_TOKEN`：具备向 CurseForge 项目 `1575615` 上传文件权限的 API 令牌。

令牌不得写入 `gradle.properties`、工作流、提交、日志或 changelog。工作流只从上述两个 Repository Secrets 读取平台令牌，并且不会把令牌输出到日志。

## 准备版本

发布前在 `gradle.properties` 中确认以下属性，并将变更提交、推送到准备发布的分支：

- `minecraft_version`：发布页面使用的 Minecraft 版本；本分支应为 `1.21.11`。
- `mod_loader`：发布页面使用的加载器；本分支应为 `neoforge`。
- `mod_name`：发布名称和主 JAR 文件名前缀。
- `mod_version`：模组版本，例如 `1.0.0`。
- `mod_version_tag`：版本标签，例如 `release`、`beta` 或 `alpha`。

发布版本严格复用 `build.gradle` 的公式：

```text
<minecraft_version>-<mod_version>-<mod_version_tag>
```

例如当前属性会生成版本 `1.21.11-1.0.0-release`，主 JAR 应为 `build/libs/SkyResources3-neoforge-1.21.11-1.0.0-release.jar`。手动输入的 `release_type` 决定平台上的 release/beta/alpha 分类，不会改写 `gradle.properties` 或版本号；发布前应确保它与版本计划一致。

## 手动运行

1. 打开仓库的 **Actions** 页面，选择“发布到 Modrinth 和 CurseForge”。
2. 点击 **Run workflow**，选择要发布的分支；发布本版本时选择 `neo/1.21.11`。
3. 在 `release_type` 中选择 `release`、`beta` 或 `alpha`。
4. 在 `changelog` 中填写本次版本的更新说明。不要在其中填写令牌或其他敏感信息。
5. 再次核对分支、版本类型和 changelog 后运行工作流。

工作流使用 Java 21 执行 `./gradlew build --no-daemon`，随后从 `gradle.properties` 读取发布元数据。只有当 `build/libs` 中恰好存在一个排除 sources、javadoc 和 dev 分类后的主 JAR，且其文件名与 Gradle 版本公式一致时，才会进入上传步骤。Modrinth 与 CurseForge 接收的是同一个已验证 JAR。

## 失败行为

- Gradle 构建失败时，元数据检查与上传步骤不会运行。
- 必需属性缺失、为空或重复定义时，工作流会在上传前失败。
- 没有主 JAR、存在多个可发布主 JAR，或 JAR 文件名与版本公式不一致时，工作流会在上传前失败，并在日志中列出检测到的 JAR 路径。
- 任一 Repository Secret 缺失时，工作流会在调用发布 Action 前失败。
- `mc-publish` 使用 `fail-mode: fail`；任一平台返回错误都会使工作流失败。两个外部平台无法提供跨平台事务保证，因此一个平台成功而另一个失败时可能出现部分发布。重试前先检查两个平台的版本状态，避免创建重复版本或重复文件。
- 并发发布会排队而不会取消正在运行的发布，防止两个手动任务同时上传。

## 安全说明

- 工作流唯一触发器是 `workflow_dispatch`，不响应 push、标签或 pull request。
- `GITHUB_TOKEN` 权限限制为 `contents: read`，检出步骤不持久化凭据。
- `checkout`、`setup-java`、`setup-gradle` 与 `mc-publish` 均固定到完整提交 SHA；升级前必须重新核验上游提交。
- 工作流没有向 `mc-publish` 提供 `github-token` 或 `github-tag`，因此不会创建或修改 GitHub Release。
- 不要在本地验证中使用真实平台令牌；本任务的静态检查和 Gradle 构建不会执行真实发布。
