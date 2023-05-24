package com.pjb.survey.ui.question

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.with
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pjb.survey.data.*
import com.pjb.survey.ui.theme.LargeTextSize
import com.pjb.survey.ui.theme.SemiTransparent
import com.pjb.survey.ui.theme.SurveyTheme


private const val TAG = "Compose SurveyScreen"


@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SurveyScreen(
    questionnaireState: SurveyState.QuestionnaireState,
    onDonePressed: () -> Unit,
) {
    val questionState = remember(questionnaireState.currentQuestionIndex) {
        questionnaireState.questionsState[questionnaireState.currentQuestionIndex]
    }
    val backHandlingEnabled = remember(questionnaireState.currentQuestionIndex) {
        questionnaireState.currentQuestionIndex > 0
    }
    val progress = animateFloatAsState(
        targetValue = questionnaireState.currentQuestionIndex.toFloat(),
        animationSpec = tween(500)
    )
    BackHandler(backHandlingEnabled) {
        questionnaireState.currentQuestionIndex--
    }
    Scaffold(
        topBar = {
            LinearProgressIndicator(
                progress = progress.value / questionnaireState.questionnaire.questions.size.toFloat(),
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.fillMaxWidth()
            )
        },
        content = { paddingValues ->
            AnimatedContent(
                targetState = questionState,
                transitionSpec = {
                    val direction =
                        if (targetState.questionIndex > initialState.questionIndex) {
                            // Going forwards in the survey: Set the initial offset to start
                            // at the size of the content so it slides in from right to left, and
                            // slides out from the left of the screen to -fullWidth
                            AnimatedContentScope.SlideDirection.Left
                        } else {
                            // Going back to the previous question in the set, we do the same
                            // transition as above, but with different offsets - the inverse of
                            // above, negative fullWidth to enter, and fullWidth to exit.
                            AnimatedContentScope.SlideDirection.Right
                        }
                    slideIntoContainer(towards = direction) with
                            slideOutOfContainer(towards = direction)
                },
                modifier = Modifier.padding(paddingValues)
            ) { targetState: QuestionState ->
                QuestionContent(
                    question = targetState.question,
                    answer = targetState.answer,
                    onAnswer = {
                        targetState.answer = it
                        targetState.enableNext = true
                        Log.d(TAG, "Screen onAnswer: $it")
                    }
                )
            }
        },
        bottomBar = {
            //底部按钮
            SurveyBottomBar(
                questionState = questionState,
                onPreviousPressed = { questionnaireState.currentQuestionIndex-- },
                onNextPressed = { questionnaireState.currentQuestionIndex++ },
                onDonePressed = onDonePressed
            )
        }
    )
}

@Composable
private fun SurveyBottomBar(
    questionState: QuestionState,
    onPreviousPressed: () -> Unit,
    onNextPressed: () -> Unit,
    onDonePressed: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 7.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            if (questionState.showPrevious) {
                OutlinedButton(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    onClick = onPreviousPressed
                ) {
                    Text(text = "上一题")
                }
            }
            if (questionState.showDone) {
                Button(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    onClick = onDonePressed,
                    enabled = questionState.enableNext
                ) {
                    Text(text = "提交")
                }
            } else {
                Button(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    onClick = onNextPressed,
                    enabled = questionState.enableNext
                ) {
                    Text(text = "下一题")
                }
            }
        }
    }
}


@Composable
fun QuestionContent(
    question: Question,
    answer: Answer<*>?,
    onAnswer: (Answer<*>) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        //问题
        Text(
            text = question.questionText,
            fontSize = LargeTextSize,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 18.dp, start = 8.dp, end = 8.dp)
        )
        //选项
        when (question.possibleAnswer) {
            is PossibleAnswer.SingleChoice -> SingleChoiceQuestion(
                possibleAnswer = question.possibleAnswer,
                answer = answer as Answer.SingleChoice?,
                onAnswerSelected = {
                    //创建answer对象，it为选中选项的索引值
                    onAnswer(Answer.SingleChoice(it))
                }
            )
            is PossibleAnswer.MultipleChoice -> MultipleChoiceQuestion(
                possibleAnswer = question.possibleAnswer,
                answer = answer as Answer.MultipleChoice?,
                onAnswerSelected = { newAnswer, selected ->
                    if (answer == null) {
                        onAnswer(Answer.MultipleChoice(setOf(newAnswer)))
                    } else {
                        val newSet = answer.answers.toMutableSet()
                        if (!selected) {
                            newSet.remove(newAnswer)
                        } else {
                            newSet.add(newAnswer)
                        }
                        onAnswer(Answer.MultipleChoice(newSet))
                    }
                }
            )
            is PossibleAnswer.Blank -> BlankQuestion(
                possibleAnswer = question.possibleAnswer,
                answer = answer as Answer.Blank?,
                onAnswer = {
                    onAnswer(Answer.Blank(it))
                }
            )
        }
    }
}


