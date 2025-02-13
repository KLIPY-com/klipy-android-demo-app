@file:OptIn(ExperimentalMaterial3Api::class)

package com.klipy.demoapp.presentation.features.conversationlist

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.klipy.demoapp.presentation.features.conversationlist.model.ConversationUiModel
import com.klipy.demoapp.presentation.theme.KlipyDemoAppTheme

@Composable
fun ConversationListScreen(
    onConversationClick: (id: Int) -> Unit
) {
    val topBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topBarState)
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Toolbar()
        },
        contentWindowInsets = ScaffoldDefaults
            .contentWindowInsets
            .exclude(WindowInsets.navigationBars)
    ) { innerPadding ->
        Conversations(
            modifier = Modifier.padding(innerPadding),
            conversations = ConversationUiModel.createMockList(),
            onConversationClick = onConversationClick
        )
    }
}

@Composable
private fun Conversations(
    modifier: Modifier = Modifier,
    conversations: List<ConversationUiModel>,
    onConversationClick: (id: Int) -> Unit
) {
    LazyColumn(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp)
    ) {
        items(conversations) {
            ConversationItem(
                conversation = it,
                onConversationClick = onConversationClick
            )
        }
    }
}

@Composable
private fun Toolbar() {
    TopAppBar(
        modifier = Modifier
            .fillMaxWidth(),
        title = {
            Box(modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center) {
                Text("Klipy")
            }
        },
        navigationIcon = {
            Image(
                modifier = Modifier
                    .padding(8.dp)
                    .size(26.dp),
                imageVector = Icons.Default.Menu,
                contentDescription = "Back",
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
            )
        },
        actions = {
            Image(
                modifier = Modifier
                    .padding(8.dp)
                    .size(26.dp),
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            scrolledContainerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Preview
@Composable
fun ToolbarPreview() {
    KlipyDemoAppTheme {
        Toolbar()
    }
}