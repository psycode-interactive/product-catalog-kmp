plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.serialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.room) apply false
    alias(libs.plugins.composeCompiler) apply false
}

subprojects {
    afterEvaluate {
        project.extensions.findByType(org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension::class.java)?.let {
            it.sourceSets.all {
                languageSettings.optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
                languageSettings.optIn("kotlinx.coroutines.FlowPreview")
                languageSettings.optIn("androidx.compose.material3.ExperimentalMaterial3Api")
                languageSettings.optIn("kotlinx.cinterop.ExperimentalForeignApi")
            }
        }
    }
}
