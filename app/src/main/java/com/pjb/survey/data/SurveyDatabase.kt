package com.pjb.survey.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Questionnaire::class, SurveyResult::class], version = 1)
@TypeConverters(Converters::class)
abstract class SurveyDatabase : RoomDatabase() {
    abstract fun questionnaireDao(): QuestionnaireDao
    abstract fun surveyResultDao(): SurveyResultDao
    companion object {
        const val DATABASE_NAME = "survey_database"


        @Volatile
        private var instance: SurveyDatabase? = null

        fun getInstance(context: Context): SurveyDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): SurveyDatabase {
            return Room.databaseBuilder(context, SurveyDatabase::class.java, DATABASE_NAME).build()
        }
    }
}