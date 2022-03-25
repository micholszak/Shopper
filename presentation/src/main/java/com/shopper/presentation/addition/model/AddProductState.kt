package com.shopper.presentation.addition.model

sealed class AddProductState {
    object Idle : AddProductState()
    object Pending : AddProductState()
    object Added : AddProductState()
    data class Error(val type: ErrorType) : AddProductState() {
        fun mapToSideEffect(): AddProductEffect =
            when (type) {
                ErrorType.EMPTY_FIELD -> AddProductEffect.EmptyFieldError
            }
    }

    enum class ErrorType {
        EMPTY_FIELD
    }
}
