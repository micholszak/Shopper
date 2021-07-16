package com.shopper.presentation.addition.model

sealed class AddProductState {
    object Idle : AddProductState()
    object Pending : AddProductState()
    object Added : AddProductState()
}
