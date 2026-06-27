package com.zx_tole.myapplication

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import android.os.Bundle
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.zx_tole.myapplication.data.MockData
import com.zx_tole.myapplication.mvi.AIToolIntent
import com.zx_tole.myapplication.mvi.AIToolMvi
import com.zx_tole.myapplication.mvi.AppDestinations
import com.zx_tole.myapplication.model.Category
import com.zx_tole.myapplication.ui.components.MainBottomBar
import com.zx_tole.myapplication.ui.screens.FavoritesScreen
import com.zx_tole.myapplication.ui.screens.HomeScreen
import com.zx_tole.myapplication.ui.screens.ProfileScreen
import com.zx_tole.myapplication.ui.screens.ToolDetailsDialog
import com.zx_tole.myapplication.ui.theme.MyApplicationTheme

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
    val state by AIToolMvi.state

    Scaffold(
        topBar = {
            androidx.compose.material3.TopAppBar(
                title = { androidx.compose.material3.Text(stringResource(R.string.app_name)) },
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            MainBottomBar(
                currentDestination = state.currentDestination,
                onDestinationChange = { destination ->
                    AIToolMvi.handleIntent(AIToolIntent.NavigationChange(destination))
                }
            )
        }
    ) { innerPadding ->
        when (state.currentDestination) {
            AppDestinations.HOME -> {
                HomeScreen(
                    searchQuery = state.searchQuery,
                    onSearchQueryChange = { query ->
                        AIToolMvi.handleIntent(AIToolIntent.SearchQueryChange(query))
                    },
                    selectedCategory = state.selectedCategory,
                    onCategorySelect = { category ->
                        AIToolMvi.handleIntent(AIToolIntent.CategorySelect(category))
                    },
                    onToolClick = { tool ->
                        AIToolMvi.handleIntent(AIToolIntent.ToolClick(tool))
                    },
                    isFavorite = { tool -> state.favoriteToolIds.contains(tool.id) },
                    modifier = Modifier.padding(innerPadding)
                )
            }

            AppDestinations.FAVORITES -> {
                FavoritesScreen(
                    onToolClick = { tool ->
                        AIToolMvi.handleIntent(AIToolIntent.ToolClick(tool))
                    },
                    isFavorite = { tool -> state.favoriteToolIds.contains(tool.id) },
                    modifier = Modifier.padding(innerPadding)
                )
            }

            AppDestinations.PROFILE -> {
                ProfileScreen(modifier = Modifier.padding(innerPadding))
            }
        }

        // Show details dialog for selected tool
        state.selectedTool?.let { tool ->
            ToolDetailsDialog(
                tool = tool,
                isFavorite = state.favoriteToolIds.contains(tool.id),
                onDismiss = {
                    AIToolMvi.handleIntent(AIToolIntent.ToolDismiss)
                },
                onToggleFavorite = { tool ->
                    AIToolMvi.handleIntent(AIToolIntent.ToggleFavorite(tool))
                }
            )
        }
    }
}
