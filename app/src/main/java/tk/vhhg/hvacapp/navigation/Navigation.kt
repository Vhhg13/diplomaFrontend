package tk.vhhg.hvacapp.navigation

import android.os.Build
import androidx.annotation.StringRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import tk.vhhg.device.DeviceScreen
import tk.vhhg.hvacapp.R
import tk.vhhg.hvacapp.navigation.Screen.ImSettingsRoute
import tk.vhhg.hvacapp.navigation.Screen.LogsRoute
import tk.vhhg.hvacapp.navigation.Screen.RoomsRoute
import tk.vhhg.hvacapp.navigation.Screen.SpecificRoomRoute
import tk.vhhg.im.ui.ImScreen
import tk.vhhg.rooms.RoomDialogEnum
import tk.vhhg.rooms.RoomsScreen
import tk.vhhg.specific_room.SpecificRoomScreen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Navigation(logout: () -> Unit, enableNotifications: () -> Unit, modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val surfaceColor = MaterialTheme.colorScheme.surface
    var scaffoldColor by remember { mutableStateOf(surfaceColor) }
    val textColor by remember { derivedStateOf { getTextColor(scaffoldColor) } }
    val textColorAnimated by animateColorAsState(textColor, animationSpec = tween(1000))
    val scaffoldColorAnimated by animateColorAsState(scaffoldColor, animationSpec = tween(1000))

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    if (!navBackStackEntry.isRoute(RoomsRoute)) scaffoldColor = surfaceColor

    val roomDialog = remember { mutableStateOf<RoomDialogEnum?>(null) }

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
                    icon = { Icon(Icons.Outlined.Home, null) },
                    label = navLabel(R.string.rooms),
                    selected = navBackStackEntry.isRoute(RoomsRoute),
                    onClick = {
                        navController.popBackStack()
                        navController.navigate(RoomsRoute) {
                            launchSingleTop = true
                        }
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.AutoMirrored.Outlined.List,null) },
                    label = navLabel(R.string.nav_logs),
                    selected = navBackStackEntry.isRoute(LogsRoute),
                    onClick = {
                        navController.popBackStack()
                        navController.navigate(LogsRoute) {
                            launchSingleTop = true
                        }
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Outlined.PlayArrow, null) },
                    label = navLabel(R.string.nav_im),
                    selected = navBackStackEntry.isRoute(ImSettingsRoute),
                    onClick = {
                        navController.popBackStack()
                        navController.navigate(ImSettingsRoute) {
                            launchSingleTop = true
                        }
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Outlined.Lock, null) },
                    label = navLabel(R.string.nav_leave),
                    selected = false,
                    onClick = logout
                )
                val apiVersion = remember { Build.VERSION.SDK_INT }
                if (apiVersion >= Build.VERSION_CODES.TIRAMISU) {
                    HorizontalDivider()
                    Text(
                        stringResource(R.string.settings), Modifier.padding(16.dp),
                        style = MaterialTheme.typography.labelLarge
                    )
                    NavigationDrawerItem(
                        label = navLabel(R.string.notifications),
                        selected = false,
                        onClick = enableNotifications
                    )
                }
            }
        }) {
        Scaffold(
            containerColor = if (scaffoldColor == surfaceColor) surfaceColor else scaffoldColorAnimated,
            floatingActionButton = {
                if (navBackStackEntry.isRoute(RoomsRoute))
                FloatingActionButton({ roomDialog.value = RoomDialogEnum.ADD }) {
                    Icon(Icons.Default.Add, "Add new room")
                }
            },
            topBar = {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors().copy(containerColor = Color.Transparent, titleContentColor = textColorAnimated),
                    title = { ScreenTitle(navBackStackEntry) },
                    navigationIcon = {
                        if (navController.previousBackStackEntry != null) {
                            IconButton(onClick = { navController.navigateUp() }) {
                                Icon(Icons.Default.ArrowBack, "Navigate back", tint = textColorAnimated)
                            }
                        } else {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, "Show NavDrawer", tint = textColorAnimated)
                            }
                        }
                    },
                    actions = {
                        if (navBackStackEntry.isRoute(RoomsRoute)) {
                            IconButton({ roomDialog.value = RoomDialogEnum.EDIT }) {
                                Icon(Icons.Default.Edit, "Edit Room", tint = textColorAnimated)
                            }
                        }
                    })
            }) {
            NavHost(
                modifier = Modifier.padding(it),
                navController = navController,
                startDestination = RoomsRoute
            ) {
                composable<RoomsRoute> {
                    RoomsScreen(
                        roomDialog,
                        textColor = textColorAnimated,
                        navigateToRoom = { id, name -> navController.navigate(SpecificRoomRoute(id, name)) },
                        onChangeColor = { color -> scaffoldColor = color }
                    )
                }
                composable<LogsRoute> { }
                composable<ImSettingsRoute> { ImScreen() }
                composable<SpecificRoomRoute> { specificRoomRoute ->
                    val route = specificRoomRoute.toRoute<SpecificRoomRoute>()
                    val roomId = route.id
                    val roomName = route.name
                    SpecificRoomScreen(roomId, navigateToDevice = { deviceId ->
                        navController.navigate(Screen.DeviceRoute(deviceId, roomId, roomName))
                    })
                }
                composable<Screen.DeviceRoute> { deviceScreen ->
                    val route = deviceScreen.toRoute<Screen.DeviceRoute>()
                    DeviceScreen(route.deviceId, route.roomId, route.roomName, { navController.navigateUp() })
                }
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
private fun ScreenTitle(entry: NavBackStackEntry?, modifier: Modifier = Modifier) {
    Text(
        when {
            entry?.destination?.hasRoute(RoomsRoute::class) == true -> stringResource(R.string.rooms)
            entry?.destination?.hasRoute(LogsRoute::class) == true -> stringResource(R.string.nav_logs)
            entry?.destination?.hasRoute(ImSettingsRoute::class) == true -> stringResource(R.string.nav_im)
            entry?.destination?.hasRoute(SpecificRoomRoute::class) == true -> entry.toRoute<SpecificRoomRoute>().name
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

    @Serializable
    data class SpecificRoomRoute(val id: Long, val name: String) : Screen

    @Serializable
    data class DeviceRoute(val deviceId: Long, val roomId: Long, val roomName: String) : Screen
}

private fun getTextColor(backgroundColor: Color): Color {
    val argb = backgroundColor.toArgb()
    val red = (argb shr 16 and 0xFF) / 255.0
    val green = (argb shr 8 and 0xFF) / 255.0
    val blue = (argb and 0xFF) / 255.0
    val luminance = 0.2126 * red + 0.7152 * green + 0.0722 * blue
    return if (luminance > 0.5) Color(0xFF000000) else Color(0xFFFFFFFF)
}