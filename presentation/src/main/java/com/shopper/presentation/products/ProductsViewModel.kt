package com.shopper.presentation.products

import androidx.lifecycle.ViewModel
import com.shopper.domain.DispatcherProvider
import com.shopper.domain.interactor.GetProducts
import com.shopper.domain.model.Product
import com.shopper.presentation.products.model.ProductView
import com.shopper.presentation.products.model.ProductsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val getProducts: GetProducts,
    dispatcherProvider: DispatcherProvider,
) : ViewModel(), ContainerHost<ProductsState, Unit> {

    override val container: Container<ProductsState, Unit> =
        container(
            initialState = ProductsState(),
            settings = Container.Settings(
                backgroundDispatcher = dispatcherProvider.io,
                orbitDispatcher = dispatcherProvider.default
            )
        ) {
            subscribeToProductUpdates()
        }

    private fun subscribeToProductUpdates() = intent {
        getProducts.execute()
            .map(::mapTasks)
            .collect { items ->
                reduce {
                    state.copy(products = items)
                }
            }
    }

    private fun mapTasks(products: List<Product>): List<ProductView> {
        return products.map { product ->
            ProductView(
                name = product.name,
            )
        }
    }
}
