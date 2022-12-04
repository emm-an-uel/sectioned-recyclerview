package com.example.sectionedrecyclerview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    lateinit var rv: RecyclerView
    lateinit var list: ArrayList<PojoOfJsonArray>
    lateinit var consolidatedList: ArrayList<ListItem>
    lateinit var adapter: Adapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rv = findViewById(R.id.rv)

        val c1 = Calendar.getInstance()
        c1.set(2017, 3, 23)
        val c2 = Calendar.getInstance()
        c2.set(2017, 3, 27)
        val c3 = Calendar.getInstance()
        c3.set(2018, 4, 2)

        list = arrayListOf()
        list.apply {
            add(PojoOfJsonArray("name 1", c1, "20170323"))
            add(PojoOfJsonArray("name 3", c1, "20170323"))
            add(PojoOfJsonArray("name 3", c2, "20170327"))
            add(PojoOfJsonArray("name 3", c2, "20170327"))
            add(PojoOfJsonArray("name 3", c2, "20170327"))
            add(PojoOfJsonArray("name 3", c1, "20170323"))
            add(PojoOfJsonArray("name 2", c3, "20180402"))
            add(PojoOfJsonArray("name 3", c2, "20170327"))
        }
        list.sortBy { it.date }

        val groupedMap: Map<String, List<PojoOfJsonArray>> = list.groupBy {
            it.dateString
        } // creates a map of 'date' to a 'list of PojoOfJsonArray' - eg: key '20160605', value is a list containing 'name 2', 'name 3'

        consolidatedList = arrayListOf()
        for (date:String in groupedMap.keys) {
            consolidatedList.add(DateItem(date)) // creates a DateItem class for each 'date' in groupedMap
            val groupItems: List<PojoOfJsonArray>? = groupedMap[date] // groupItems is a list of PojoOfJsonArray which corresponds to the above 'date'
            groupItems?.forEach {
                consolidatedList.add(GeneralItem(it.name)) // creates a GeneralItem class for each 'name' in above list
            }
        }

        adapter = Adapter(consolidatedList)
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
                consolidatedList.removeAt(viewHolder.adapterPosition)
                adapter.notifyItemRemoved(viewHolder.adapterPosition)
                checkForDoubleDate(pos)
            }

        }).attachToRecyclerView(rv)
    }

    private fun checkForDoubleDate(removedIndex: Int) {
        if (removedIndex < consolidatedList.size) {
            if (consolidatedList[removedIndex].type == ListItem.TYPE_DATE) {
                if (consolidatedList[removedIndex-1].type == ListItem.TYPE_DATE) {
                    // if both a) the item which has replaced the one just removed, and b) the previous item are TYPE_DATE
                    consolidatedList.removeAt(removedIndex-1) // remove the double date
                    adapter.notifyItemRemoved(removedIndex-1)
                }
            }
        } else { // if the item removed was the last item in the list
            if (consolidatedList[removedIndex-1].type == ListItem.TYPE_DATE) {
                consolidatedList.removeAt(removedIndex-1)
                adapter.notifyItemRemoved(removedIndex-1)
            }
        }
    }
}