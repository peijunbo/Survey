package com.pjb.survey.ui.design

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CheckBox
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pjb.survey.data.QuestionType
import com.pjb.survey.ui.theme.LargeTextSize
import com.pjb.survey.ui.theme.SemiTransparent
import com.pjb.survey.ui.theme.SurveyTheme

private const val TAG = "DesignScreen"

enum class IconQuestion(
    val icon: ImageVector,
    val text: String,
    val type: QuestionType
) {
    SingleChoice(
        icon = Icons.Default.RadioButtonChecked,
        text = "单选题",
        type = QuestionType.SingleChoice
    ),
    MultipleChoice(
        icon = Icons.Outlined.CheckBox,
        text = "多选题",
        type = QuestionType.MultipleChoice
    ),
    Blank(
        icon = Icons.Default.BorderColor,
        text = "填空题",
        type = QuestionType.Blank
    )
}

@Composable
fun DesignScreen(
    designState: DesignState,
    onDone: () -> Unit = {},
) {
    var showTitleEdit by remember { mutableStateOf(false) }
    var showQuestionEdit by remember { mutableStateOf(false) }
    var showSelType by remember { mutableStateOf(false) }
    var questionState = QuestionState(QuestionType.SingleChoice)
    BackHandler(
        onBack = onDone
    )
    Surface {
        Column {
            Box {
                QuestionnaireContent(
                    designState = designState,
                    onTitleEdit = { showTitleEdit = true },
                    onQuestionEdit = { questionState = it; showQuestionEdit = true },
                    onAddQuestion = { showSelType = true },
                    onCopy = {
                        designState.questions.add(
                            it,
                            designState.questions[it].copy()
                        )
                    }
                )
                TitleEditScreen(
                    show = showTitleEdit,
                    questionnaireState = designState.questionnaireState,
                    onDone = { showTitleEdit = false },
                    onBack = { showTitleEdit = false },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
                QuestionEditScreen(
                    show = showQuestionEdit,
                    questionState = questionState,
                    onBack = { showQuestionEdit = false },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
                SelQuestionType(
                    show = showSelType,
                    onSelect = {
                        showSelType = false
                        designState.addEmptyQuestion(it)
                    },
                    onBack = {
                        showSelType = false
                    },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}

@Composable
private fun QuestionnaireTopPart(
    designState: DesignState,
    onTitleEdit: () -> Unit
) {
    Row {
        //问卷标题和简介
        var showTitleMenu by remember { mutableStateOf(false) }
        ColumnWithMenu(
            show = showTitleMenu,
            onEdit = {
                onTitleEdit()
                showTitleMenu = false
            }
        ) {
            //标题
            Surface(modifier = Modifier
                .padding(8.dp)
                .clickable {
                    showTitleMenu = !showTitleMenu
                }) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    val title =
                        if (designState.questionnaireState.title == "") "添加标题"
                        else designState.questionnaireState.title
                    val description =
                        if (designState.questionnaireState.description == "") "添加问卷说明"
                        else designState.questionnaireState.description
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic,
                        fontSize = 24.sp,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                    )
                    Text(
                        text = description,
                        fontSize = 12.sp,
                    )
                }
            }
        }
    }
}

@Composable
private fun QuestionnaireContent(
    designState: DesignState,
    modifier: Modifier = Modifier,
    onTitleEdit: () -> Unit = {},
    onQuestionEdit: (QuestionState) -> Unit = {},
    onAddQuestion: () -> Unit = {},
    onCopy: (index: Int) -> Unit = {}
) {
    Column(
        modifier = modifier
    ) {
        //标题
        QuestionnaireTopPart(designState = designState, onTitleEdit = onTitleEdit)
        Divider()
        //问题部分
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(top = 16.dp)
                .weight(1f)
        ) {
            itemsIndexed(designState.questions) { index: Int, item: QuestionState ->
                var showMenu by remember { mutableStateOf(false) }
                ColumnWithMenu(
                    show = showMenu,
                    showCopy = true,
                    showUp = index > 0,
                    showDown = index < designState.questions.size - 1,
                    showDelete = true,
                    onCopy = { onCopy(index); showMenu = false },
                    onEdit = { onQuestionEdit(item); showMenu = false },
                    onUp = { designState.swap(index, index - 1);showMenu = false },
                    onDown = { designState.swap(index, index + 1);showMenu = false },
                    onDelete = { designState.questions.removeAt(index) },
                    modifier = Modifier.background(
                        MaterialTheme.colorScheme.tertiaryContainer.copy(
                            SemiTransparent
                        )
                    )
                ) {
                    QuestionContent(
                        questionState = item, index = index,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(remember { MutableInteractionSource() }, null) {
                                showMenu = !showMenu
                            }
                            .background(MaterialTheme.colorScheme.surface)
                    )
                }
            }
            item {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface)
                        .clickable(onClick = onAddQuestion)
                ) {
                    Icon(
                        imageVector = Icons.Default.AddCircle,
                        contentDescription = "添加问题",
                        tint = MaterialTheme.colorScheme.surfaceTint
                    )
                    Text(text = "添加问题", fontSize = LargeTextSize)
                }
            }
            item {
                Spacer(modifier = Modifier.height(128.dp))
            }
        }


    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelQuestionType(
    show: Boolean,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onSelect: (QuestionType) -> Unit
) {
    BackHandler(enabled = show, onBack = onBack)
    Box(modifier = modifier) {
        AnimatedVisibility(visible = show,
            enter = slideInVertically { it },
            exit = slideOutVertically { it }) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .border(1.dp, color = MaterialTheme.colorScheme.outline)
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 64.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(32.dp)
                ) {
                    items(IconQuestion.values()) { item ->
                        Card(
                            onClick = { onSelect(item.type) }
                        ) {
                            Column {
                                Icon(imageVector = item.icon, contentDescription = item.text)
                                Text(text = item.text)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuestionContent(
    questionState: QuestionState,
    index: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
    ) {
        Column {
            Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text(
                    text = "${index + 1}. " + questionState.questionText,
                    fontWeight = FontWeight.Bold
                )
            }

            when (questionState.possibleAnswer) {
                is AnswerState.SingleChoice -> {
                    SingleChoiceContent(questionState.possibleAnswer as AnswerState.SingleChoice)
                }
                is AnswerState.MultipleChoice -> {
                    MultipleChoiceContent(questionState.possibleAnswer as AnswerState.MultipleChoice)
                }
                is AnswerState.Blank -> {
                    BlankContent(questionState.possibleAnswer as AnswerState.Blank)
                }
            }
        }
    }
}

@Composable
private fun SingleChoiceContent(
    possibleAnswer: AnswerState.SingleChoice,
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        possibleAnswer.options.forEachIndexed { index, option ->
            Row {
                RadioButton(
                    selected = false, onClick = {}, enabled = false,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Text(
                    text = option,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }

    }
}


@Composable
private fun MultipleChoiceContent(
    possibleAnswer: AnswerState.MultipleChoice,
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        possibleAnswer.options.forEachIndexed { index, option ->
            Row {
                Checkbox(
                    checked = false, onCheckedChange = {}, enabled = false,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Text(
                    text = option,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }

    }
}

@Composable
private fun BlankContent(
    possibleAnswer: AnswerState.Blank
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row {
            Text(text = possibleAnswer.hint.value)
        }
    }
}

@Composable
fun ColumnWithMenu(
    modifier: Modifier = Modifier,
    show: Boolean,
    showEdit: Boolean = true,
    showCopy: Boolean = false,
    showUp: Boolean = false,
    showDown: Boolean = false,
    showDelete: Boolean = false,
    onEdit: () -> Unit = {},
    onCopy: () -> Unit = {},
    onUp: () -> Unit = {},
    onDown: () -> Unit = {},
    onDelete: () -> Unit = {},
    content: @Composable() (ColumnScope.() -> Unit)
) {

    Column {
        content()
        AnimatedVisibility(
            visible = show,
        ) {
            Divider()
            Row(
                modifier = modifier
            ) {
                if (showEdit) {

                    IconButton(
                        onClick = onEdit, modifier = Modifier
                            .weight(1f)
                            .align(Alignment.CenterVertically)
                    ) {
                        Icon(
                            imageVector = Icons.Default.EditNote,
                            contentDescription = "编辑"
                        )
                    }
                }
                if (showCopy) {
                    IconButton(
                        onClick = onCopy, modifier = Modifier
                            .weight(1f)
                            .align(Alignment.CenterVertically)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "复制"
                        )
                    }
                }
                if (showUp) {
                    IconButton(
                        onClick = onUp, modifier = Modifier
                            .weight(1f)
                            .align(Alignment.CenterVertically)
                    ) {
                        Icon(imageVector = Icons.Default.ArrowUpward, contentDescription = "上移")
                    }
                }
                if (showDown) {
                    IconButton(
                        onClick = onDown, modifier = Modifier
                            .weight(1f)
                            .align(Alignment.CenterVertically)
                    ) {
                        Icon(imageVector = Icons.Default.ArrowDownward, contentDescription = "下移")

                    }
                }
                if (showDelete) {
                    IconButton(
                        onClick = onDelete, modifier = Modifier
                            .weight(1f)
                            .align(Alignment.CenterVertically)
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "删除")
                    }
                }
            }

        }
    }
}

@Preview(name = "DesignScreen Light", uiMode = UI_MODE_NIGHT_NO, showBackground = true)
@Preview(name = "DesignScreen Night", uiMode = UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun PreviewDesignScreen() {
    SurveyTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val designState = DesignState(
                id = 1,
                questions = arrayOf(
                    QuestionState(
                        QuestionType.SingleChoice,
                        questionText = "问题1",
                        possibleAnswer = AnswerState.SingleChoice(
                            arrayOf(
                                "选项1",
                                "选项2",
                                "选项3"
                            )
                        )
                    ),
                    QuestionState(
                        QuestionType.SingleChoice,
                        questionText = "问题2",
                        possibleAnswer = AnswerState.SingleChoice(
                            arrayOf(
                                "选项7",
                                "选项8",
                                "选项9"
                            )
                        )
                    ),
                    QuestionState(
                        QuestionType.SingleChoice,
                        questionText = "问题3",
                        possibleAnswer = AnswerState.SingleChoice(
                            arrayOf(
                                "选项4",
                                "选项5",
                                "选项6"
                            )
                        )
                    )
                )
            )
            designState.questionnaireState.title = "标题"
            designState.questionnaireState.description = "描述"

            Log.d(TAG, "PreviewDesignScreen: ${designState.questions.size}")
            DesignScreen(
                designState
            )
        }
    }
}
