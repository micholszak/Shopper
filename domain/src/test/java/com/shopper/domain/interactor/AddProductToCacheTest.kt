package com.shopper.domain.interactor

import com.shopper.cache.ProductCache
import com.shopper.domain.model.AddProductResult
import com.shopper.domain.model.Product
import com.shopper.test.utils.TestDispatcherProvider
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock

internal class AddProductToCacheTest {

    private val dispatcherProvider = TestDispatcherProvider()
    private val mockProductCache: ProductCache = mock()

    private val addProduct = AddProductToCache(
        productCache = mockProductCache,
        dispatcherProvider = dispatcherProvider
    )

    @Test
    fun `End with failure when the product name is empty`() {
        runBlocking {
            val product = Product("")

            val result = addProduct.execute(product)

            val expectedMessage = "The name of the product cannot be empty"
            assertThat(result).isEqualTo(AddProductResult.Failure(expectedMessage))
        }
    }

    @Test
    fun `Successfully add product to cache`() {
        runBlocking {
            val product = Product("some name")

            val result = addProduct.execute(product)

            assertThat(result).isEqualTo(AddProductResult.Success)
        }
    }
}
