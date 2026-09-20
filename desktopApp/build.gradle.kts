import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

dependencies {
    implementation(projects.shared)

    implementation(compose.desktop.currentOs)
    implementation(libs.kotlinx.coroutinesSwing)

    implementation(libs.compose.uiToolingPreview)
}

compose.desktop {
    application {
        mainClass = "com.rostykstets.fembodydeformer.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "FEM Body Deformer"
            packageVersion = "1.0.0"

            macOS {
                bundleID = "com.rostykstets.fembodydeformer"
                iconFile.set(
                    project.file("src/main/resources/icon.icns")
                )
            }

            windows {
                iconFile.set(
                    project.file("src/main/resources/icon.ico")
                )
            }

            linux {
                iconFile.set(
                    project.file("src/main/resources/icon.png")
                )
            }
        }
    }
}