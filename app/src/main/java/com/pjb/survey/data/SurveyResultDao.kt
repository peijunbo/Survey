package com.pjb.survey.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SurveyResultDao {
    @Insert
    fun insert(value: SurveyResult): Long

    @Delete
    fun delete(value: SurveyResult)

    @Query("DELETE FROM SurveyResult WHERE questionnaireId = :questionnaireId")
    fun deleteByQuestionnaireId(questionnaireId: Long)

    @Query("SELECT * FROM SurveyResult")
    fun getAll(): List<SurveyResult>
}