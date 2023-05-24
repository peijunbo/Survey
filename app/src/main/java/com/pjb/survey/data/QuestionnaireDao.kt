package com.pjb.survey.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionnaireDao {
    @Insert
    fun insert(value: Questionnaire): Long

    @Update
    fun update(value: Questionnaire)

    @Delete
    fun delete(value: Questionnaire)

    @Query("DELETE FROM Questionnaire WHERE id = :id")
    fun deleteById(id: Long)

    @Query("SELECT * FROM Questionnaire")
    fun getAll(): List<Questionnaire>

    @Query("SELECT * FROM Questionnaire WHERE id = :id")
    fun getQuestionnaireById(id: Long): Questionnaire

    @Transaction
    @Query("SELECT * FROM Questionnaire")
    fun getAllWithResults(): Flow<List<QuestionnaireWithResults>>
}