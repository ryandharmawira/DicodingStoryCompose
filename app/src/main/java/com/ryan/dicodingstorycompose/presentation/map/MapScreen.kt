package com.ryan.dicodingstorycompose.presentation.map

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.ryan.dicodingstorycompose.R
import com.ryan.dicodingstorycompose.common.components.CustomTopAppBar
import com.ryan.dicodingstorycompose.common.components.LoadingDialog
import kotlinx.coroutines.launch

@Composable
fun MapScreen(
    viewModel: MapViewModel = hiltViewModel(),
    onNavigationIconClick: () -> Unit,
) {

    val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
    )

    val multiplePermissionResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { perms ->
            val areGranted = perms.values.all { it }
            viewModel.onLocationGranted(areGranted)
        }
    )

    LaunchedEffect(key1 = true) {
        multiplePermissionResultLauncher.launch(locationPermissions)
    }

    MapScreenContent(
        state = viewModel.state,
        onNavigationIconClick = onNavigationIconClick,
        onErrorMessageShown = viewModel::onErrorMessageShown
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreenContent(
    state: MapState,
    onNavigationIconClick: () -> Unit,
    onErrorMessageShown: () -> Unit,
) {

    val indonesiaPosition = LatLng(-2.3813319, 107.2187979)

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val mapProperties = MapProperties(
        isMyLocationEnabled = state.isLocationEnabled,
    )
    val cameraPositionState = rememberCameraPositionState()

    LaunchedEffect(key1 = state.stories) {
        if (state.stories.isNotEmpty()) {
            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(indonesiaPosition, 4f))
        }
    }

    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = stringResource(R.string.maps),
                navigationIcon = Icons.Default.Menu,
                navigationDescription = stringResource(id = R.string.menu),
                onNavigationIconClick = onNavigationIconClick
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize()
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                properties = mapProperties,
                cameraPositionState = cameraPositionState
            ) {
                state.stories.forEach { story ->
                    if (story.lat != null && story.lon != null) {
                        val position = LatLng(story.lat, story.lon)
                        Marker(
                            state = MarkerState(position = position),
                            title = story.authorName,
                            snippet = story.description,
                        )
                    }
                }
            }
            LoadingDialog(isShowingDialog = state.isLoading)
            state.errorMessage?.let {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = it,
                        duration = SnackbarDuration.Short
                    )
                }
                onErrorMessageShown()
            }
        }
    }
}