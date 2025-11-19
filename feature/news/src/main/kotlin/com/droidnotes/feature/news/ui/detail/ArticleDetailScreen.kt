package com.droidnotes.feature.news.ui.detail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.droidnotes.feature.news.R
import com.droidnotes.feature.news.ui.ArticleDetailUiState
import com.droidnotes.feature.news.viewmodel.ArticleDetailViewModel
import java.time.Instant
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleDetailScreen(
    articleId: String,
    onBackClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    viewModel: ArticleDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Article") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back),
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (uiState is ArticleDetailUiState.Success) {
                        val article = (uiState as ArticleDetailUiState.Success).article

                        IconButton(onClick = {
                            val shareIntent = Intent.createChooser(
                                Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        "${article.title}\n\n${article.url}"
                                    )
                                    type = "text/plain"
                                },
                                "Share article"
                            )
                            context.startActivity(shareIntent)
                        }, modifier = Modifier.size(24.dp)) {
                            Icon(
                                painter = painterResource(R.drawable.ic_share),
                                contentDescription = "Share"
                            )
                        }

                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_more),
                                contentDescription = "More options"
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Open in browser") },
                                onClick = {
                                    showMenu = false
                                    val browserIntent =
                                        Intent(Intent.ACTION_VIEW, Uri.parse(article.url))
                                    context.startActivity(browserIntent)
                                }
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (uiState is ArticleDetailUiState.Success) {
                val article = (uiState as ArticleDetailUiState.Success).article
                FloatingActionButton(onClick = {
                    viewModel.toggleBookmark(article)
                    onBookmarkClick()
                }) {
                    Icon(
                        painter = painterResource(
                            if (article.isBookmarked) R.drawable.ic_bookmark_filled
                            else R.drawable.ic_bookmark_outline
                        ),
                        contentDescription = if (article.isBookmarked) "Remove bookmark" else "Add bookmark"
                    )
                }
            }
        }
    ) { padding ->
        when (uiState) {
            is ArticleDetailUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is ArticleDetailUiState.Success -> {
                val article = (uiState as ArticleDetailUiState.Success).article
                ArticleContent(
                    article = article,
                    modifier = Modifier.padding(padding)
                )
            }

            is ArticleDetailUiState.Error -> {
                val error = uiState as ArticleDetailUiState.Error
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = error.message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun ArticleContent(
    article: com.droidnotes.domain.news.model.Article,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
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
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        }

        // Title
        Text(
            text = article.title,
            style = MaterialTheme.typography.headlineMedium
        )

        // Source and date
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = article.source.name,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = formatPublishedDate(article.publishedAt),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Description
        article.description?.let { description ->
            Text(
                text = description,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        // Content
        article.content?.let { content ->
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Read full article link
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Read full article: ${article.url}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

private fun formatPublishedDate(publishedAt: Instant): String {
    val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' HH:mm")
    return publishedAt.atZone(java.time.ZoneId.systemDefault()).format(formatter)
}
