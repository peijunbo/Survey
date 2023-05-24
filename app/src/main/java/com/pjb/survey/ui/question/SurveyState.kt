package com.pjb.survey.ui.question

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.pjb.survey.data.*

class QuestionState(
    val question: Question,
    val questionIndex: Int,
    val showPrevious: Boolean,
    val showDone: Boolean
) {
    var enableNext by mutableStateOf(false)
    var answer by mutableStateOf<Answer<*>?>(null)
}

sealed class SurveyState {
    data class QuestionnaireState(
        val questionnaire: Questionnaire,
        val questionsState: List<QuestionState>
    ) : SurveyState() {
        var currentQuestionIndex by mutableStateOf(0)
        fun getResult() = SurveyResult(
            this.questionsState.mapIndexed { index, questionState ->
                QuestionResult(
                    index,
                    questionState.question.type,
                    questionState.answer,
                )
            },
            this.questionnaire.id
        )
    }
}

 fun Questionnaire.getSurveyState() = SurveyState.QuestionnaireState(
    questionnaire = this,
    questionsState = questions.mapIndexed { index, question ->
        QuestionState(
            question = question,
            questionIndex = index,
            showPrevious = index > 0,
            showDone = index == questions.size - 1
        )
    }
)