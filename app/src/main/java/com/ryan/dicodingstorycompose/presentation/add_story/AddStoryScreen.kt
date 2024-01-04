package com.ryan.dicodingstorycompose.presentation.add_story

import android.Manifest
import android.annotation.SuppressLint
import android.os.Looper
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter.Companion.tint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.google.android.gms.location.*
import com.ryan.dicodingstorycompose.R
import com.ryan.dicodingstorycompose.common.components.*
import com.ryan.dicodingstorycompose.common.findActivity
import com.ryan.dicodingstorycompose.common.openAppSettings
import com.ryan.dicodingstorycompose.ui.theme.AppTheme
import kotlinx.coroutines.launch

private lateinit var locationCallback: LocationCallback

@SuppressLint("MissingPermission")
@Composable
fun AddStoryScreen(
    viewModel: AddStoryViewModel = hiltViewModel(),
    onNavigationIconClick: () -> Unit,
    onNavigateBackAndReload: () -> Unit,
    onNavigateToCamera: () -> Unit,
) {

    val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
    )

    val dialogQueue = viewModel.visiblePermissionDialogQueue
    val context = LocalContext.current
    val currentActivity = context.findActivity()

    val cameraPermissionResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            viewModel.onPermissionResult(
                permission = Manifest.permission.CAMERA,
                isGranted = isGranted
            )
            if (isGranted) onNavigateToCamera()
        }
    )

    val multiplePermissionResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { perms ->
            locationPermissions.forEach { permission ->
                viewModel.onPermissionResult(
                    permission = permission,
                    isGranted = perms[permission] == true
                )
            }
            val areGranted = perms.values.all { it }
            if (areGranted) {
                val locationProvider = LocationServices.getFusedLocationProviderClient(context)

                val request = LocationRequest.Builder(10000L)
                    .setIntervalMillis(10000L)
                    .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                    .build()

                locationCallback = object : LocationCallback() {
                    override fun onLocationResult(result: LocationResult) {
                        super.onLocationResult(result)
                        result.locations.lastOrNull()?.let { location ->
                            Log.d("RyanTag", "Location: ${location.latitude}, ${location.longitude}")
                            viewModel.onEvent(AddStoryEvent.OnLocationChange(location.latitude, location.longitude))
                            locationProvider.removeLocationUpdates(locationCallback)
                        }
                    }
                }

                locationProvider.requestLocationUpdates(
                    request,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }
//            if (areGranted) {
//                val locationClient = LocationClientImpl(
//                    context,
//                    LocationServices.getFusedLocationProviderClient(context)
//                )
//                locationClient
//                    .getLocationUpdates(10000L)
//                    .catch { e -> e.printStackTrace() }
//                    .onEach { location ->
//                        val lat = location.latitude
//                        val lon = location.longitude
//                        viewModel.onEvent(AddStoryEvent.OnLocationChange(lat, lon))
//                        Log.d("RyanTag", "Location: $lat, $lon")
//                    }
//                    .launchIn(scope)
//            }
        }
    )

    LaunchedEffect(key1 = true) {
        multiplePermissionResultLauncher.launch(locationPermissions)
    }

    AddStoryScreenContent(
        state = viewModel.state,
        onEvent = viewModel::onEvent,
        onNavigationIconClick = onNavigationIconClick,
        onNavigateBack = onNavigateBackAndReload,
        onLaunchCameraPermission = {
            cameraPermissionResultLauncher.launch(Manifest.permission.CAMERA)
        },
    )

    dialogQueue
        .reversed()
        .forEach { permission ->
            PermissionDialog(
                permissionTextProvider = when (permission) {
                    Manifest.permission.CAMERA -> { CameraPermissionTextProvider() }
                    else -> return@forEach
                },
                isPermanentlyDeclined = !currentActivity.shouldShowRequestPermissionRationale(
                    permission
                ),
                onDismiss = viewModel::dismissDialog,
                onOkClick = {
                    viewModel.dismissDialog()
                    multiplePermissionResultLauncher.launch(
                        arrayOf(permission)
                    )
                },
                onGoToAppSettingsClick = {
                    currentActivity.openAppSettings()
                }
            )
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddStoryScreenContent(
    state: AddStoryState,
    onEvent: (AddStoryEvent) -> Unit,
    onNavigationIconClick: () -> Unit,
    onNavigateBack: () -> Unit,
    onLaunchCameraPermission: () -> Unit,
) {

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            CustomTopAppBar(
                isMainNavigation = false,
                title = stringResource(R.string.new_story),
                navigationIcon = Icons.Default.ArrowBack,
                navigationDescription = stringResource(R.string.back),
                onNavigationIconClick = onNavigationIconClick,
            )
        },
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            Column {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 32.dp, vertical = 16.dp)
                ) {
                    if (state.photoFile != null) {
                        AsyncImage(
                            model = state.photoFile.absolutePath,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onLaunchCameraPermission()
                                },
                        )
                    } else {
                        Image(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            colorFilter = tint(MaterialTheme.colorScheme.onBackground),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onLaunchCameraPermission()
                                }
                                .border(width = 1.dp, color = MaterialTheme.colorScheme.secondary),
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        value = state.descriptionInput,
                        onValueChange = { onEvent(AddStoryEvent.OnDescriptionChange(it)) },
                        placeholder = { Text(stringResource(R.string.description_placeholder)) },
                        minLines = 6,
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Button(
                        onClick = { onEvent(AddStoryEvent.OnUploadClick) },
                        enabled = state.isValidInput,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(stringResource(R.string.upload))
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
                onEvent(AddStoryEvent.OnErrorMessageShown)
            }
            if (state.success) {
                CustomAlertDialog(
                    onConfirmation = {
                        onEvent(AddStoryEvent.OnDismissDialog)
                        onNavigateBack()
                    },
                    dialogTitle = stringResource(R.string.dialog_success_add_story_title),
                    dialogText = stringResource(R.string.dialog_success_add_story_description),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddStoryScreenPreview() {
    AppTheme {
        AddStoryScreenContent(
            state = AddStoryState(),
            onEvent = {},
            onNavigationIconClick = {},
            onNavigateBack = {},
            onLaunchCameraPermission = {},
        )
    }
}