package com.taskflow.ui.tasks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.taskflow.data.model.Category
import com.taskflow.ui.AppViewModelProvider
import com.taskflow.ui.components.EmptyState
import com.taskflow.ui.components.GradientFab
import com.taskflow.ui.components.TaskRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    onAddTask: () -> Unit,
    onOpenTask: (Long) -> Unit,
    viewModel: TaskListViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var sortMenuOpen by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("할 일", style = MaterialTheme.typography.titleLarge)
                        Text(
                            text = "진행 ${state.activeCount} · 완료 ${state.completedCount}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                actions = {
                    TextButton(onClick = { viewModel.toggleShowCompleted() }) {
                        Text(if (state.filter.showCompleted) "완료 숨기기" else "완료 표시")
                    }
                },
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            GradientFab(
                onClick = onAddTask,
                icon = Icons.Filled.Add,
                contentDescription = "추가",
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            CategoryFilterRow(
                selected = state.filter.category,
                onSelect = viewModel::setCategory,
            )
            // 정렬 선택(탭으로 메뉴 열기)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box {
                    TextButton(onClick = { sortMenuOpen = true }) {
                        Icon(Icons.Filled.Sort, contentDescription = null, modifier = Modifier.height(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(state.filter.sort.label)
                    }
                    DropdownMenu(
                        expanded = sortMenuOpen,
                        onDismissRequest = { sortMenuOpen = false },
                    ) {
                        SortMode.entries.forEach { mode ->
                            DropdownMenuItem(
                                text = { Text(mode.label) },
                                onClick = {
                                    viewModel.setSort(mode)
                                    sortMenuOpen = false
                                },
                            )
                        }
                    }
                }
            }

            if (state.tasks.isEmpty()) {
                EmptyState(emoji = "📝", message = "표시할 할 일이 없습니다. 오른쪽 아래 버튼으로 추가해 보세요.")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        start = 16.dp, end = 16.dp, top = 4.dp, bottom = 88.dp,
                    ),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(state.tasks, key = { it.id }) { task ->
                        TaskRow(
                            task = task,
                            onToggleComplete = { checked -> viewModel.toggleComplete(task, checked) },
                            onClick = { onOpenTask(task.id) },
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryFilterRow(
    selected: Category?,
    onSelect: (Category?) -> Unit,
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            FilterChip(
                selected = selected == null,
                onClick = { onSelect(null) },
                label = { Text("전체") },
            )
        }
        items(Category.entries.toList()) { category ->
            FilterChip(
                selected = selected == category,
                onClick = { onSelect(if (selected == category) null else category) },
                label = { Text(category.label) },
            )
        }
    }
}
