package com.pjb.survey.data

import androidx.room.TypeConverter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {
    @TypeConverter
    fun questionTypeToInt(value: QuestionType): Int {
        return value.ordinal
    }

    @TypeConverter
    fun questionTypeFromInt(value: Int): QuestionType {
        return QuestionType.values()[value]
    }


    @TypeConverter
    fun questionToJson(value: Question): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun questionFromJson(value: String): Question {
        return Json.decodeFromString(Question.serializer(), value)
    }


    @TypeConverter
    fun questionListToJson(value: List<Question>): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun questionListFromJson(value: String): List<Question> {
        return Json.decodeFromString(value)
    }


    @TypeConverter
    fun surveyResultToJson(value: SurveyResult): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun surveyResultFromJson(value: String): SurveyResult {
        return Json.decodeFromString(value)
    }

    @TypeConverter
    fun questionResultsToJson(value: List<QuestionResult>): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun questionResultsFromJson(value: String): List<QuestionResult> {
        return Json.decodeFromString(value)
    }
}