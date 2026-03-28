# 6YIX-android

六爻排盘 Android 端（Compose + Kotlin）。

## 快速开始（运行）
1. 用 Android Studio 打开项目根目录
2. 等待 Gradle Sync 完成
3. 选择运行配置 `app`，点击 Run

## 文档
- 架构与分工：`docs/ARCHITECTURE.md`

## 协作流程（强制）
### 分支策略
- `main`：稳定分支，**禁止直接 push**
- 每个需求/任务都新建分支开发，例如：
    - `feature/engine-core`
    - `feature/ui-home`
    - `fix/xxx`

### 提交流程（必须走 PR）
1. 从 `main` 拉分支开发
2. push 到自己的分支
3. 在 GitHub 上发起 Pull Request（目标分支：`main`）
4. 至少 1 人 Review 通过后再合并（建议启用 GitHub 分支保护）

### Commit 约定（建议）
- 一次 commit 只做一件事
- 提交信息尽量清晰，例如：
    - `Add engine result model`
    - `Implement line type mapping`
    - `Add home screen layout`

## 目录约定（当前）
- `com.sixyix.android.ui`：Compose UI、导航、ViewModel（不要放排盘核心逻辑）
- `com.sixyix.android.domain`：纯 Kotlin 数据模型/规则（无 Android 依赖）
- `com.sixyix.android.engine`（待建）：排盘核心算法（纯 Kotlin + 单测）
- `com.sixyix.android.shake`（待建）：摇一摇传感器/节流（只发事件给 UI）
- `com.sixyix.android.data`（待建）：网络/AI/持久化（DTO、Mapper、重试等）

## 测试
- engine 必须写单元测试：`app/src/test`
- domain 尽量保持 Android-free，保证测试快速稳定
