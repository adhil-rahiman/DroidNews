package com.droidnotes.feature.news.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.droidnotes.domain.news.model.Article
import com.droidnotes.domain.news.model.Source
import com.droidnotes.feature.news.R
import java.time.Instant
import java.time.format.DateTimeFormatter

@Composable
fun ArticleCard(
    article: Article,
    onArticleClick: (String) -> Unit,
    onBookmarkClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onArticleClick(article.id) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            // Article image
            article.imageUrl?.let { imageUrl ->
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
                Spacer(modifier = Modifier.width(16.dp))
            }

            // Article content
            Column(modifier = Modifier.weight(1f)) {
                // Title
                Text(
                    text = article.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Description
                article.description?.let { description ->
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Source and date
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = article.source.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = formatPublishedDate(article.publishedAt),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Bookmark button
            IconButton(onClick = { onBookmarkClick(article.id) }) {
                Icon(
                    painter = painterResource(
                        if (article.isBookmarked) R.drawable.ic_bookmark_filled
                        else R.drawable.ic_bookmark_outline
                    ),
                    contentDescription = if (article.isBookmarked) "Remove bookmark" else "Add bookmark",
                    tint = if (article.isBookmarked) MaterialTheme.colorScheme.primary
                           else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun formatPublishedDate(publishedAt: Instant): String {
    val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
    return publishedAt.atZone(java.time.ZoneId.systemDefault()).format(formatter)
}

@Preview(showBackground = true)
@Composable
private fun ArticleCardPreview() {
    val sampleArticle = Article(
        id = "1",
        title = "Breaking News: Major Tech Announcement",
        description = "A major technology company has announced a groundbreaking new product that could change the industry forever.",
        content = "Full article content here...",
        url = "https://example.com/article",
        imageUrl = null,
        source = Source(id = "tech-news", name = "Tech News"),
        publishedAt = Instant.now(),
        isBookmarked = false
    )

    ArticleCard(
        article = sampleArticle,
        onArticleClick = {},
        onBookmarkClick = {}
    )
}
