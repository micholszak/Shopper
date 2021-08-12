package com.shopper.domain.interactor

import app.cash.turbine.test
import com.shopper.cache.ProductCache
import com.shopper.cache.model.CachedProduct
import com.shopper.domain.model.Product
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

internal class GetProductsFromCacheTest {

    private val mockProductCache: ProductCache = mock()
    private val getProductsFromCache = GetProductsFromCache(mockProductCache)

    @Test
    fun `Should map properly map products`() {
        val size = 10
        val items = List(size) { index ->
            CachedProduct(
                productId = index.toLong(),
                name = "$index",
                checked = index % 2 == 0
            )
        }
        whenever(mockProductCache.getAllProducts()).doReturn(flowOf(items))

        runBlocking {
            getProductsFromCache.execute().test {
                val expectedProducts = List(size) { index ->
                    Product(name = "$index")
                }
                assertThat(expectedProducts).isEqualTo(awaitItem())
                awaitComplete()
            }
        }
    }
}
