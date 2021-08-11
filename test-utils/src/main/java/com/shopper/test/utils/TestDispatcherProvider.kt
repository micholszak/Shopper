package com.shopper.test.utils

import com.shopper.domain.DispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineDispatcher

class TestDispatcherProvider(
    dispatcher: CoroutineDispatcher = TestCoroutineDispatcher()
) : DispatcherProvider {

    override val io: CoroutineDispatcher = dispatcher
    override val default: CoroutineDispatcher = dispatcher
    override val main: CoroutineDispatcher = dispatcher
    override val unconfined: CoroutineDispatcher = dispatcher
}