package tk.vhhg.hvacapp.navigation

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import tk.vhhg.hvacapp.R
import tk.vhhg.hvacapp.navigation.Screen.ImSettingsRoute
import tk.vhhg.hvacapp.navigation.Screen.LogsRoute
import tk.vhhg.hvacapp.navigation.Screen.RoomsRoute
import tk.vhhg.im.ui.ImScreen
import tk.vhhg.knob.KnobPreview


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Navigation(logout: () -> Unit, modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    ModalNavigationDrawer(
        gesturesEnabled = false,
        drawerState = drawerState,
        modifier = modifier,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    stringResource(R.string.navigation),
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.labelLarge
                )

                NavigationDrawerItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Home,
                            contentDescription = null
                        )
                    },
                    label = navLabel(R.string.rooms),
                    selected = navBackStackEntry.isRoute(RoomsRoute),
                    onClick = {
                        navController.navigate(RoomsRoute)
                        scope.launch {
                            drawerState.close()
                        }
                    }
                )
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.List,
                            contentDescription = null
                        )
                    },
                    label = navLabel(R.string.nav_logs),
                    selected = navBackStackEntry.isRoute(LogsRoute),
                    onClick = {
                        navController.navigate(LogsRoute)
                        scope.launch {
                            drawerState.close()
                        }
                    }
                )
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.PlayArrow,
                            contentDescription = null
                        )
                    },
                    label = navLabel(R.string.nav_im),
                    selected = navBackStackEntry.isRoute(ImSettingsRoute),
                    onClick = {
                        navController.navigate(ImSettingsRoute)
                        scope.launch {
                            drawerState.close()
                        }
                    }
                )
                NavigationDrawerItem(
                    icon = { Icon(imageVector = Icons.Outlined.Lock, contentDescription = null) },
                    label = navLabel(R.string.nav_leave),
                    selected = false,
                    onClick = logout
                )
            }
        }) {
        Scaffold(topBar = {
            CenterAlignedTopAppBar(navigationIcon = {
                IconButton(onClick = {
                    scope.launch { drawerState.open() }
                }) {
                    Icon(imageVector = Icons.Default.Menu, contentDescription = "Show NavDrawer")
                }
            }, title = { ScreenTitle(navBackStackEntry?.destination) })
        }) {
            NavHost(
                modifier = Modifier.padding(it),
                navController = navController,
                startDestination = RoomsRoute
            ) {
                composable<RoomsRoute> { KnobPreview() }
                composable<LogsRoute> { Text("Logs") }
                composable<ImSettingsRoute> { ImScreen() }
            }
        }
    }
}

private fun <T> NavBackStackEntry?.isRoute(route: T) =
    this?.destination?.hierarchy?.any { it.hasRoute(route!!::class) } == true

@Composable
private fun navLabel(@StringRes id: Int): @Composable () -> Unit = {
    Text(stringResource(id), style = MaterialTheme.typography.labelLarge)
}

@Composable
private fun ScreenTitle(dest: NavDestination?, modifier: Modifier = Modifier) {
    Text(
        when {
            dest?.hasRoute(RoomsRoute::class) == true -> stringResource(R.string.rooms)
            dest?.hasRoute(LogsRoute::class) == true -> stringResource(R.string.nav_logs)
            dest?.hasRoute(ImSettingsRoute::class) == true -> stringResource(R.string.nav_im)
            else -> ""
        },
        modifier
    )
}

sealed interface Screen {
    @Serializable
    data object RoomsRoute : Screen

    @Serializable
    data object LogsRoute : Screen

    @Serializable
    data object ImSettingsRoute : Screen
}