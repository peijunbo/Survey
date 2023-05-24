package com.pjb.survey.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import kotlinx.serialization.Serializable

@Serializable
enum class QuestionType {
    SingleChoice,
    MultipleChoice,
    Blank
}


@Entity
@Serializable
data class Questionnaire(
    val title: String,
    val description: String,
    val questions: List<Question>,
    @PrimaryKey(autoGenerate = true) val id: Long = 0
)



@Entity
@Serializable
data class SurveyResult(
    val questionResults: List<QuestionResult>,
    val questionnaireId: Long,
    @PrimaryKey(autoGenerate = true) val id: Long = 0
)



data class QuestionnaireWithResults(
    @Embedded val questionnaire: Questionnaire,
    @Relation(
        parentColumn = "id",
        entityColumn = "questionnaireId"
    )
    val surveyResult: List<SurveyResult>
)
