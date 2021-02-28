package pl.olszak.todo.presentation.addition.model

import pl.olszak.todo.domain.Result

sealed class AddTaskResult : Result {

    object Pending : AddTaskResult()

    object Added : AddTaskResult()

    object Failure : AddTaskResult()
}