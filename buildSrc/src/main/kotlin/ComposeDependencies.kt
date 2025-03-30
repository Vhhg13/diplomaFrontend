import org.gradle.api.artifacts.dsl.DependencyHandler

fun DependencyHandler.compose() {
    add("implementation", "androidx.compose.ui:ui")
    add("implementation", "androidx.compose.ui:ui-graphics")
    add("implementation", "androidx.compose.ui:ui-tooling-preview")
    add("implementation", "androidx.compose.material3:material3")

    add("debugImplementation", "androidx.compose.ui:ui-tooling")
    add("debugImplementation", "androidx.compose.ui:ui-test-manifest")
}