package com.shopper.domain.composition

import com.shopper.domain.interactor.AddProduct
import com.shopper.domain.interactor.AddProductToCache
import com.shopper.domain.interactor.GetProducts
import com.shopper.domain.interactor.GetProductsFromCache
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class InteractionModule {

    @Binds
    abstract fun bindAddProduct(useCase: AddProductToCache): AddProduct

    @Binds
    abstract fun bindGetProducts(useCase: GetProductsFromCache): GetProducts
}
