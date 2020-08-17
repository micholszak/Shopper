package pl.olszak.todo.feature.todos.adapter

import android.widget.TextView
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegate
import pl.olszak.todo.R
import pl.olszak.todo.core.adapter.AdapterItem

fun createTaskDelegate() =
    adapterDelegate<TaskViewItem, AdapterItem>(R.layout.view_holder_task) {
        val title: TextView = itemView.findViewById(R.id.title)
        val description: TextView = itemView.findViewById(R.id.description)

        bind {
            title.text = item.title
            description.text = item.description
        }
    }
