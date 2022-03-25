package com.shopper.domain.composition

import com.shopper.domain.DefaultDispatcherProvider
import com.shopper.domain.DispatcherProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ConcurrentModule {

    @Binds
    internal abstract fun bindDispatcherProvider(provider: DefaultDispatcherProvider): DispatcherProvider
}
