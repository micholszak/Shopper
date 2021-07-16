package com.shopper.presentation.addition.model

sealed class AddProductEffect {
    object EmptyFieldError : AddProductEffect()
}
