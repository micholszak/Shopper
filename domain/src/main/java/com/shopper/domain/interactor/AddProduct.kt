package com.shopper.domain.interactor

import com.shopper.domain.model.AddProductResult
import com.shopper.domain.model.Product

interface AddProduct {

    suspend fun execute(product: Product): AddProductResult
}
