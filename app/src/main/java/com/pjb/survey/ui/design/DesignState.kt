package com.pjb.survey.ui.design

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.pjb.survey.data.PossibleAnswer
import com.pjb.survey.data.Question
import com.pjb.survey.data.QuestionType
import com.pjb.survey.data.Questionnaire

class QuestionState(
    val type: QuestionType,
    questionText: String = "",
    possibleAnswer: AnswerState = AnswerState.SingleChoice(),
) {
    var questionText by mutableStateOf(questionText)
    var possibleAnswer by mutableStateOf<AnswerState>(possibleAnswer)
    fun copy() = QuestionState(
        type = type,
        questionText = "" + questionText,
        possibleAnswer = when (possibleAnswer) {
            is AnswerState.SingleChoice -> {
                val t = possibleAnswer as AnswerState.SingleChoice
                AnswerState.SingleChoice(t.options.toTypedArray())
            }
            is AnswerState.MultipleChoice -> {
                val t = possibleAnswer as AnswerState.MultipleChoice
                AnswerState.MultipleChoice(t.options.toTypedArray())
            }
            is AnswerState.Blank -> {
                val t = possibleAnswer as AnswerState.Blank
                AnswerState.Blank(t.hint.value)
            }
        }
    )
}


sealed class AnswerState {
    class SingleChoice(options: Array<String> = arrayOf()) : AnswerState() {
        val options = mutableStateListOf<String>(*options)
    }

    class MultipleChoice(options: Array<String> = arrayOf()) : AnswerState() {
        val options = mutableStateListOf<String>(*options)
    }

    class Blank(hint: String = "") : AnswerState() {
        val hint = mutableStateOf(hint)
    }
}

class QuestionnaireState(
    title: String = "",
    description: String = ""
) {
    var title by mutableStateOf(title)
    var description by mutableStateOf(description)
}

class DesignState(
    val id: Long,
    val questionnaireState: QuestionnaireState = QuestionnaireState(),
    questions: Array<QuestionState> = arrayOf()
) {
    val questions = mutableStateListOf<QuestionState>(*questions)
    fun swap(ia: Int, ib: Int) {
        val t = questions[ia]
        questions[ia] = questions[ib]
        questions[ib] = t
    }

    fun addEmptyQuestion(type: QuestionType, index: Int = questions.size) {
        questions.add(
            index = index,
            QuestionState(
                QuestionType.MultipleChoice,
                "输入问题",
                when (type) {
                    QuestionType.SingleChoice -> AnswerState.SingleChoice()
                    QuestionType.MultipleChoice -> AnswerState.MultipleChoice()
                    QuestionType.Blank -> AnswerState.Blank()
                }
            )
        )
    }

    fun getQuestionnaire() = Questionnaire(
        id = id,
        title = questionnaireState.title,
        description = questionnaireState.description,
        questions = questions.toList().map {
            val possibleAnswer = when (it.possibleAnswer) {
                is AnswerState.SingleChoice -> {
                    val answer = it.possibleAnswer as AnswerState.SingleChoice
                    PossibleAnswer.SingleChoice(answer.options.toList())
                }
                is AnswerState.MultipleChoice -> {
                    val answer = it.possibleAnswer as AnswerState.MultipleChoice
                    PossibleAnswer.MultipleChoice(answer.options.toList())
                }
                is AnswerState.Blank -> {
                    val answer = it.possibleAnswer as AnswerState.Blank
                    PossibleAnswer.Blank(answer.hint.value)
                }
            }
            Question(
                type = it.type,
                questionText = it.questionText,
                possibleAnswer = possibleAnswer
            )
        }
    )
}


fun Questionnaire.getDesignState() = DesignState(
    id = id,
    questionnaireState = QuestionnaireState(
        title = title,
        description = description
    ),
    questions = questions.map {
        val answerState = when (it.possibleAnswer) {
            is PossibleAnswer.SingleChoice -> {
                AnswerState.SingleChoice(it.possibleAnswer.optionsString.toTypedArray())
            }
            is PossibleAnswer.MultipleChoice -> {
                AnswerState.MultipleChoice(it.possibleAnswer.optionsString.toTypedArray())
            }
            is PossibleAnswer.Blank -> {
                AnswerState.Blank(it.possibleAnswer.hint ?: "")
            }
        }
        QuestionState(
            type = it.type,
            questionText = it.questionText,
            possibleAnswer = answerState
        )
    }.toTypedArray()
)