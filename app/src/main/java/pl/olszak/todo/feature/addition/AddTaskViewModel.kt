package pl.olszak.todo.feature.addition

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.launch
import pl.olszak.todo.core.IntentProcessor
import pl.olszak.todo.core.Reducer
import pl.olszak.todo.core.ViewStateEvent
import pl.olszak.todo.feature.addition.interactor.AddTask
import pl.olszak.todo.feature.addition.model.AddTaskIntent
import pl.olszak.todo.feature.addition.model.AddTaskResult
import pl.olszak.todo.feature.addition.model.AddViewState
import pl.olszak.todo.feature.addition.model.FieldError
import pl.olszak.todo.feature.data.Task

class AddTaskViewModel @ViewModelInject constructor(
    private val addTask: AddTask
) : ViewModel() {

    private val mutableState: MutableStateFlow<AddViewState> = MutableStateFlow(AddViewState())
    val state: StateFlow<AddViewState> = mutableState

    private val reducer: Reducer<AddViewState, AddTaskResult> = { previous, result ->
        when (result) {
            is AddTaskResult.Pending -> previous.copy(
                isLoading = true
            )
            is AddTaskResult.Added -> AddViewState(
                isLoading = false,
                isTaskAdded = true
            )
            is AddTaskResult.Failure -> previous.copy(
                isLoading = false,
                errorEvent = ViewStateEvent(FieldError.TITLE)
            )
        }
    }

    private val intentProcessor: IntentProcessor<AddTaskIntent, AddTaskResult> = { intent ->
        when (intent) {
            is AddTaskIntent.ProcessTask -> addTaskToStore(intent.taskTitle)
        }
    }

    fun subscribeToIntents(taskIntent: Flow<AddTaskIntent>) {
        viewModelScope.launch {
            taskIntent.flatMapMerge(transform = intentProcessor)
                .scan(AddViewState(), reducer)
                .collect { newState ->
                    mutableState.value = newState
                }
        }
    }

    private fun addTaskToStore(title: String): Flow<AddTaskResult> =
        flow {
            emit(AddTaskResult.Pending)
            val task = Task(title = title)
            addTask(task)
            emit(AddTaskResult.Added)
        }.catch {
            emit(AddTaskResult.Failure)
        }
}
