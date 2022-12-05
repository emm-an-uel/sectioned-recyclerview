package com.example.sectionedrecyclerview

import androidx.lifecycle.ViewModel
import java.util.*
import kotlin.collections.ArrayList

class ViewModel: ViewModel() {
    private lateinit var list: ArrayList<PojoOfJsonArray>
    private lateinit var list1: ArrayList<PojoOfJsonArray>
    private lateinit var list2: ArrayList<PojoOfJsonArray>

    private lateinit var consolidatedList1: ArrayList<ListItem>
    private lateinit var consolidatedList2: ArrayList<ListItem>

    fun createData() {
        list1 = arrayListOf()
        list2 = arrayListOf()

        val c1 = Calendar.getInstance()
        c1.set(2017, 3, 23)
        val c2 = Calendar.getInstance()
        c2.set(2017, 3, 27)
        val c3 = Calendar.getInstance()
        c3.set(2018, 4, 2)

        list = arrayListOf()
        list.apply {
            add(PojoOfJsonArray("name 1", c1, "20170323", 1))
            add(PojoOfJsonArray("name 3", c1, "20170323", 1))
            add(PojoOfJsonArray("name 3", c2, "20170327", 1))
            add(PojoOfJsonArray("name 3", c2, "20170327", 1))
            add(PojoOfJsonArray("name 3", c2, "20170327", 1))
            add(PojoOfJsonArray("name 3", c1, "20170323", 2))
            add(PojoOfJsonArray("name 2", c3, "20180402", 2))
            add(PojoOfJsonArray("name 3", c2, "20170327", 2))
        }

        for (i in list) { // sort into list 1 or 2
            if (i.num == 1) {
                list1.add(i)
            } else {
                list2.add(i)
            }
        }

        list1.sortBy { it.date }
        list2.sortBy { it.date }

        // create sectioned list to be passed into respective rv's
        val groupedMap1: Map<String, List<PojoOfJsonArray>> = list1.groupBy {
            it.dateString
        } // creates a map of 'date' to a 'list of PojoOfJsonArray' - eg: key '20160605', value is a list containing 'name 2', 'name 3'
        consolidatedList1 = arrayListOf()
        for (date: String in groupedMap1.keys) {
            consolidatedList1.add(DateItem(date)) // creates a DateItem class for each 'date' in groupedMap
            val groupItems: List<PojoOfJsonArray>? = groupedMap1[date] // groupItems is a list of PojoOfJsonArray which corresponds to the above 'date'
            groupItems?.forEach {
                consolidatedList1.add(GeneralItem(it.name)) // creates a GeneralItem class for each 'name' in above list
            }
        }

        val groupedMap2: Map<String, List<PojoOfJsonArray>> = list2.groupBy { it.dateString }
        consolidatedList2 = arrayListOf()
        for (date: String in groupedMap2.keys) {
            consolidatedList2.add(DateItem(date))
            val groupItems = groupedMap2[date]
            groupItems!!.forEach {
                consolidatedList2.add(GeneralItem(it.name))
            }
        }
    }

    fun getList1(): ArrayList<ListItem> {
        return consolidatedList1
    }

    fun getList2(): ArrayList<ListItem> {
        return consolidatedList2
    }
}