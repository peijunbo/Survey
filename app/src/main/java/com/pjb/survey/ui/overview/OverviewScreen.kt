package com.pjb.survey.ui.overview

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pjb.survey.data.Questionnaire
import com.pjb.survey.data.QuestionnaireWithResults
import com.pjb.survey.ui.question.SurveyViewModel
import com.pjb.survey.ui.theme.LargeTextSize
import com.pjb.survey.ui.theme.SemiTransparent
import com.pjb.survey.ui.theme.SmallTextSize
import com.pjb.survey.ui.theme.SurveyTheme
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverviewScreen(
    onSurvey: (Questionnaire) -> Unit = {},
    onEdit: (Long) -> Unit = {},
    onAdd: () -> Unit = {},
    onShowResult: (Int) -> Unit = {},
    overviewViewModel: OverviewViewModel = viewModel()
) {
    Scaffold(
        topBar = {
            OverviewTopBar(
                onAdd = onAdd,
                onImport = {
                    overviewViewModel.importQuestionnaire()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    ) {
        Surface(
            modifier = Modifier.padding(it)
        ) {
            Column {
                val itemList by overviewViewModel.questionnaireWithResults.collectAsState(initial = listOf())
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(itemList) { index, item ->
                        QuestionnaireContent(
                            item = item,
                            onExport = { questionnaire ->
                                overviewViewModel.exportQuestionnaire(questionnaire)
                            },
                            onEdit = { onEdit(item.questionnaire.id) },
                            onDelete = { overviewViewModel.deleteById(item.questionnaire.id) },
                            onSurvey = onSurvey,
                            onShowResult = { onShowResult(index) },
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverviewTopBar(
    modifier: Modifier = Modifier,
    onAdd: () -> Unit = {},
    onImport: () -> Unit = {},
    surveyViewModel: SurveyViewModel = viewModel()
) {
    Box(
    ) {
        TopAppBar(
            title = { Text(text = "调查助手", modifier = Modifier.align(Alignment.Center)) },
            modifier = modifier,
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                actionIconContentColor = MaterialTheme.colorScheme.onPrimary
            ),
            actions = {
                IconButton(onClick = { surveyViewModel.insertTestQuestionnaire() }) {
                    Icon(imageVector = Icons.Default.AddBox, contentDescription = "插入")
                }
                var showMenu by remember {
                    mutableStateOf(false)
                }
                IconButton(onClick = { showMenu = true }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "添加问卷")
                }
                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                    DropdownMenuItem(
                        text = { Text(text = "创建") },
                        onClick = { onAdd();showMenu = false })
                    DropdownMenuItem(
                        text = { Text(text = "从剪贴板导入") },
                        onClick = { onImport();showMenu = false })
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuestionnaireContent(
    item: QuestionnaireWithResults,
    modifier: Modifier = Modifier,
    onDelete: () -> Unit = {},
    onEdit: () -> Unit = {},
    onExport: (Questionnaire) -> Unit = {},
    onSurvey: (Questionnaire) -> Unit = {},
    onShowResult: () -> Unit = {}
) {
    var showMenu by remember {
        mutableStateOf(false)
    }
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        elevation = CardDefaults.cardElevation(4.dp),
        onClick = { onSurvey(item.questionnaire) }
    ) {
        Row(
            modifier = modifier
        ) {
            Text(
                text = item.questionnaire.title,
                fontSize = LargeTextSize,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Spacer(modifier = Modifier.weight(1f))
            Column {
                Text(
                    text = "${item.surveyResult.size}",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Text(
                    text = "答卷数量",
                    color = Color.Unspecified.copy(SemiTransparent),
                    fontSize = SmallTextSize
                )
            }
            Box {

                IconButton(
                    onClick = { showMenu = true }
                ) {
                    Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Options")
                }
                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                    DropdownMenuItem(
                        text = { Text(text = "编辑") },
                        onClick = { onEdit();showMenu = false })
                    DropdownMenuItem(
                        text = { Text(text = "作答情况") },
                        onClick = { onShowResult();showMenu = false })
                    DropdownMenuItem(
                        text = { Text(text = "导出为JSON") },
                        onClick = {
                            onExport(item.questionnaire)
                            showMenu = false
                        })
                    DropdownMenuItem(
                        text = { Text(text = "删除") },
                        onClick = { onDelete();showMenu = false })
                }
            }
        }
    }

}


@Preview(
    name = "OverviewScreen Light",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    showBackground = true
)
@Preview(
    name = "OverviewScreen Night",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true
)
@Composable
fun PreviewOverviewScreen() {
    SurveyTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            OverviewScreen()
        }
    }
}