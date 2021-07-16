package com.shopper.app.presentation.list

import com.shopper.app.InstantTaskExecutorExtension
import com.shopper.app.presentation.list.model.TasksViewState
import com.shopper.app.view.list.model.TaskViewItem
import com.shopper.domain.interactor.GetProducts
import com.shopper.domain.model.Product
import com.shopper.domain.test.TestDispatcherProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.orbitmvi.orbit.assert
import org.orbitmvi.orbit.test

@ExtendWith(InstantTaskExecutorExtension::class)
class TasksViewModelTest {

    companion object {
        private const val DEFAULT_SIZE = 5
    }

    private val mockGetTasks: GetProducts = mock()
    private val dispatcherProvider = TestDispatcherProvider()

    @Test
    fun `Start requesting for database updates during initialisation`() {
        givenThatGetProductsReturns(flowOf(emptyList()))
        val initialState = TasksViewState()
        val viewModel = TasksViewModel(mockGetTasks, dispatcherProvider).test(
            initialState = initialState,
            runOnCreate = true
        )

        viewModel.assert(initialState)
        verify(mockGetTasks).execute()
    }

    @Test
    fun `Update the list with new values`() {
        val firstTasks = createProduct()
        givenThatGetProductsReturns(flowOf(firstTasks))

        val initialState = TasksViewState()
        val viewModel = TasksViewModel(mockGetTasks, dispatcherProvider).test(
            initialState = initialState,
            runOnCreate = true
        )
        viewModel.assert(initialState) {
            states(
                { TasksViewState(tasks = createTaskViewItems()) }
            )
        }
    }

    @Test
    fun `Update state after database update`() {
        givenThatGetProductsReturns(
            with = flowOf(
                createProduct(5),
                createProduct(6),
                createProduct(7)
            )
        )

        val initialState = TasksViewState()
        val viewModel = TasksViewModel(mockGetTasks, dispatcherProvider).test(
            initialState = initialState,
            runOnCreate = true
        )
        viewModel.assert(initialState) {
            states(
                { TasksViewState(tasks = createTaskViewItems(5)) },
                { TasksViewState(tasks = createTaskViewItems(6)) },
                { TasksViewState(tasks = createTaskViewItems(7)) }
            )
        }
    }

    private fun givenThatGetProductsReturns(with: Flow<List<Product>>) {
        whenever(mockGetTasks.execute()).doReturn(with)
    }

    private fun createProduct(size: Int = DEFAULT_SIZE): List<Product> =
        List(size) { index ->
            Product("$index")
        }

    private fun createTaskViewItems(size: Int = DEFAULT_SIZE): List<TaskViewItem> =
        List(size) { index ->
            TaskViewItem(title = "$index")
        }
}
