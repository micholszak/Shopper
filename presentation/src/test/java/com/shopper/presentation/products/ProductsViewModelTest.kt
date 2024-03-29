package com.shopper.presentation.products

import com.shopper.domain.interactor.GetProducts
import com.shopper.domain.model.Product
import com.shopper.presentation.products.model.ProductView
import com.shopper.presentation.products.model.ProductViewMapper
import com.shopper.presentation.products.model.ProductsState
import com.shopper.test.utils.TestDispatcherProvider
import com.shopper.test.utils.extension.InstantTaskExecutorExtension
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.orbitmvi.orbit.test

@ExtendWith(InstantTaskExecutorExtension::class)
class ProductsViewModelTest {

    companion object {
        private const val DEFAULT_SIZE = 5
    }

    private val mockGetProducts: GetProducts = mock()
    private val dispatcherProvider = TestDispatcherProvider()

    private lateinit var systemUnderTest: ProductsViewModel

    @BeforeEach
    fun setup() {
        systemUnderTest = ProductsViewModel(
            getProducts = mockGetProducts,
            productViewMapper = ProductViewMapper(),
            dispatcherProvider = dispatcherProvider
        )
    }

    @Test
    fun `Start requesting for updates during initialisation`() {
        runTest {
            givenThatGetProductsReturnsWith(flowOf(emptyList()))
            val initialState = ProductsState()
            systemUnderTest.test(initialState = initialState)
                .runOnCreate()
                .assert(initialState)

            verify(mockGetProducts).execute()
        }
    }

    @Test
    fun `Update the list with new values`() {
        runTest {
            val firstTasks = buildProducts()
            givenThatGetProductsReturnsWith(flowOf(firstTasks))

            val initialState = ProductsState()
            systemUnderTest.test(initialState = initialState)
                .runOnCreate()
                .assert(initialState) {
                    states(
                        { ProductsState(products = createTaskViewItems()) }
                    )
                }
        }
    }

    @Test
    fun `Update state after database update`() {
        runTest {
            givenThatGetProductsReturnsWith(
                flowOf(
                    buildProducts(5),
                    buildProducts(6),
                    buildProducts(7)
                )
            )

            val initialState = ProductsState()
            systemUnderTest.test(initialState = initialState)
                .runOnCreate()
                .assert(initialState) {
                    states(
                        { ProductsState(products = createTaskViewItems(5)) },
                        { ProductsState(products = createTaskViewItems(6)) },
                        { ProductsState(products = createTaskViewItems(7)) }
                    )
                }
        }
    }

    private fun givenThatGetProductsReturnsWith(flow: Flow<List<Product>>) {
        whenever(mockGetProducts.execute()).doReturn(flow)
    }

    private fun buildProducts(size: Int = DEFAULT_SIZE): List<Product> =
        List(size) { index ->
            Product("$index")
        }

    private fun createTaskViewItems(size: Int = DEFAULT_SIZE): List<ProductView> =
        List(size) { index ->
            ProductView(name = "$index")
        }
}