@Composable
fun SingleChoiceQuestion(
    possibleAnswer: PossibleAnswer.SingleChoice,
    answer: Answer.SingleChoice?,
    onAnswerSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = possibleAnswer.optionsString
    val selected = answer?.answer
    val (selectedOption, onOptionSelected) = remember(answer) {
        mutableStateOf(
            if (selected != null) options[selected] else null
        )
    }
    LazyColumn(
        modifier = modifier
    ) {
        itemsIndexed(options) { index: Int, text: String ->
            val onClickHandle = {
                onOptionSelected(text)
                onAnswerSelected(index)
                Log.d(TAG, "SingleChoiceQuestion: $text")
                Unit
            }
            val optionSelected = text == selectedOption
            val answerBorderColor = if (optionSelected) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            } else {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
            }
            val answerBackgroundColor = if (optionSelected) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
            } else {
                MaterialTheme.colorScheme.background
            }
            Surface(
                border = BorderStroke(
                    width = 1.dp,
                    color = answerBorderColor
                ),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                //子选项
                Row(
                    modifier = Modifier.selectable(
                        selected = optionSelected,
                        onClick = onClickHandle
                    ).fillMaxWidth(0.8f)
                ) {
                    RadioButton(
                        selected = optionSelected,
                        onClick = onClickHandle
                    )
                    Text(text = text)
                }
            }
        }
    }
}

@Composable
fun MultipleChoiceQuestion(
    possibleAnswer: PossibleAnswer.MultipleChoice,
    answer: Answer.MultipleChoice?,
    onAnswerSelected: (Int, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = possibleAnswer.optionsString
    LazyColumn(
        modifier = modifier
    ) {
        itemsIndexed(options) { index: Int, text: String ->
            var checkedState by remember(answer) {
                val selectedOption = answer?.answers?.contains(index)
                mutableStateOf(selectedOption ?: false)
            }
            val answerBorderColor = if (checkedState) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            } else {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
            }
            val answerBackgroundColor = if (checkedState) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
            } else {
                MaterialTheme.colorScheme.background
            }
            Surface(
                border = BorderStroke(
                    width = 1.dp,
                    color = answerBorderColor
                ),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                //子选项
                Row(
                    modifier = Modifier.clickable {
                        checkedState = !checkedState
                        onAnswerSelected(index, checkedState)
                    }.fillMaxWidth(0.8f)
                ) {
                    Checkbox(checked = checkedState, onCheckedChange = {
                        checkedState = it
                        onAnswerSelected(index, it)
                    })
                    Text(text = text)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlankQuestion(
    possibleAnswer: PossibleAnswer.Blank,
    answer: Answer.Blank?,
    onAnswer: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var text by remember {
        val t = answer?.answer ?: ""
        mutableStateOf(t)
    }
    Row(
        modifier = modifier
    ) {
        TextField(value = text, onValueChange = {
            text = it
            onAnswer(it)
        }, placeholder = {
            Text(
                text = possibleAnswer.hint ?: "", color = MaterialTheme.colorScheme.onSurface.copy(
                    SemiTransparent
                )
            )
        })
    }
}

@Preview(name = "SurveyScreen Night", uiMode = UI_MODE_NIGHT_YES)
@Preview(name = "SurveyScreen Night", uiMode = UI_MODE_NIGHT_NO)
@Composable
fun PreviewSurveyScreen() {
    val questionnaire = Questionnaire(
        title = "标题",
        description = "描述，这是一个这样的问卷",
        questions = listOf(
            Question(
                QuestionType.SingleChoice, "问题1", PossibleAnswer.SingleChoice(
                    listOf("选项1", "选项2", "选项3")
                )
            ),
            Question(
                QuestionType.SingleChoice, "问题2", PossibleAnswer.SingleChoice(
                    listOf("选项1", "选项2", "选项3")
                )
            ),
            Question(
                QuestionType.SingleChoice, "问题3", PossibleAnswer.SingleChoice(
                    listOf("选项1", "选项2", "选项3")
                )
            ),
            Question(
                QuestionType.SingleChoice, "问题4", PossibleAnswer.SingleChoice(
                    listOf("选项1", "选项2", "选项3")
                )
            )
        )
    )
    val surveyState = SurveyState.QuestionnaireState(
        questionnaire = questionnaire,
        questionsState = questionnaire.questions.mapIndexed { index, question ->
            QuestionState(
                question = question,
                questionIndex = index,
                showPrevious = index > 0,
                showDone = index == questionnaire.questions.size - 1
            )
        }
    )
    SurveyTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            SurveyScreen(
                questionnaireState = surveyState,
                onDonePressed = {}
            )
        }
    }
}