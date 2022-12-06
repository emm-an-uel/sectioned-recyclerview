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
        c1.set(2022, 12, 6) // today
        val c2 = Calendar.getInstance()
        c2.set(2022, 12, 7) // tomorrow
        val c3 = Calendar.getInstance()
        c3.set(2022, 11, 15) // overdue
        val c4 = Calendar.getInstance()
        c4.set(2023, 12, 2) // others
        val c5 = Calendar.getInstance()
        c5.set(2022, 12, 12) // next week

        list = arrayListOf()
        list.apply {
            add(PojoOfJsonArray("name A", c1, "20221206", 1))
            add(PojoOfJsonArray("name B", c1, "20221206", 1))
            add(PojoOfJsonArray("name C", c2, "20221207", 1))
            add(PojoOfJsonArray("name D", c2, "20221207", 1))
            add(PojoOfJsonArray("name E", c2, "20221207", 1))
            add(PojoOfJsonArray("name F", c1, "20221206", 1))
            add(PojoOfJsonArray("name G", c3, "20221115", 1))
            add(PojoOfJsonArray("name H", c4, "20231202", 1))
            add(PojoOfJsonArray("name I", c4, "20231202", 1))
            add(PojoOfJsonArray("name J", c5, "20221212", 1))
            add(PojoOfJsonArray("name K", c5, "20221212", 1))
            add(PojoOfJsonArray("name L", c5, "20221212", 1))
        }

        for (i in list) { // sort into list 1 or 2
            if (i.num == 1) {
                list1.add(i)
            } else {
                list2.add(i)
            }
        }

        createConsolidatedList1()
        createConsolidatedList2()
    }

    private fun createConsolidatedList1() {
        // TODO: sectioning works only sometimes - changes when I re-run the app
        val today = Calendar.getInstance()
        val actualMonth = today.get(Calendar.MONTH)+1
        today.set(today.get(Calendar.YEAR), actualMonth, today.get(Calendar.DAY_OF_MONTH)) // note that 'today' was instantiated as "2022 11 6" since months go from 0 to 11. this fixes that
        val todayString = dateToString(today)

        val tomorrow = Calendar.getInstance()
        tomorrow.set(Calendar.MONTH, tomorrow.get(Calendar.MONTH)+1)
        tomorrow.add(Calendar.DATE, 1) // adds a day to today's date

        val nextWeek = Calendar.getInstance()
        nextWeek.set(Calendar.MONTH, nextWeek.get(Calendar.MONTH)+1)
        nextWeek.add(Calendar.DATE, 7) // adds 7 days to today's date

        list1.sortBy { it.date }

        // headings will be - Overdue, Today, Tomorrow, Next Week, Others
        var overdueHeader = false
        var todayHeader = false
        var tomorrowHeader = false
        var nextWeekHeader = false
        var othersHeader = false // these will be set to true as headers are added into consolidatedLists

        // create sectioned list to be passed into respective rv's
        val groupedMap1: Map<Calendar, List<PojoOfJsonArray>> = list1.groupBy {
            it.date
        } // creates a map of 'date' to a 'list of PojoOfJsonArray' - eg: key '20160605', value is a list containing 'name 2', 'name 3'
        consolidatedList1 = arrayListOf()
        for (date: Calendar in groupedMap1.keys) {
            if (date < today) {
                if (!overdueHeader) {
                    consolidatedList1.add(DateItem("Overdue")) // adds a header if one doesn't already exist
                    overdueHeader = true
                }
                val groupItems: List<PojoOfJsonArray>? = groupedMap1[date] // groupItems is a list of PojoOfJsonArray which corresponds to the above 'date'
                groupItems?.forEach {
                    consolidatedList1.add(GeneralItem(it.name, it.dateString)) // creates a GeneralItem class for each 'name' in above list
                }

            } else if (date == today) {
                if (!todayHeader) {
                    consolidatedList1.add(DateItem("Due Today")) // adds a header if one doesn't already exist
                    todayHeader = true
                }
                val groupItems: List<PojoOfJsonArray>? = groupedMap1[date] // groupItems is a list of PojoOfJsonArray which corresponds to the above 'date'
                groupItems?.forEach {
                    consolidatedList1.add(GeneralItem(it.name, it.dateString)) // creates a GeneralItem class for each 'name' in above list
                }

            } else if (date == tomorrow) {
                if (!tomorrowHeader) {
                    consolidatedList1.add(DateItem("Due Tomorrow")) // adds a header if one doesn't already exist
                    tomorrowHeader = true
                }
                val groupItems: List<PojoOfJsonArray>? = groupedMap1[date] // groupItems is a list of PojoOfJsonArray which corresponds to the above 'date'
                groupItems?.forEach {
                    consolidatedList1.add(GeneralItem(it.name, it.dateString)) // creates a GeneralItem class for each 'name' in above list
                }

            } else if (date < nextWeek) {
                if (!nextWeekHeader) {
                    consolidatedList1.add(DateItem("Due Next Week")) // adds a header if one doesn't already exist
                    nextWeekHeader = true
                }
                val groupItems: List<PojoOfJsonArray>? = groupedMap1[date] // groupItems is a list of PojoOfJsonArray which corresponds to the above 'date'
                groupItems?.forEach {
                    consolidatedList1.add(GeneralItem(it.name, it.dateString)) // creates a GeneralItem class for each 'name' in above list
                }

            } else {
                if (!othersHeader) {
                    consolidatedList1.add(DateItem("Others")) // adds a header if one doesn't already exist
                    othersHeader = true
                }
                val groupItems: List<PojoOfJsonArray>? = groupedMap1[date] // groupItems is a list of PojoOfJsonArray which corresponds to the above 'date'
                groupItems?.forEach {
                    consolidatedList1.add(GeneralItem(it.name, it.dateString)) // creates a GeneralItem class for each 'name' in above list
                }
            }
        }
    }

    private fun createConsolidatedList2() {
        val groupedMap2: Map<String, List<PojoOfJsonArray>> = list2.groupBy { it.dateString }
        consolidatedList2 = arrayListOf()
        for (date: String in groupedMap2.keys) {
            consolidatedList2.add(DateItem(date))
            val groupItems = groupedMap2[date]
            groupItems!!.forEach {
                consolidatedList2.add(GeneralItem(it.name, it.dateString))
            }
        }
    }

    fun getConsolidatedList1(): ArrayList<ListItem> {
        return consolidatedList1
    }

    fun getConsolidatedList2(): ArrayList<ListItem> {
        return consolidatedList2
    }

    fun getList1(): ArrayList<PojoOfJsonArray> {
        return list1
    }

    fun getList2(): ArrayList<PojoOfJsonArray> {
        return list2
    }

    fun updateList1(item: PojoOfJsonArray, actualIndex: Int) { // this method is called when an item is swiped from list2 to list1
        item.num = 1
        list1.add(item)
        list2.removeAt(actualIndex)
        saveLists()
        createConsolidatedList1()
    }

    fun updateList2(item: PojoOfJsonArray, actualIndex: Int) {
        item.num = 2
        list2.add(item)
        list1.removeAt(actualIndex)
        saveLists()
        createConsolidatedList2()
    }

    private fun saveLists() {
        list.apply {
            clear()
            addAll(list1)
            addAll(list2)
        }
        // in an actual app, this would then be saved in a json file
    }

    private fun dateToString(date: Calendar): String {
        val year = date.get(Calendar.YEAR)
        val month = date.get(Calendar.MONTH)
        val day = date.get(Calendar.DAY_OF_MONTH)
        return "$year $month $day"
    }
}