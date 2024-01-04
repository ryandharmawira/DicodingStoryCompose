package com.ryan.dicodingstorycompose.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ryan.dicodingstorycompose.R

@Composable
fun NavigationDrawerFooter(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
    ) {
        Divider()
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.version, com.ryan.dicodingstorycompose.BuildConfig.VERSION_NAME),
            fontStyle = FontStyle.Italic,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = stringResource(id = R.string.developer_name),
            fontStyle = FontStyle.Italic,
            fontWeight = FontWeight.SemiBold,
        )
    }
}