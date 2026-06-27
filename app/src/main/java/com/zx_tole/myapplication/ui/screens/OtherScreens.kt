package com.zx_tole.myapplication.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zx_tole.myapplication.R
import com.zx_tole.myapplication.data.MockData
import com.zx_tole.myapplication.model.AITool
import com.zx_tole.myapplication.model.Category

@Composable
fun FavoritesScreen(
    onToolClick: (AITool) -> Unit,
    isFavorite: (AITool) -> Boolean,
    modifier: Modifier = Modifier
) {
    val favoriteTools = MockData.aiTools.filter { isFavorite(it) }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (favoriteTools.isEmpty()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_favorite),
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                )
                Text(
                    text = stringResource(R.string.no_favorites),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 16.dp)
                )
                Text(
                    text = stringResource(R.string.add_to_favorites),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    modifier = Modifier.padding(top = 8.dp),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(favoriteTools) { tool ->
                    AIToolCard(
                        tool = tool,
                        isFavorite = isFavorite(tool),
                        onClick = { onToolClick(tool) }
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.about_app),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 16.dp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
        )

        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )

        Text(
            text = stringResource(R.string.version),
            style = MaterialTheme.typography.bodyMedium
        )

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 24.dp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
        )

        Text(
            text = stringResource(R.string.description),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )

        Text(
            text = stringResource(R.string.description),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp)
        )

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 24.dp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
        )

        Text(
            text = stringResource(R.string.developer),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )

        Text(
            text = stringResource(R.string.created_with),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
