package com.shopper.domain.interactor

import com.shopper.cache.ProductCache
import com.shopper.cache.model.CachedProduct
import com.shopper.domain.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class GetProductsFromCache @Inject constructor(
    private val productCache: ProductCache,
) : GetProducts {

    override fun execute(): Flow<List<Product>> =
        productCache.getAllProducts()
            .map(::mapEntities)

    private fun mapEntities(entities: List<CachedProduct>): List<Product> =
        entities.map { entity ->
            Product(
                name = entity.name
            )
        }
}
