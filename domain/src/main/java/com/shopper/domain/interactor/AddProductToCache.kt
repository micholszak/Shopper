package com.shopper.domain.interactor

import com.shopper.cache.ProductCache
import com.shopper.domain.DispatcherProvider
import com.shopper.domain.model.AddProductResult
import com.shopper.domain.model.Product
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class AddProductToCache @Inject constructor(
    private val productCache: ProductCache,
    private val dispatcherProvider: DispatcherProvider,
) : AddProduct {

    companion object {
        private const val EMPTY_PRODUCT_MESSAGE = "The name of the product cannot be empty"
    }

    override suspend fun execute(product: Product): AddProductResult {
        val name = product.name
        if (name.isEmpty()) {
            return AddProductResult.Failure(EMPTY_PRODUCT_MESSAGE)
        }
        withContext(dispatcherProvider.io) {
            productCache.addProduct(productName = name)
        }
        return AddProductResult.Success
    }
}
