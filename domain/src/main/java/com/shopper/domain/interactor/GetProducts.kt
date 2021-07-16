package com.shopper.domain.interactor

import com.shopper.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface GetProducts {

    fun execute(): Flow<List<Product>>
}
