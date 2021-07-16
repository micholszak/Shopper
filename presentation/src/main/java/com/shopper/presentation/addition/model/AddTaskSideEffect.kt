package com.shopper.presentation.addition.model

sealed class AddTaskSideEffect {
    object EmptyFieldError : AddTaskSideEffect()
}
