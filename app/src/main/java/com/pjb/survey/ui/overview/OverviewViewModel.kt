package com.pjb.survey.ui.overview

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pjb.survey.SurveyApplication
import com.pjb.survey.data.Questionnaire
import com.pjb.survey.data.QuestionnaireWithResults
import com.pjb.survey.data.SurveyDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class OverviewViewModel : ViewModel() {
    companion object {
        private const val TAG = "OverviewViewModel"
    }

    private val context by lazy { SurveyApplication.context }
    private val database: SurveyDatabase = SurveyDatabase.getInstance(context)

    private val _uiState = MutableStateFlow(listOf<QuestionnaireWithResults>())
    val uiState: StateFlow<List<QuestionnaireWithResults>>
        get() = _uiState

    val questionnaireWithResults
        get() = database.questionnaireDao().getAllWithResults()

    fun importQuestionnaire() {
        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val json = clipboardManager.primaryClip?.getItemAt(0)?.text
        if (json == null) {
            Toast.makeText(context, "剪贴板为空", Toast.LENGTH_SHORT).show()
        }
        var questionnaire: Questionnaire? = null
        try {
            questionnaire = Json.decodeFromString<Questionnaire>(json.toString())
        } catch (e: Exception) {
            Log.e(TAG, "importQuestionnaire: Json parse error!", )
            Toast.makeText(context, "Json解析失败", Toast.LENGTH_SHORT).show()
        }
        if (questionnaire != null) {
            viewModelScope.launch(Dispatchers.IO) {
                database.questionnaireDao().insert(questionnaire.copy(id = 0))
            }
        }
    }

    fun exportQuestionnaire(questionnaire: Questionnaire) {
        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val json = Json.encodeToString(questionnaire)
        val clipData = ClipData.newPlainText("问卷", json)
        clipboardManager.setPrimaryClip(clipData)
    }

    fun deleteById(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            database.questionnaireDao().deleteById(id)
            database.surveyResultDao().deleteByQuestionnaireId(id)
        }
    }
}