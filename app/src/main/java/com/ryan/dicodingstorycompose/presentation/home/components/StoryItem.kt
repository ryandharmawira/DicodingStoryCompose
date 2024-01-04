package com.ryan.dicodingstorycompose.presentation.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.ryan.dicodingstorycompose.common.shimmerEffect
import com.ryan.dicodingstorycompose.common.toTimeDiffer
import com.ryan.dicodingstorycompose.domain.model.Story
import com.ryan.dicodingstorycompose.ui.theme.AppTheme
import java.time.LocalDateTime
import com.ryan.dicodingstorycompose.R

@Composable
fun StoryItem(
    modifier: Modifier = Modifier,
    item: Story,
) {

    val context = LocalContext.current

    var isOverflow by rememberSaveable { mutableStateOf(false) }
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    val descriptionText = buildAnnotatedString {
        withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
            append(item.authorName)
        }
        append(" ")
        append(item.description)
    }

    Column(
        modifier = modifier,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.placeholder_no_photo),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = item.authorName,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        SubcomposeAsyncImage(
            model = item.photoUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 300.dp),
            loading = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .shimmerEffect()
                )
//                CircularProgressIndicator(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .wrapContentSize()
//                        .align(Alignment.Center)
//                )
            },
            error = {
                Image(
                    painter = painterResource(id = R.drawable.ic_error_image),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = descriptionText,
            maxLines = if (isExpanded) Int.MAX_VALUE else 2,
            overflow = if (isExpanded) TextOverflow.Visible else TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 16.dp),
            onTextLayout = { textLayoutResult ->
                isOverflow = textLayoutResult.hasVisualOverflow
//                if (textLayoutResult.hasVisualOverflow) {
//                    val lineEndIndex = textLayoutResult.getLineEnd(
//                        lineIndex = 0,
//                        visibleEnd = true
//                    )
//                    badgeCount = descriptionText
//                        .substring(lineEndIndex)
//                        .count { it == ',' }
//                }
            }
        )
        if (isOverflow) {
            Text(
                text = "more",
                color = Color.Gray,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .clickable {
                        isExpanded = !isExpanded
                    },
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = item.createdAt.toTimeDiffer(context),
            color = Color.Gray,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

@Composable
fun ShimmerStoryItem(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .shimmerEffect()
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.3f)
                    .height(20.dp)
                    .shimmerEffect()
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .shimmerEffect()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .height(20.dp)
                .shimmerEffect()
        )
        Spacer(modifier = Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(0.4f)
                .height(12.dp)
                .shimmerEffect()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun StoryItemPreview() {
    AppTheme {
        StoryItem(
            item = Story(
                id = "0",
                authorName = "test",
                description = "Ini adalah deskripsi.",
                photoUrl = "",
                createdAt = LocalDateTime.of(2023, 11, 18, 0, 0),
                lat = null,
                lon = null
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}