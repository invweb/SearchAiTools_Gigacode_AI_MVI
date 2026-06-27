package com.zx_tole.myapplication.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zx_tole.myapplication.R
import com.zx_tole.myapplication.data.MockData
import com.zx_tole.myapplication.model.AITool
import com.zx_tole.myapplication.model.Category

@Composable
fun HomeScreen(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedCategory: Category?,
    onCategorySelect: (Category?) -> Unit,
    onToolClick: (AITool) -> Unit,
    isFavorite: (AITool) -> Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        // Search Field
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = { Text(stringResource(R.string.search_placeholder)) },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_home),
                    contentDescription = null
                )
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                onSearchQueryChange(searchQuery)
            }),
            singleLine = true
        )

        // Category Filter
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                CategoryChip(
                    label = stringResource(R.string.all),
                    isSelected = selectedCategory == null,
                    onClick = { onCategorySelect(null) }
                )
            }
            items(Category.values()) { category ->
                CategoryChip(
                    label = stringResource(id = category.displayNameRes),
                    isSelected = selectedCategory == category,
                    onClick = { onCategorySelect(category) }
                )
            }
        }

        // AI Tools List
        val searchQueryLower = searchQuery.lowercase()
        val filteredTools = MockData.aiTools.filter { tool ->
            val toolName = stringResource(id = tool.nameResId)
            val toolDescription = stringResource(id = tool.descriptionResId)
            val matchesSearch = toolName.lowercase().contains(searchQueryLower) ||
                toolDescription.lowercase().contains(searchQueryLower)
            val matchesCategory = selectedCategory == null || tool.category == selectedCategory
            matchesSearch && matchesCategory
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            itemsIndexed(filteredTools) { index, tool ->
                AIToolCard(
                    tool = tool,
                    isFavorite = isFavorite(tool),
                    onClick = { onToolClick(tool) }
                )
                if (index < filteredTools.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            },
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 14.sp
        )
    }
}

@Composable
fun AIToolCard(
    tool: AITool,
    isFavorite: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(tool.nameResId),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
//                Icon(
//                    painter = painterResource(id = tool.category.iconRes),
//                    contentDescription = stringResource(id = tool.category.displayNameRes),
//                    modifier = Modifier.size(20.dp)
//                )
            }

            Text(
                text = stringResource(id = tool.category.displayNameRes),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = stringResource(tool.descriptionResId),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolDetailsDialog(
    tool: AITool,
    isFavorite: Boolean,
    onDismiss: () -> Unit,
    onToggleFavorite: (AITool) -> Unit
) {
    androidx.compose.material3.BasicAlertDialog(
        onDismissRequest = onDismiss
    ) {
        androidx.compose.material3.Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header with favorite button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { onToggleFavorite(tool) }) {
                        Icon(
                            painter = if (isFavorite) {
                                painterResource(id = R.drawable.ic_favorite)
                            } else {
                                painterResource(id = R.drawable.ic_favorite_outline)
                            },
                            contentDescription = if (isFavorite) {
                                stringResource(R.string.remove_from_favorite)
                            } else {
                                stringResource(R.string.add_to_favorite)
                            },
                            tint = if (isFavorite) {
                                MaterialTheme.colorScheme.error
                            } else {
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            },
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Text(
                    text = stringResource(tool.nameResId),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp)
                )

                Text(
                    text = stringResource(id = tool.category.displayNameRes),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp)
                )

                // Placeholder image
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(top = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_image_generate),
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Image: ${stringResource(tool.nameResId)}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }

                Text(
                    text = stringResource(tool.descriptionResId),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth()
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 16.dp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                )

                Text(
                    text = stringResource(R.string.website),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                androidx.compose.material3.TextButton(
                    onClick = { /* Open URL */ },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(
                        text = tool.url,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                androidx.compose.material3.TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text(stringResource(R.string.close))
                }
            }
        }
    }
}
