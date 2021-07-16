package com.shopper.app.presentation.addition

import com.shopper.app.InstantTaskExecutorExtension
import com.shopper.app.presentation.addition.model.AddTaskSideEffect
import com.shopper.app.presentation.addition.model.AddTaskViewState
import com.shopper.domain.interactor.AddProduct
import com.shopper.domain.model.AddProductResult
import com.shopper.domain.test.TestDispatcherProvider
import kotlinx.coroutines.test.runBlockingTest
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

    @Test
    fun `Start with initial state`() {
        val initialState = AddTaskViewState.Idle
        val viewModel = AddTaskViewModel(mockAddProduct, dispatcherProvider)
            .test(AddTaskViewState.Idle)

        viewModel.assert(initialState)
    }

    @Test
    fun `Emit error event given that adding task fails`() {
        runBlockingTest {
            givenAddProductsReturns(with = AddProductResult.Failure("empty field"))

            val initialState = AddTaskViewState.Idle
            val viewModel = AddTaskViewModel(mockAddProduct, dispatcherProvider)
                .test(initialState)
            viewModel.addProductWith(name = "")
            viewModel.assert(initialState) {
                states(
                    { AddTaskViewState.Pending },
                    { AddTaskViewState.Idle }
                )
                postedSideEffects(
                    AddTaskSideEffect.EmptyFieldError,
                )
            }
        }
    }

    @Test
    fun `Indicate task added to view`() {
        runBlockingTest {
            givenAddProductsReturns(with = AddProductResult.Success)
            val initialState = AddTaskViewState.Idle
            val viewModel = AddTaskViewModel(mockAddProduct, dispatcherProvider)
                .test(initialState)
            viewModel.addProductWith("some name")
            viewModel.assert(initialState) {
                states(
                    { AddTaskViewState.Pending },
                    { AddTaskViewState.Added }
                )
            }
        }
    }

    private suspend fun givenAddProductsReturns(with: AddProductResult) {
        whenever(mockAddProduct.execute(any())).doReturn(with)
    }
}
