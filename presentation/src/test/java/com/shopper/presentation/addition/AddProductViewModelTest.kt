package com.shopper.presentation.addition

import com.shopper.domain.interactor.AddProduct
import com.shopper.domain.model.AddProductResult
import com.shopper.presentation.addition.model.AddProductEffect
import com.shopper.presentation.addition.model.AddProductState.Added
import com.shopper.presentation.addition.model.AddProductState.Error
import com.shopper.presentation.addition.model.AddProductState.ErrorType
import com.shopper.presentation.addition.model.AddProductState.Idle
import com.shopper.presentation.addition.model.AddProductState.Pending
import com.shopper.test.utils.TestDispatcherProvider
import com.shopper.test.utils.extension.InstantTaskExecutorExtension
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
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
        val initialState = Idle
        systemUnderTest.test(initialState)
            .assert(initialState)
    }

    @Test
    fun `Emit error event given that adding product fails`() {
        runTest {
            givenAddProductsReturnsWith(result = AddProductResult.Failure("empty field"))

            val initialState = Idle

            val testContainer = systemUnderTest.test(initialState)
            systemUnderTest.addProductWith(name = "")

            testContainer.runOnCreate()
                .assert(initialState) {
                    states(
                        { Pending },
                        { Error(ErrorType.EMPTY_FIELD) }
                    )
                    postedSideEffects(
                        AddProductEffect.EmptyFieldError
                    )
                }
        }
    }

    @Test
    fun `Indicate product added to view`() {
        runTest {
            givenAddProductsReturnsWith(result = AddProductResult.Success)
            val initialState = Idle

            val testContainer = systemUnderTest.test(initialState)
            systemUnderTest.addProductWith("some name")

            testContainer.runOnCreate()
                .assert(initialState) {
                    states(
                        { Pending },
                        { Added }
                    )
                }
        }
    }

    private suspend fun givenAddProductsReturnsWith(result: AddProductResult) {
        whenever(mockAddProduct.execute(any())).doReturn(result)
    }
}
