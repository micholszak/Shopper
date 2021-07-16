package com.shopper.domain.model

sealed class AddProductResult {
    object Success : AddProductResult()
    data class Failure(val message: String) : AddProductResult()
}
