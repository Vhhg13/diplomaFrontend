import org.gradle.api.Plugin
import org.gradle.api.Project

class KotlinxJson : Plugin<Project> {
    companion object {
        const val KOTLINX_JSON = "1.8.0"
    }
    override fun apply(project: Project) {
        project.apply {
            plugin("org.jetbrains.kotlin.plugin.serialization")
        }
        project.dependencies.add("implementation", "org.jetbrains.kotlinx:kotlinx-serialization-json:$KOTLINX_JSON")
    }
}