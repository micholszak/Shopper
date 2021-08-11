package com.shopper.presentation.addition

import androidx.lifecycle.ViewModel
import com.shopper.domain.DispatcherProvider
import com.shopper.domain.interactor.AddProduct
import com.shopper.domain.model.AddProductResult
import com.shopper.domain.model.Product
import com.shopper.presentation.addition.model.AddProductEffect
import com.shopper.presentation.addition.model.AddProductState
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class AddProductViewModel @Inject constructor(
    private val addProduct: AddProduct,
    dispatcherProvider: DispatcherProvider,
) : ViewModel(), ContainerHost<AddProductState, AddProductEffect> {

    override val container: Container<AddProductState, AddProductEffect> =
        container(
            initialState = AddProductState.Idle,
            settings = Container.Settings(
                backgroundDispatcher = dispatcherProvider.io,
                orbitDispatcher = dispatcherProvider.default,
            )
        )

    fun addProductWith(name: String) = intent {
        reduce { AddProductState.Pending }
        val product = Product(name)
        when (addProduct.execute(product)) {
            is AddProductResult.Success -> {
                reduce { AddProductState.Added }
            }
            is AddProductResult.Failure -> {
                postSideEffect(AddProductEffect.EmptyFieldError)
                reduce { AddProductState.Idle }
            }
        }
    }
}
