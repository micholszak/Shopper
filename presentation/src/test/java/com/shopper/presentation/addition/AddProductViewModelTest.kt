package com.shopper.presentation.addition

import com.shopper.domain.interactor.AddProduct
import com.shopper.domain.model.AddProductResult
import com.shopper.presentation.addition.model.AddProductEffect
import com.shopper.presentation.addition.model.AddProductState
import com.shopper.presentation.extension.InstantTaskExecutorExtension
import com.shopper.test.utils.TestDispatcherProvider
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.orbitmvi.orbit.assert
import org.orbitmvi.orbit.test

@ExtendWith(InstantTaskExecutorExtension::class)
class AddProductViewModelTest {

    private val dispatcherProvider = TestDispatcherProvider()
    private val mockAddProduct: AddProduct = mock()

    private lateinit var systemUnderTest: AddProductViewModel

    @BeforeEach
    fun setup() {
        systemUnderTest = AddProductViewModel(
            addProduct = mockAddProduct,
            dispatcherProvider = dispatcherProvider
        )
    }

    @Test
    fun `Start with initial state`() {
        val initialState = AddProductState.Idle
        systemUnderTest.test(AddProductState.Idle)

        systemUnderTest.assert(initialState)
    }

    @Test
    fun `Emit error event given that adding product fails`() = runBlockingTest {
        givenAddProductsReturnsWith(result = AddProductResult.Failure("empty field"))

        val initialState = AddProductState.Idle
        systemUnderTest.test(initialState)
        systemUnderTest.addProductWith(name = "")
        systemUnderTest.assert(initialState) {
            states(
                { AddProductState.Pending },
                { AddProductState.Idle }
            )
            postedSideEffects(
                AddProductEffect.EmptyFieldError,
            )
        }
    }

    @Test
    fun `Indicate product added to view`() = runBlockingTest {
        givenAddProductsReturnsWith(result = AddProductResult.Success)
        val initialState = AddProductState.Idle
        systemUnderTest.test(initialState)
        systemUnderTest.addProductWith("some name")
        systemUnderTest.assert(initialState) {
            states(
                { AddProductState.Pending },
                { AddProductState.Added }
            )
        }
    }

    private suspend fun givenAddProductsReturnsWith(result: AddProductResult) {
        whenever(mockAddProduct.execute(any())).doReturn(result)
    }
}
