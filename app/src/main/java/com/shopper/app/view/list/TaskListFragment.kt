package com.shopper.app.view.list

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.shopper.app.R
import com.shopper.app.databinding.FragmentTaskListBinding
import com.shopper.app.view.common.ScaleAnimation
import com.shopper.app.view.common.adapter.ItemAdapter
import com.shopper.app.view.common.binding.viewBinding
import com.shopper.app.view.list.adapter.createTaskDelegate
import com.shopper.app.view.list.model.TaskViewItem
import com.shopper.presentation.products.ProductsViewModel
import com.shopper.presentation.products.model.ProductsState
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.recyclerview.animators.FadeInUpAnimator
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class TaskListFragment : Fragment(R.layout.fragment_task_list) {

    private var listAdapter: ItemAdapter? = null
    private val binding: FragmentTaskListBinding by viewBinding(FragmentTaskListBinding::bind)

    private val viewModel: ProductsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.inflateMenu(R.menu.menu_todo_list)
        setupList()
        binding.addTask.setOnClickListener {
            findNavController().navigate(R.id.action_add_task)
        }
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.container.stateFlow.collect(::render)
        }
    }

    private fun setupList() {
        listAdapter = ItemAdapter(
            createTaskDelegate(),
            animation = ScaleAnimation()
        )
        binding.list.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = listAdapter
            itemAnimator = FadeInUpAnimator().apply {
                addDuration = 200
            }
        }
    }

    private fun render(state: ProductsState) {
        listAdapter?.items = state.products.map { productView ->
            TaskViewItem(title = productView.name)
        }
    }
}
