package com.shopper.presentation.products.model

import com.shopper.domain.model.Product
import javax.inject.Inject

class ProductViewMapper @Inject constructor() {

    fun map(products: List<Product>): List<ProductView> =
        products.map { product ->
            ProductView(name = product.name)
        }
}
