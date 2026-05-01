package edu.moravian.csci395.carman

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import carman.composeapp.generated.resources.Res
import carman.composeapp.generated.resources.tab_cars
import carman.composeapp.generated.resources.tab_home
import carman.composeapp.generated.resources.tab_map
import carman.composeapp.generated.resources.tab_settings
import edu.moravian.csci395.carman.data.CarManDatabase
import edu.moravian.csci395.carman.data.CarManSettings
import edu.moravian.csci395.carman.screens.AddCar
import edu.moravian.csci395.carman.screens.AddCarScreen
import edu.moravian.csci395.carman.screens.AddEvent
import edu.moravian.csci395.carman.screens.AddEventScreen
import edu.moravian.csci395.carman.screens.CarDetail
import edu.moravian.csci395.carman.screens.CarDetailScreen
import edu.moravian.csci395.carman.screens.Cars
import edu.moravian.csci395.carman.screens.CarsScreen
import edu.moravian.csci395.carman.screens.Home
import edu.moravian.csci395.carman.screens.HomeScreen
import edu.moravian.csci395.carman.screens.LogMileage
import edu.moravian.csci395.carman.screens.LogMileageScreen
import edu.moravian.csci395.carman.screens.MapScreen
import edu.moravian.csci395.carman.screens.MechanicsMap
import edu.moravian.csci395.carman.screens.Settings
import edu.moravian.csci395.carman.screens.SettingsScreen
import edu.moravian.csci395.carman.theme.AppTheme
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun App(database: CarManDatabase, settings: CarManSettings) {
    AppTheme {
        val navController = rememberNavController()
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination: NavDestination? = backStackEntry?.destination

        val isOnTab = TopLevelTabs.any { tab ->
            currentDestination?.hasRoute(tab.route::class) == true
        }

        Scaffold(
            bottomBar = {
                if (isOnTab) {
                    NavigationBar {
                        TopLevelTabs.forEach { tab ->
                            val selected = currentDestination?.hasRoute(tab.route::class) == true
                            NavigationBarItem(
                                selected = selected,
                                onClick = {
                                    navController.navigate(tab.route) {
                                        popUpTo(Home) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = {
                                    Icon(tab.icon, contentDescription = stringResource(tab.labelRes))
                                },
                                label = { Text(stringResource(tab.labelRes)) },
                            )
                        }
                    }
                }
            },
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Home,
                modifier = Modifier.padding(innerPadding),
            ) {
                composable<Home> {
                    HomeScreen(
                        carDao = database.carDao(),
                        eventDao = database.maintenanceEventDao(),
                        onEventClick = { id -> navController.navigate(CarDetail(id)) },
                    )
                }
                composable<Cars> {
                    CarsScreen(
                        carDao = database.carDao(),
                        onCarClick = { carId -> navController.navigate(CarDetail(carId)) },
                        onAddCarClick = { navController.navigate(AddCar) },
                    )
                }
                composable<MechanicsMap> { MapScreen() }
                composable<Settings> {
                    SettingsScreen(settings = settings)
                }
                composable<CarDetail> { entry ->
                    val route = entry.toRoute<CarDetail>()
                    CarDetailScreen(
                        carId = route.carId,
                        carDao = database.carDao(),
                        eventDao = database.maintenanceEventDao(),
                        onBack = { navController.popBackStack() },
                        onLogMileageClick = { id -> navController.navigate(LogMileage(id)) },
                        onAddEventClick = { id -> navController.navigate(AddEvent(id)) },
                    )
                }
                composable<AddCar> {
                    AddCarScreen(
                        carDao = database.carDao(),
                        onSaved = { navController.popBackStack() },
                        onCancel = { navController.popBackStack() },
                    )
                }
                composable<AddEvent> { entry ->
                    val route = entry.toRoute<AddEvent>()
                    AddEventScreen(
                        carId = route.carId,
                        carDao = database.carDao(),
                        eventDao = database.maintenanceEventDao(),
                        onSaved = { navController.popBackStack() },
                        onCancel = { navController.popBackStack() },
                    )
                }
                composable<LogMileage> { entry ->
                    val route = entry.toRoute<LogMileage>()
                    LogMileageScreen(
                        carId = route.carId,
                        carDao = database.carDao(),
                        onSaved = { navController.popBackStack() },
                        onCancel = { navController.popBackStack() },
                    )
                }
            }
        }
    }
}

private data class TopLevelTab(
    val route: Any,
    val labelRes: StringResource,
    val icon: ImageVector,
)

private val TopLevelTabs = listOf(
    TopLevelTab(Home, Res.string.tab_home, Icons.Default.Home),
    TopLevelTab(Cars, Res.string.tab_cars, Icons.Default.List),
    TopLevelTab(MechanicsMap, Res.string.tab_map, Icons.Default.LocationOn),
    TopLevelTab(Settings, Res.string.tab_settings, Icons.Default.Settings),
)