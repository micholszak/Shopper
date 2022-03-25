package com.shopper.presentation.addition

import androidx.lifecycle.ViewModel
import com.shopper.domain.DispatcherProvider
import com.shopper.domain.interactor.AddProduct
import com.shopper.domain.model.AddProductResult
import com.shopper.domain.model.Product
import com.shopper.presentation.addition.model.AddProductEffect
import com.shopper.presentation.addition.model.AddProductState
import com.shopper.presentation.addition.model.AddProductState.Added
import com.shopper.presentation.addition.model.AddProductState.Error
import com.shopper.presentation.addition.model.AddProductState.ErrorType.EMPTY_FIELD
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
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
                intentDispatcher = dispatcherProvider.io,
            )
        )

    fun addProductWith(name: String) = intent {
        flow {
            emit(AddProductState.Pending)
            val product = Product(name)
            val nextState = when (addProduct.execute(product)) {
                is AddProductResult.Success -> Added
                is AddProductResult.Failure -> Error(EMPTY_FIELD)
            }
            emit(nextState)
        }.collect { state ->
            reduce { state }
            if (state is Error) postSideEffect(state.mapToSideEffect())
        }
    }
}
