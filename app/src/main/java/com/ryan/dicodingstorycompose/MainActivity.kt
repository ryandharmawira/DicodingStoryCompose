package com.ryan.dicodingstorycompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import dagger.hilt.android.AndroidEntryPoint
import com.ryan.dicodingstorycompose.common.getBackStackResult
import com.ryan.dicodingstorycompose.common.setBackStackResult
import com.ryan.dicodingstorycompose.data.session.Session
import com.ryan.dicodingstorycompose.navigation.NavigationDrawerFooter
import com.ryan.dicodingstorycompose.navigation.NavigationDrawerHeader
import com.ryan.dicodingstorycompose.navigation.appDrawerItems
import com.ryan.dicodingstorycompose.presentation.about.AboutScreen
import com.ryan.dicodingstorycompose.presentation.add_story.AddStoryEvent
import com.ryan.dicodingstorycompose.presentation.add_story.AddStoryScreen
import com.ryan.dicodingstorycompose.presentation.add_story.AddStoryViewModel
import com.ryan.dicodingstorycompose.presentation.camera.CameraScreen
import com.ryan.dicodingstorycompose.presentation.home.HomeScreen
import com.ryan.dicodingstorycompose.presentation.home.HomeViewModel
import com.ryan.dicodingstorycompose.presentation.login.LoginScreen
import com.ryan.dicodingstorycompose.presentation.map.MapScreen
import com.ryan.dicodingstorycompose.presentation.register.RegisterScreen
import com.ryan.dicodingstorycompose.ui.theme.AppTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var session: Session

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                    val scope = rememberCoroutineScope()

                    // Observe back navigation event
                    val currentBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = currentBackStackEntry?.destination?.route

                    BackHandler(enabled = drawerState.isOpen) {
                        scope.launch {
                            drawerState.close()
                        }
                    }

                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        gesturesEnabled = drawerState.isOpen,
                        drawerContent = {
                            ModalDrawerSheet(
                                modifier = Modifier.requiredWidth(300.dp)
                            ) {
                                NavigationDrawerHeader(modifier = Modifier.padding(16.dp))
                                Divider()
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .verticalScroll(rememberScrollState())
                                        .padding(vertical = 16.dp)
                                ) {
                                    appDrawerItems.forEach { item ->
                                        val isSelected = item.route == currentRoute
                                        NavigationDrawerItem(
                                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                                            label = { Text(stringResource(item.title)) },
                                            selected = isSelected,
                                            icon = {
                                                Icon(
                                                    imageVector = if (isSelected)
                                                        item.selectedIcon
                                                    else
                                                        item.unselectedIcon,
                                                    contentDescription = stringResource(id = item.title)
                                                )
                                            },
                                            onClick = {
                                                scope.launch {
                                                    drawerState.close()
                                                }
                                                navController.navigate(item.route) {
                                                    popUpTo(navController.graph.findStartDestination().id)
                                                    launchSingleTop = true
                                                }
                                            },
                                        )
                                    }
                                }
                                Divider()
                                Text(
                                    text = stringResource(id = R.string.account),
                                    color = Color.Gray,
                                    modifier = Modifier.padding(16.dp),
                                    style = MaterialTheme.typography.labelLarge
                                )
                                NavigationDrawerItem(
                                    label = { Text(text = stringResource(id = R.string.logout)) },
                                    selected = false,
                                    onClick = {
                                        scope.launch {
                                            session.saveToken("")
                                            drawerState.close()
                                        }
                                    },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.Logout,
                                            contentDescription = stringResource(id = R.string.logout)
                                        )
                                    },
                                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                NavigationDrawerFooter(modifier = Modifier.padding(bottom = 16.dp))
                            }
                        }
                    ) {
                        val token = runBlocking { session.getTokenFlow().first() }
                        val startDestination = if (token.isBlank()) "auth" else "main"

                        NavHost(
                            navController = navController,
                            startDestination = startDestination
                        ) {
                            navigation(
                                startDestination = "login",
                                route = "auth"
                            ) {
                                composable("login") {
                                    LoginScreen(
                                        onRegisterClick = {
                                            navController.navigate("register")
                                        },
                                        onLoggedIn = {
                                            navController.navigate("main") {
                                                popUpTo("main")
                                            }
                                        }
                                    )
                                }

                                composable("register") {
                                    RegisterScreen(
                                        onNavigateBack = { navController.navigateUp() }
                                    )
                                }
                            }

                            navigation(
                                startDestination = "home",
                                route = "main"
                            ) {
                                composable("home") { entry ->

                                    val viewModel: HomeViewModel = hiltViewModel()
                                    entry.getBackStackResult<Boolean>(
                                        this@MainActivity,
                                        "reload"
                                    ) { shouldReload ->
                                        if (shouldReload) viewModel.getStories()
                                    }

                                    HomeScreen(
                                        viewModel = viewModel,
                                        onLogout = {
                                            navController.navigate("auth") {
                                                popUpTo("auth")
                                            }
                                        },
                                        onNavigationIconClick = {
                                            scope.launch {
                                                drawerState.open()
                                            }
                                        },
                                        onFloatingActionButtonClick = {
                                            navController.navigate("add_story")
                                        }
                                    )
                                }

                                composable("about") {
                                    AboutScreen(
                                        onNavigationIconClick = {
                                            scope.launch {
                                                drawerState.open()
                                            }
                                        }
                                    )
                                }

                                composable("maps") {
                                    MapScreen(
                                        onNavigationIconClick = {
                                            scope.launch {
                                                drawerState.open()
                                            }
                                        }
                                    )
                                }

                                composable("add_story") { entry ->

                                    val viewModel: AddStoryViewModel = hiltViewModel()
                                    entry.getBackStackResult<String>(
                                        lifecycleOwner = this@MainActivity,
                                        key = "file_path"
                                    ) {
                                        val photoFile = File(it)
                                        viewModel.onEvent(AddStoryEvent.OnPhotoChange(photoFile))
                                    }

                                    AddStoryScreen(
                                        viewModel = viewModel,
                                        onNavigationIconClick = {
                                            navController.navigateUp()
                                        },
                                        onNavigateBackAndReload = {
                                            navController.setBackStackResult(
                                                key = "reload",
                                                result = true
                                            )
                                            navController.popBackStack()
                                        },
                                        onNavigateToCamera = {
                                            navController.navigate("camera")
                                        }
                                    )
                                }

                                composable("camera") {
                                    CameraScreen(
                                        onNavigateBackWithResult = { photoPath ->
                                            navController.setBackStackResult(
                                                key = "file_path",
                                                result = photoPath
                                            )
                                            navController.popBackStack()
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}