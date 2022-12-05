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
import kotlinx.android.synthetic.main.fragment_1.*

class Fragment1 : Fragment() {
    lateinit var rv: RecyclerView
    lateinit var adapter: Adapter

    lateinit var viewModel: ViewModel

    lateinit var consolidatedList1: ArrayList<ListItem>
    lateinit var list1: ArrayList<PojoOfJsonArray>
    lateinit var mapOfIndex: MutableMap<Int, Int>

    lateinit var linearLayoutIndex: LinearLayout
    lateinit var linearLayoutList: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(requireActivity())[ViewModel::class.java]
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_1, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // setup debugging linearLayouts
        linearLayoutIndex = view.findViewById(R.id.linearLayoutIndex)
        linearLayoutList = view.findViewById(R.id.linearLayoutList)

        // get data lists and map of <position, actualIndex>
        list1 = viewModel.getList1()
        consolidatedList1 = viewModel.getConsolidatedList1() // get list from viewModel
        createMapOfIndex()

        // setup recyclerview
        adapter = Adapter(consolidatedList1)
        rv = view.findViewById(R.id.rv)
        rv.adapter = adapter
        swipeFunctions()
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

    private fun createMapOfIndex() {
        mapOfIndex = mutableMapOf()
        var index = 0
        for (n in 0 until consolidatedList1.size) {
            if (consolidatedList1[n].type == ListItem.TYPE_GENERAL) {
                mapOfIndex[n] = index
                index++
            }
        }
        populateLinearLayoutIndex()
        populateLinearLayoutList()
    }

    private fun populateLinearLayoutList() {
        linearLayoutList.removeAllViews()
        for (item in list1) {
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
                consolidatedList1.removeAt(viewHolder.adapterPosition)
                val actualIndex = mapOfIndex[pos]!!
                val item: PojoOfJsonArray = list1[actualIndex]
                viewModel.updateList2(item) // adds this item to list2
                list1.removeAt(actualIndex)
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
            for (p in pos+1 until consolidatedList1.size+1) {
                if (mapOfIndex.containsKey(p)) { // if it doesn't contain, that means the item at p is a DateItem
                    val oldValue = mapOfIndex[p]!!
                    mapOfIndex.remove(p)
                    mapOfIndex[p-1] = oldValue-1
                }
            }
        } else { // DateItem got removed
            for (p in pos+1 until consolidatedList1.size+1) {
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
        if (removedIndex < consolidatedList1.size) {
            if (consolidatedList1[removedIndex].type == ListItem.TYPE_DATE) {
                if (consolidatedList1[removedIndex-1].type == ListItem.TYPE_DATE) {
                    // if both a) the item which has replaced the one just removed, and b) the previous item are TYPE_DATE
                    consolidatedList1.removeAt(removedIndex-1) // remove the double date
                    adapter.notifyItemRemoved(removedIndex-1)
                    updateMap(removedIndex-1, false)
                }
            }
        } else { // if the item removed was the last item in the list
            if (consolidatedList1[removedIndex-1].type == ListItem.TYPE_DATE) {
                consolidatedList1.removeAt(removedIndex-1)
                adapter.notifyItemRemoved(removedIndex-1)
                updateMap(removedIndex-1, false)
            }
        }
    }
}