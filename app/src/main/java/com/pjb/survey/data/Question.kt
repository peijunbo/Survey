package com.pjb.survey.data

import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
data class Question(
    val type: QuestionType,
    val questionText: String,
    val possibleAnswer: PossibleAnswer
)

@Serializable
data class QuestionResult(
    val questionId: Int,
    val type: QuestionType,
    val answer: Answer<*>?,
    @PrimaryKey(autoGenerate = true) val resultId: Long = 0,
)

@Serializable
sealed class PossibleAnswer {
    @Serializable
    data class SingleChoice(val optionsString: List<String>) : PossibleAnswer()

    @Serializable
    data class MultipleChoice(val optionsString: List<String>) : PossibleAnswer()

    @Serializable
    data class Blank(val hint: String?) : PossibleAnswer()
}

@Serializable
sealed class Answer<T : PossibleAnswer> {
    @Serializable
    data class SingleChoice(val answer: Int) : Answer<PossibleAnswer.SingleChoice>()

    @Serializable
    data class MultipleChoice(val answers: Set<Int>) : Answer<PossibleAnswer.MultipleChoice>()

    @Serializable
    data class Blank(val answer: String) : Answer<PossibleAnswer.Blank>()
}