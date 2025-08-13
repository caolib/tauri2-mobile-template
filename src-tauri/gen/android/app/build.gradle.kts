import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("rust")
}

val tauriProperties = Properties().apply {
    val propFile = file("tauri.properties")
    if (propFile.exists()) {
        propFile.inputStream().use { load(it) }
    }
}

android {
    compileSdk = 36
    namespace = "io.github.caolib.tmt"
    defaultConfig {
        manifestPlaceholders["usesCleartextTraffic"] = "false"
        applicationId = "io.github.caolib.tmt"
        minSdk = 24
        targetSdk = 36
        versionCode = tauriProperties.getProperty("tauri.android.versionCode", "1").toInt()
        versionName = tauriProperties.getProperty("tauri.android.versionName", "1.0")
    }
    // ABI 分包策略：
    // 1. 如果存在 KEYSTORE_PASSWORD 环境变量（workflow 中设置），说明是 CI 构建，启用分包
    // 2. 或者手动设置 ENABLE_ABI_SPLITS=true 强制开启
    // 3. 本地开发（无 KEYSTORE_PASSWORD）不分包，避免 tauri android dev 找不到路径
    val hasKeystoreConfig = !System.getenv("KEYSTORE_PASSWORD").isNullOrBlank()
    val enableAbiSplits = hasKeystoreConfig || System.getenv("ENABLE_ABI_SPLITS") == "true"
    if (enableAbiSplits) {
        println("[build.gradle] ${if (hasKeystoreConfig) "CI 构建 (检测到 KEYSTORE_PASSWORD)" else "手动启用"}：启用 ABI 分包 (arm64-v8a, armeabi-v7a, x86_64 + universal)")
    } else {
        println("[build.gradle] 本地开发模式：禁用 ABI 分包 (单一 APK)")
    }
    splits {
        abi {
            isEnable = enableAbiSplits
            reset()
            include("arm64-v8a")
            isUniversalApk = true
        }
    }
    buildTypes {
        getByName("debug") {
            manifestPlaceholders["usesCleartextTraffic"] = "true"
            isDebuggable = true
            isJniDebuggable = true
            isMinifyEnabled = false
            packaging {                
                jniLibs.keepDebugSymbols.add("*/arm64-v8a/*.so")
                jniLibs.keepDebugSymbols.add("*/armeabi-v7a/*.so")
                jniLibs.keepDebugSymbols.add("*/x86/*.so")
                jniLibs.keepDebugSymbols.add("*/x86_64/*.so")
            }
        }
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                *fileTree(".") { include("**/*.pro") }
                    .plus(getDefaultProguardFile("proguard-android-optimize.txt"))
                    .toList().toTypedArray()
            )
            // 签名配置：依赖 GitHub Actions 注入的环境变量
            val envKeystoreFile = System.getenv("ANDROID_KEYSTORE_FILE")
            val envKeystorePass = System.getenv("KEYSTORE_PASSWORD") ?: System.getenv("KEYSTORE_PASS")
            val envKeyAlias = System.getenv("KEY_ALIAS") ?: "my-key-alias"
            val envKeyPass = System.getenv("KEY_PASSWORD") ?: envKeystorePass
            if (!envKeystoreFile.isNullOrBlank() && !envKeystorePass.isNullOrBlank()) {
                println("Using signing config with keystore: $envKeystoreFile")
                // 使用 maybeCreate 避免重复创建同名 signingConfig 抛异常
                val cfg = signingConfigs.maybeCreate("releaseAuto")
                cfg.storeFile = file(envKeystoreFile)
                cfg.storePassword = envKeystorePass
                cfg.keyAlias = envKeyAlias
                cfg.keyPassword = envKeyPass
                signingConfig = cfg
            } else {
                println("[WARN] Keystore info not fully provided (ANDROID_KEYSTORE_FILE / KEYSTORE_PASSWORD). Building unsigned release APK.")
            }
        }
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        buildConfig = true
    }
}

rust {
    rootDirRel = "../../../"
}

dependencies {
    implementation("androidx.webkit:webkit:1.14.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("androidx.activity:activity-ktx:1.10.1")
    implementation("com.google.android.material:material:1.12.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.4")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.0")
}

apply(from = "tauri.build.gradle.kts")