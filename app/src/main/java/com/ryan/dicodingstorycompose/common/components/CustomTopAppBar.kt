package com.ryan.dicodingstorycompose.common.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopAppBar(
    modifier: Modifier = Modifier,
    isMainNavigation: Boolean = true,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    title: String = "",
    navigationIcon: ImageVector,
    navigationDescription: String = "",
    onNavigationIconClick: () -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
) {
    if (isMainNavigation) {
        CenterAlignedTopAppBar(
            modifier = modifier,
            scrollBehavior = scrollBehavior,
            title = { Text(title) },
            navigationIcon = {
                IconButton(onClick = onNavigationIconClick) {
                    Icon(
                        imageVector = navigationIcon,
                        contentDescription = navigationDescription
                    )
                }
            },
            actions = actions
        )
    } else {
        TopAppBar(
            modifier = modifier,
            scrollBehavior = scrollBehavior,
            title = { Text(title) },
            navigationIcon = {
                IconButton(onClick = onNavigationIconClick) {
                    Icon(
                        imageVector = navigationIcon,
                        contentDescription = navigationDescription
                    )
                }
            },
            actions = actions
        )
    }
}