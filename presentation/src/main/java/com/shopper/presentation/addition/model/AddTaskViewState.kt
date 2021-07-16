package com.shopper.presentation.addition.model

sealed class AddTaskViewState {
    object Idle : AddTaskViewState()
    object Pending : AddTaskViewState()
    object Added : AddTaskViewState()
}
