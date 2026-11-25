# 关屏（CloseScreen）

只有一件事：**一键关闭屏幕**。可选：关屏同时切静音（勿扰）。

不做别的：没有网络、没有统计、没有后台常驻、没有第三方依赖。

## 装和用

1. 从 Actions 的构建产物下载 `app-debug.apk`，装到手机上。
2. 打开 App，按提示：
   - **① 开启无障碍服务**（设置 → 无障碍 → 已下载的服务 → 关屏）
   - **② 授予勿扰权限**（设置 → 通知 → 勿扰访问 → 关屏）；不想静音可以跳过
3. 使用方式：
   - **下拉快捷开关里加一格「关屏」**，随时点一下就锁屏（最方便）
   - 或者打开 App 点「立即关屏」

## 权限说明

| 权限 | 为什么 |
|---|---|
| 无障碍服务 | 唯一能"任意界面一键锁屏"的非 root 途径，调用的是系统的 `GLOBAL_ACTION_LOCK_SCREEN` |
| 勿扰访问 | 只在勾选"关屏时同时静音"时才有用，用来把系统切到勿扰 |
| 通知权限 | 可选，仅用于提示 |

无障碍服务里 `canRetrieveWindowContent=false`，也就是**不读屏幕内容**。

## 兼容性

- Android 8.0（API 26）以上可装。
- 锁屏动作需要 Android 9（API 28）以上；8.x 上只能提示、无法锁屏。
- 部分国产 ROM 会额外限制无障碍服务（需要在电池/自启动设置里放行），服务被杀后重新开启即可。

## 构建

全部走 GitHub Actions，本地不构建：

- 工作流：[`.github/workflows/build.yml`](.github/workflows/build.yml)
- 触发：push 到 `main`/`master`，或在 Actions 页手动 `workflow_dispatch`
- 产物：`closescreen-debug` artifact 里的 `app-debug.apk`

环境：JDK 17 + Android SDK 34 + Gradle 8.9 + AGP 8.5.2 + Kotlin 1.9.24。

## 目录

```
app/src/main/java/com/closescreen/
├── MainActivity.kt              界面：状态、按钮、静音开关
├── LockAccessibilityService.kt  无障碍服务，只为了拿 performGlobalAction
├── LockTileService.kt           下拉快捷开关
├── LockHelper.kt                锁屏调用与能力判断
└── SoundHelper.kt               勿扰静音
```
