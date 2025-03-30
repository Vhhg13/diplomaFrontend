import org.gradle.api.Plugin
import org.gradle.api.Project

class HiltPlugin: Plugin<Project> {
    companion object {
        const val HILT_VERSION = "2.51.1"
        const val HILT_NAV_COMPOSE = "1.2.0"
    }
    override fun apply(project: Project) {
        project.apply {
            plugin("com.google.devtools.ksp")
            plugin("com.google.dagger.hilt.android")
        }
        project.dependencies.add("implementation", "androidx.hilt:hilt-navigation-compose:$HILT_NAV_COMPOSE")
        project.dependencies.add("implementation", "com.google.dagger:hilt-android:$HILT_VERSION")
        project.dependencies.add("ksp", "com.google.dagger:hilt-android-compiler:$HILT_VERSION")
    }
}