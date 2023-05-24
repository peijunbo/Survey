package com.pjb.survey.ui.result

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pjb.survey.data.Answer
import com.pjb.survey.data.PossibleAnswer
import com.pjb.survey.data.QuestionnaireWithResults
import com.pjb.survey.ui.overview.OverviewViewModel
import com.pjb.survey.ui.theme.LargeTextSize

@Composable
fun ResultScreen(
    itemId: Int,
    overviewViewModel: OverviewViewModel = viewModel()
) {
    if (itemId == -1) {
        val itemList by overviewViewModel.questionnaireWithResults.collectAsState(initial = listOf())
        AllResults(itemList = itemList)
    } else {
        val itemList by overviewViewModel.questionnaireWithResults.collectAsState(initial = listOf())
        if (itemList.size > itemId) {
            SingleResult(questionnaireWithResults = itemList[itemId])
        }
    }
}

@Composable
fun AllResults(
    itemList: List<QuestionnaireWithResults>
) {
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(itemList) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(0.8f)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = item.questionnaire.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = LargeTextSize,
                            modifier = Modifier.align(CenterHorizontally)
                        )
                        item.surveyResult.forEachIndexed { index, surveyResult ->
                            Text(text = "答卷$index")
                            surveyResult.questionResults.forEachIndexed { _, questionResult ->
                                Text(text = "问题${questionResult.questionId}")
                                when (questionResult.answer) {
                                    is Answer.SingleChoice -> {
                                        val possibleAnswer =
                                            item.questionnaire.questions[questionResult.questionId].possibleAnswer as PossibleAnswer.SingleChoice
                                        val option =
                                            possibleAnswer.optionsString[questionResult.answer.answer]
                                        Text(text = "选项: $option")
                                    }
                                    is Answer.MultipleChoice -> {
                                        val possibleAnswer =
                                            item.questionnaire.questions[questionResult.questionId].possibleAnswer as PossibleAnswer.MultipleChoice
                                        val options =
                                            possibleAnswer.optionsString.filterIndexed { idx, _ -> idx in questionResult.answer.answers }
                                        Text(text = "选项:")
                                        options.forEach {
                                            Text(text = it)
                                        }
                                    }
                                    is Answer.Blank -> {
                                        Text(text = "回答: ${questionResult.answer.answer}")
                                    }
                                    null -> {}
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SingleResult(
    questionnaireWithResults: QuestionnaireWithResults
) {
    val questionnaire = questionnaireWithResults.questionnaire
    val results = questionnaireWithResults.surveyResult
    Surface {
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(scrollState)
        ) {
            Text(
                text = questionnaire.title,
                fontWeight = FontWeight.Bold,
                fontSize = LargeTextSize,
                modifier = Modifier.align(CenterHorizontally)
            )
            results.forEachIndexed { index, surveyResult ->
                Text(text = "答卷$index")
                surveyResult.questionResults.forEachIndexed { _, questionResult ->
                    Text(text = "问题${questionResult.questionId}")
                    when (questionResult.answer) {
                        is Answer.SingleChoice -> {
                            val possibleAnswer =
                                questionnaire.questions[questionResult.questionId].possibleAnswer as PossibleAnswer.SingleChoice
                            val option =
                                possibleAnswer.optionsString[questionResult.answer.answer]
                            Text(text = "选项: $option")
                        }
                        is Answer.MultipleChoice -> {
                            val possibleAnswer =
                                questionnaire.questions[questionResult.questionId].possibleAnswer as PossibleAnswer.MultipleChoice
                            val options =
                                possibleAnswer.optionsString.filterIndexed { idx, _ -> idx in questionResult.answer.answers }
                            Text(text = "选项:")
                            options.forEach {
                                Text(text = it)
                            }
                        }
                        is Answer.Blank -> {
                            Text(text = "回答: ${questionResult.answer.answer}")
                        }
                        null -> {}
                    }
                }
            }
        }
    }
}