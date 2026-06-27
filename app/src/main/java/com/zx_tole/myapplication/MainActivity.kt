package com.zx_tole.myapplication

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import android.os.Bundle
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.material3.Surface
import com.zx_tole.myapplication.data.MockData
import com.zx_tole.myapplication.model.AITool
import com.zx_tole.myapplication.model.Category
import com.zx_tole.myapplication.ui.theme.MyApplicationTheme

// Extension function for Category to get display name
@Composable
fun Category.getDisplayName(): String {
    return stringResource(id = this.displayNameRes)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                AIToolsApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIToolsApp() {
    var currentDestination by remember { mutableStateOf(AppDestinations.HOME) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var selectedTool by remember { mutableStateOf<AITool?>(null) }
    
    // Favorite state: list of tool IDs
    var favoriteToolIds by remember { mutableStateOf<Set<Int>>(emptySet()) }
    
    // Function to toggle favorite
    val toggleFavorite = { tool: AITool ->
        favoriteToolIds = if (favoriteToolIds.contains(tool.id)) {
            favoriteToolIds - tool.id
        } else {
            favoriteToolIds + tool.id
        }
    }
    
    // Function to check if tool is in favorites
    val isFavorite = { tool: AITool -> favoriteToolIds.contains(tool.id) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ) {
                NavigationBarItem(
                    icon = { Icon(painter = painterResource(id = AppDestinations.HOME.iconRes), contentDescription = null) },
                    label = { Text(stringResource(AppDestinations.HOME.labelRes)) },
                    selected = currentDestination == AppDestinations.HOME,
                    onClick = { currentDestination = AppDestinations.HOME }
                )
                NavigationBarItem(
                    icon = { Icon(painter = painterResource(id = AppDestinations.FAVORITES.iconRes), contentDescription = null) },
                    label = { Text(stringResource(AppDestinations.FAVORITES.labelRes)) },
                    selected = currentDestination == AppDestinations.FAVORITES,
                    onClick = { currentDestination = AppDestinations.FAVORITES }
                )
                NavigationBarItem(
                    icon = { Icon(painter = painterResource(id = AppDestinations.PROFILE.iconRes), contentDescription = null) },
                    label = { Text(stringResource(AppDestinations.PROFILE.labelRes)) },
                    selected = currentDestination == AppDestinations.PROFILE,
                    onClick = { currentDestination = AppDestinations.PROFILE }
                )
            }
        }
    ) { innerPadding ->
        when (currentDestination) {
            AppDestinations.HOME -> {
                HomeScreen(
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    selectedCategory = selectedCategory,
                    onCategorySelect = { selectedCategory = it },
                    onToolClick = { tool ->
                        selectedTool = tool
                    },
                    onToggleFavorite = toggleFavorite,
                    isFavorite = isFavorite,
                    modifier = Modifier.padding(innerPadding)
                )
            }

            AppDestinations.FAVORITES -> {
                FavoritesScreen(
                    onToolClick = { tool ->
                        selectedTool = tool
                    },
                    onToggleFavorite = toggleFavorite,
                    isFavorite = isFavorite,
                    modifier = Modifier.padding(innerPadding)
                )
            }

            AppDestinations.PROFILE -> {
                ProfileScreen(modifier = Modifier.padding(innerPadding))
            }
        }

        // Show details dialog for selected tool
        selectedTool?.let { tool ->
            ToolDetailsDialog(
                tool = tool,
                isFavorite = isFavorite(tool),
                onDismiss = { selectedTool = null },
                onToggleFavorite = { toggleFavorite(tool) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedCategory: Category?,
    onCategorySelect: (Category?) -> Unit,
    onToolClick: (AITool) -> Unit,
    onToggleFavorite: (AITool) -> Unit,
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
                    label = category.getDisplayName(),
                    isSelected = selectedCategory == category,
                    onClick = { onCategorySelect(category) }
                )
            }
        }

        // AI Tools List
        val filteredTools = remember(searchQuery, selectedCategory) {
            MockData.aiTools.filter { tool ->
                val matchesSearch = tool.name.contains(searchQuery, ignoreCase = true) ||
                    tool.description.contains(searchQuery, ignoreCase = true)
                val matchesCategory = selectedCategory == null || tool.category == selectedCategory
                matchesSearch && matchesCategory
            }
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            itemsIndexed(filteredTools) { index, tool ->
                AIToolCard(
                    tool = tool,
                    isFavorite = isFavorite(tool),
                    onToggleFavorite = { onToggleFavorite(tool) },
                    onClick = { onToolClick(tool) }
                )
                if (index < filteredTools.size - 1) {
                    androidx.compose.material3.HorizontalDivider(
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
    onToggleFavorite: () -> Unit,
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
                    text = tool.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    painter = painterResource(id = tool.category.iconRes),
                    contentDescription = tool.category.getDisplayName(),
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                text = tool.category.getDisplayName(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = tool.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 8.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = onToggleFavorite
                ) {
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
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolDetailsDialog(
    tool: AITool,
    isFavorite: Boolean,
    onDismiss: () -> Unit,
    onToggleFavorite: () -> Unit
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
                // Header with icon and favorite button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = tool.category.iconRes),
                        contentDescription = tool.category.getDisplayName(),
                        modifier = Modifier.size(32.dp)
                    )
                    IconButton(onClick = onToggleFavorite) {
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
                    text = tool.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp)
                )

                Text(
                    text = tool.category.getDisplayName(),
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
                            text = "Image: ${tool.name}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }

                Text(
                    text = tool.description,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 16.dp)
                )

                androidx.compose.material3.HorizontalDivider(
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
                        color = MaterialTheme.colorScheme.primary
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

@Composable
fun FavoritesScreen(
    onToolClick: (AITool) -> Unit,
    onToggleFavorite: (AITool) -> Unit,
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
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
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
                        onToggleFavorite = { onToggleFavorite(tool) },
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
            fontWeight = FontWeight.Bold
        )

        androidx.compose.material3.HorizontalDivider(
            modifier = Modifier.padding(vertical = 16.dp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
        )

        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = stringResource(R.string.version),
            style = MaterialTheme.typography.bodyMedium
        )

        androidx.compose.material3.HorizontalDivider(
            modifier = Modifier.padding(vertical = 24.dp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
        )

        Text(
            text = stringResource(R.string.description),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = stringResource(R.string.description),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp)
        )

        androidx.compose.material3.HorizontalDivider(
            modifier = Modifier.padding(vertical = 24.dp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
        )

        Text(
            text = stringResource(R.string.developer),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = stringResource(R.string.created_with),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

enum class AppDestinations(
    val labelRes: Int,
    val iconRes: Int,
) {
    HOME(R.string.search_placeholder, R.drawable.ic_home),
    FAVORITES(R.string.favorite, R.drawable.ic_favorite),
    PROFILE(R.string.profile, R.drawable.ic_assistant),
}

@PreviewScreenSizes
@Composable
fun AIToolsAppPreview() {
    MyApplicationTheme {
        Surface {
            AIToolsApp()
        }
    }
}
