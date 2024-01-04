package com.ryan.dicodingstorycompose.presentation.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ryan.dicodingstorycompose.R
import com.ryan.dicodingstorycompose.common.components.CustomTopAppBar
import com.ryan.dicodingstorycompose.common.pullrefresh.PullRefreshIndicator
import com.ryan.dicodingstorycompose.common.pullrefresh.pullRefresh
import com.ryan.dicodingstorycompose.common.pullrefresh.rememberPullRefreshState
import com.ryan.dicodingstorycompose.presentation.home.components.ShimmerStoryItem
import com.ryan.dicodingstorycompose.presentation.home.components.StoryItem
import com.ryan.dicodingstorycompose.ui.theme.AppTheme

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onLogout: () -> Unit,
    onNavigationIconClick: () -> Unit,
    onFloatingActionButtonClick: () -> Unit,
) {

    HomeScreenContent(
        state = viewModel.state,
        onLogout = onLogout,
        onNavigationIconClick = onNavigationIconClick,
        onRefresh = viewModel::getStories,
        onFloatingActionButtonClick = onFloatingActionButtonClick
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    state: HomeState,
    onLogout: () -> Unit,
    onNavigationIconClick: () -> Unit,
    onRefresh: () -> Unit,
    onFloatingActionButtonClick: () -> Unit,
) {

    LaunchedEffect(key1 = state.isLoggedIn) {
        if (!state.isLoggedIn) onLogout()
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = state.isLoading,
        onRefresh = onRefresh
    )

    val listState = rememberLazyListState()
    val isScrolledOnTop by remember {
        derivedStateOf { listState.firstVisibleItemIndex == 0 }
    }
//    val isScrolledOnTop by remember {
//        derivedStateOf { listState.firstVisibleItemScrollOffset == 0 }
//    }

    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = stringResource(R.string.home),
                navigationIcon = Icons.Default.Menu,
                navigationDescription = stringResource(id = R.string.menu),
                onNavigationIconClick = onNavigationIconClick
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onFloatingActionButtonClick,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PostAdd,
                        contentDescription = stringResource(R.string.add_story)
                    )
                    AnimatedVisibility(visible = isScrolledOnTop) {
                        Row {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = stringResource(R.string.add_story))
                        }
                    }
                }
            }
        }
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .pullRefresh(pullRefreshState)
                .padding(contentPadding)
        ) {
            LazyColumn(
                contentPadding = PaddingValues(vertical = 16.dp),
                state = listState
            ) {
                if (state.isLoading) {
                    items(10) {
                        ShimmerStoryItem(
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                } else {
                    items(
                        items = state.stories,
                        key = { it.id }
                    ) { item ->
                        StoryItem(
                            item = item,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }
            PullRefreshIndicator(
                refreshing = state.isLoading,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    AppTheme {
        HomeScreenContent(
            state = HomeState(),
            onLogout = {},
            onNavigationIconClick = {},
            onRefresh = {},
            onFloatingActionButtonClick = {}
        )
    }
}