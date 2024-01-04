package com.ryan.dicodingstorycompose.navigation

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.ryan.dicodingstorycompose.R

@Composable
fun NavigationDrawerHeader(
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier,
        text = stringResource(id = R.string.app_name),
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold
    )
}