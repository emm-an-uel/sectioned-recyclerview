package com.example.sectionedrecyclerview

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class Fragment2 : Fragment() {
    lateinit var rv: RecyclerView
    lateinit var adapter: Adapter

    lateinit var viewModel: ViewModel

    lateinit var consolidatedList2: ArrayList<ListItem>
    lateinit var list2: ArrayList<PojoOfJsonArray>
    lateinit var mapOfIndex: MutableMap<Int, Int>

    lateinit var linearLayoutIndex: LinearLayout
    lateinit var linearLayoutList: LinearLayout

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

        // setup debugging linearLayouts
        linearLayoutIndex = view.findViewById(R.id.linearLayoutIndex)
        linearLayoutList = view.findViewById(R.id.linearLayoutList)

        // get data lists and map of <position, actualIndex>
        list2 = viewModel.getList2()
        consolidatedList2 = viewModel.getConsolidatedList2() // get list from viewModel
        createMapOfIndex()

        // setup recyclerview
        adapter = Adapter(consolidatedList2)
        rv = view.findViewById(R.id.rv)
        rv.adapter = adapter
        swipeFunctions()
    }

    private fun createMapOfIndex() {
        mapOfIndex = mutableMapOf()
        var index = 0
        for (n in 0 until consolidatedList2.size) {
            if (consolidatedList2[n].type == ListItem.TYPE_GENERAL) {
                mapOfIndex[n] = index
                index++
            }
        }
        populateLinearLayoutIndex()
        populateLinearLayoutList()
    }

    private fun populateLinearLayoutIndex() {
        linearLayoutIndex.removeAllViews()
        for (p in mapOfIndex.keys) {
            val i = mapOfIndex[p]
            val textView = TextView(context)
            textView.text = "$p - $i"
            linearLayoutIndex.addView(textView)
        }
    }

    private fun populateLinearLayoutList() {
        linearLayoutList.removeAllViews()
        for (item in list2) {
            val name = item.name
            val tv = TextView(context)
            tv.text = name
            linearLayoutList.addView(tv)
        }
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
                val actualIndex = mapOfIndex[pos]!!
                val item: PojoOfJsonArray = list2[actualIndex]
                viewModel.updateList1(item, actualIndex)
                adapter.notifyItemRemoved(viewHolder.adapterPosition)
                updateMap(pos, true)

                checkForDoubleDate(pos)
            }

        }).attachToRecyclerView(rv)
    }

    private fun updateMap(pos: Int, indexChanged: Boolean) {
        mapOfIndex.remove(pos) // remove the key-value pair of the swiped item

        // adjust following key-value pairs
        if (indexChanged) { // GeneralItem got removed
            for (p in pos+1 until consolidatedList2.size+1) {
                if (mapOfIndex.containsKey(p)) { // if it doesn't contain, that means the item at p is a DateItem
                    val oldValue = mapOfIndex[p]!!
                    mapOfIndex.remove(p)
                    mapOfIndex[p-1] = oldValue-1
                }
            }
        } else { // DateItem got removed
            for (p in pos+1 until consolidatedList2.size+1) {
                if (mapOfIndex.containsKey(p)) {
                    val value = mapOfIndex[p]!!
                    mapOfIndex.remove(p)
                    mapOfIndex[p-1] = value
                }
            }
        }
        populateLinearLayoutIndex()
        populateLinearLayoutList()
    }

    private fun checkForDoubleDate(removedIndex: Int) {
        if (removedIndex < consolidatedList2.size) {
            if (consolidatedList2[removedIndex].type == ListItem.TYPE_DATE) {
                if (consolidatedList2[removedIndex-1].type == ListItem.TYPE_DATE) {
                    // if both a) the item which has replaced the one just removed, and b) the previous item are TYPE_DATE
                    consolidatedList2.removeAt(removedIndex-1) // remove the double date
                    adapter.notifyItemRemoved(removedIndex-1)
                    updateMap(removedIndex-1, false)
                }
            }
        } else { // if the item removed was the last item in the list
            if (consolidatedList2[removedIndex-1].type == ListItem.TYPE_DATE) {
                consolidatedList2.removeAt(removedIndex-1)
                adapter.notifyItemRemoved(removedIndex-1)
                updateMap(removedIndex-1, false)
            }
        }
    }
}