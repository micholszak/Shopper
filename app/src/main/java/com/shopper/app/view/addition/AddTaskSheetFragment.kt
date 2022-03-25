package com.shopper.app.view.addition

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.shopper.app.R
import com.shopper.app.databinding.FragmentAddTaskSheetBinding
import com.shopper.app.view.common.binding.viewBinding
import com.shopper.app.view.common.hideSoftInputFromDialog
import com.shopper.app.view.common.showSoftInputInDialog
import com.shopper.app.view.common.toast
import com.shopper.presentation.addition.AddProductViewModel
import com.shopper.presentation.addition.model.AddProductEffect
import com.shopper.presentation.addition.model.AddProductState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddTaskSheetFragment : BottomSheetDialogFragment() {

    private val binding: FragmentAddTaskSheetBinding by viewBinding(FragmentAddTaskSheetBinding::bind)
    private val addProductViewModel: AddProductViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_add_task_sheet, container, false)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        dialog.behavior.skipCollapsed = true
        dialog.behavior.state = STATE_EXPANDED

        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.title.showSoftInputInDialog()
        binding.createButton.setOnClickListener {
            val text = binding.title.text?.toString().orEmpty()
            addProductViewModel.addProductWith(text)
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            addProductViewModel.container.stateFlow.collect(::render)
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            addProductViewModel.container.sideEffectFlow.collect(::displaySideEffect)
        }
    }

    private fun render(state: AddProductState) {
        when (state) {
            is AddProductState.Idle -> {
                binding.createButton.isClickable = true
            }
            is AddProductState.Pending -> {
                binding.createButton.isClickable = false
            }
            is AddProductState.Error -> {
                binding.createButton.isClickable = false
            }
            is AddProductState.Added -> {
                dismiss()
            }
        }
    }

    // todo extract proper error message, error should be cleared or moved to state handling
    private fun displaySideEffect(sideEffect: AddProductEffect) {
        when (sideEffect) {
            AddProductEffect.EmptyFieldError -> {
                toast("Title should not be empty")
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        binding.title.hideSoftInputFromDialog()
        super.onDismiss(dialog)
    }
}
