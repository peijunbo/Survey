package com.pjb.survey.ui.design

import android.content.res.Configuration
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pjb.survey.data.QuestionType
import com.pjb.survey.ui.theme.SemiTransparent
import com.pjb.survey.ui.theme.SeaBlue
import com.pjb.survey.ui.theme.SurveyTheme

private const val TAG = "EditScreen"

@Composable
private fun EditScreen(
    show: Boolean, modifier: Modifier = Modifier, content: @Composable (() -> Unit)
) {
    Box(modifier = modifier) {
        AnimatedVisibility(visible = show,
            enter = slideInVertically { it },
            exit = slideOutVertically { it }) {
            Surface(
                content = content,
                modifier = Modifier.border(1.dp, color = MaterialTheme.colorScheme.outline)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TitleEditScreen(
    show: Boolean,
    questionnaireState: QuestionnaireState,
    modifier: Modifier = Modifier,
    onDone: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    BackHandler(
        enabled = show, onBack = onBack
    )
    EditScreen(show = show, modifier = modifier) {
        val focusManager = LocalFocusManager.current
        Column(Modifier
            .fillMaxSize()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }) { focusManager.clearFocus() }) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Text(
                    text = "标题",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            OutlinedTextField(value = questionnaireState.title, onValueChange = {
                questionnaireState.title = it
            }, placeholder = { Text("输入标题") }, modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Text(
                    text = "简介",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            OutlinedTextField(
                value = questionnaireState.description,
                onValueChange = {
                    questionnaireState.description = it
                },
                placeholder = { Text(text = "输入简介") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
            Spacer(modifier = Modifier.weight(0.2f))
            Button(
                onClick = onDone, modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(text = "完成")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionEditScreen(
    show: Boolean,
    modifier: Modifier = Modifier,
    questionState: QuestionState,
    onBack: () -> Unit = {}
) {
    BackHandler(
        enabled = show, onBack = onBack
    )
    EditScreen(show = show, modifier = modifier) {
        val focusManager = LocalFocusManager.current
        Column(Modifier
            .fillMaxSize()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { focusManager.clearFocus() }) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Text(
                    text = "输入问题",
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            OutlinedTextField(
                value = questionState.questionText,
                onValueChange = {
                    questionState.questionText = it
                },
                placeholder = { Text(text = "输入问题", color = MaterialTheme.colorScheme.outline) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
            when (questionState.possibleAnswer) {
                is AnswerState.SingleChoice -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        Text(
                            text = "输入选项",
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                    SingleChoiceContent(
                        possibleAnswer = questionState.possibleAnswer as AnswerState.SingleChoice
                    )
                }
                is AnswerState.MultipleChoice -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        Text(
                            text = "输入选项",
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                    MultipleChoiceContent(
                        possibleAnswer = questionState.possibleAnswer as AnswerState.MultipleChoice
                    )
                }
                is AnswerState.Blank -> {
                    BlankContent(possibleAnswer = questionState.possibleAnswer as AnswerState.Blank)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SingleChoiceContent(
    possibleAnswer: AnswerState.SingleChoice
) {
    LazyColumn {
        itemsIndexed(possibleAnswer.options) { index, option ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                IconButton(onClick = { possibleAnswer.options.remove(option) }) {
                    Icon(
                        imageVector = Icons.Default.RemoveCircle,
                        contentDescription = "删除",
                        tint = Color.Red,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
                OutlinedTextField(
                    value = option,
                    onValueChange = { possibleAnswer.options[index] = it },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = MaterialTheme.colorScheme.background,
                        unfocusedBorderColor = MaterialTheme.colorScheme.background
                    ),
                    placeholder = {
                        Text(
                            text = "输入选项", color = MaterialTheme.colorScheme.onBackground.copy(
                                SemiTransparent
                            )
                        )
                    },
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                )
            }
        }
    }
    Divider()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        IconButton(onClick = { possibleAnswer.options.add("") }) {
            Icon(
                imageVector = Icons.Default.AddCircle,
                contentDescription = "添加选项",
                tint = SeaBlue,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
        Text(text = "添加选项", modifier = Modifier.align(Alignment.CenterVertically))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MultipleChoiceContent(
    possibleAnswer: AnswerState.MultipleChoice
) {
    LazyColumn {
        itemsIndexed(possibleAnswer.options) { index, option ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                IconButton(onClick = { possibleAnswer.options.remove(option) }) {
                    Icon(
                        imageVector = Icons.Default.RemoveCircle,
                        contentDescription = "删除",
                        tint = Color.Red,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
                OutlinedTextField(
                    value = option,
                    onValueChange = { possibleAnswer.options[index] = it },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = MaterialTheme.colorScheme.background,
                        unfocusedBorderColor = MaterialTheme.colorScheme.background
                    ),
                    placeholder = {
                        Text(
                            text = "输入选项", color = MaterialTheme.colorScheme.onBackground.copy(
                                SemiTransparent
                            )
                        )
                    },
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                )
            }
        }
    }
    Divider()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        IconButton(onClick = { possibleAnswer.options.add("") }) {
            Icon(
                imageVector = Icons.Default.AddCircle,
                contentDescription = "添加选项",
                tint = SeaBlue,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
        Text(text = "添加选项", modifier = Modifier.align(Alignment.CenterVertically))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BlankContent(
    possibleAnswer: AnswerState.Blank
) {
    Row {
        OutlinedTextField(
            value = possibleAnswer.hint.value,
            onValueChange = { possibleAnswer.hint.value = it },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colorScheme.background,
                unfocusedBorderColor = MaterialTheme.colorScheme.background
            ),
            placeholder = {
                Text(
                    text = "输入选项", color = MaterialTheme.colorScheme.onBackground.copy(
                        SemiTransparent
                    )
                )
            },
            modifier = Modifier
                .align(Alignment.CenterVertically)
        )
    }
}

@Preview(name = "EditScreen Light", uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Preview(name = "EditScreen Night", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun PreviewEditScreen() {
    SurveyTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            val designState = DesignState(
                id = 1,
                questions = arrayOf(
                    QuestionState(
                        QuestionType.SingleChoice,
                        questionText = "问题1",
                        possibleAnswer = AnswerState.SingleChoice(
                            arrayOf(
                                "选项1", "选项2", "选项3"
                            )
                        )
                    ), QuestionState(
                        QuestionType.SingleChoice,
                        questionText = "问题2",
                        possibleAnswer = AnswerState.SingleChoice(
                            arrayOf(
                                "选项7", "选项8", "选项9"
                            )
                        )
                    ), QuestionState(
                        QuestionType.SingleChoice,
                        questionText = "问题3",
                        possibleAnswer = AnswerState.SingleChoice(
                            arrayOf(
                                "选项4", "选项5", "选项6"
                            )
                        )
                    )
                )
            )
            designState.questionnaireState.title = "标题"
            designState.questionnaireState.description = "描述"

            Log.d(TAG, "PreviewDesignScreen: ${designState.questions.size}")
            //TitleEditScreen(show = true, questionnaireState = designState.questionnaireState)
            QuestionEditScreen(
                show = true,
                questionState = designState.questions[0]
            )
        }
    }
}