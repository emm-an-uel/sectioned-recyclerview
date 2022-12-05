package com.example.sectionedrecyclerview

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class Fragment2 : Fragment() {
    lateinit var rv: RecyclerView
    lateinit var adapter: Adapter

    lateinit var viewModel: ViewModel

    lateinit var consolidatedList2: ArrayList<ListItem>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(requireActivity())[ViewModel::class.java]
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        consolidatedList2 = viewModel.getList2() // get list from viewModel

        // setup recyclerview
        adapter = Adapter(consolidatedList2)
        rv = view.findViewById(R.id.rv)
        rv.adapter = adapter
        swipeFunctions()
    }

    private fun swipeFunctions() {
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun getSwipeDirs (recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
                if (viewHolder is Adapter.DateViewHolder) return 0 // prevents DateViewHolders from getting swiped
                return super.getSwipeDirs(recyclerView, viewHolder)
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val pos = viewHolder.adapterPosition
                consolidatedList2.removeAt(viewHolder.adapterPosition)
                adapter.notifyItemRemoved(viewHolder.adapterPosition)
                checkForDoubleDate(pos)
            }

        }).attachToRecyclerView(rv)
    }

    private fun checkForDoubleDate(removedIndex: Int) {
        if (removedIndex < consolidatedList2.size) {
            if (consolidatedList2[removedIndex].type == ListItem.TYPE_DATE) {
                if (consolidatedList2[removedIndex-1].type == ListItem.TYPE_DATE) {
                    // if both a) the item which has replaced the one just removed, and b) the previous item are TYPE_DATE
                    consolidatedList2.removeAt(removedIndex-1) // remove the double date
                    adapter.notifyItemRemoved(removedIndex-1)
                }
            }
        } else { // if the item removed was the last item in the list
            if (consolidatedList2[removedIndex-1].type == ListItem.TYPE_DATE) {
                consolidatedList2.removeAt(removedIndex-1)
                adapter.notifyItemRemoved(removedIndex-1)
            }
        }
    }
}