package com.pjb.survey.ui.design

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pjb.survey.SurveyApplication
import com.pjb.survey.data.Questionnaire
import com.pjb.survey.data.SurveyDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DesignViewModel : ViewModel() {
    companion object {
        private const val TAG = "DesignViewModel"
    }
    private val context by lazy { SurveyApplication.context }
    private val database: SurveyDatabase = SurveyDatabase.getInstance(context)

    private val _uiState = MutableStateFlow<DesignState?>(null)
    val uiState: StateFlow<DesignState?>
        get() = _uiState


    fun insertAndDesign() {
        viewModelScope.launch(Dispatchers.IO) {
            val questionnaire = Questionnaire("", "", listOf())
            val id = database.questionnaireDao().insert(questionnaire)

            withContext(Dispatchers.Main) {
                _uiState.value = DesignState(id = id)
                Log.d(TAG, "insertAndDesign: ui")
            }
        }
    }

    fun startDesign(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val questionnaire = database.questionnaireDao().getQuestionnaireById(id = id)
            withContext(Dispatchers.Main) {
                _uiState.value = questionnaire.getDesignState()
            }
        }
    }

    fun checkResult(): Boolean {
        uiState.value?.apply {
            if (questionnaireState.title == "") {
                Toast.makeText(context, "请添加标题", Toast.LENGTH_SHORT).show()
                return false
            }
            if (questionnaireState.description == "") {
                Toast.makeText(context, "请添加问卷说明", Toast.LENGTH_SHORT).show()
                return false
            }
            if (questions.size == 0) {
                Toast.makeText(context, "至少添加一个标题", Toast.LENGTH_SHORT).show()
                return false
            }
        }
        return true
    }

    fun finishDesign() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value?.getQuestionnaire()?.let {
                database.questionnaireDao().update(it)
                Log.d(TAG, "finishDesign: update")
            }
        }
    }
}