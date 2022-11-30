package com.example.sectionedrecyclerview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var list: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        list = findViewById(R.id.list)

        val c1 = Calendar.getInstance()
        c1.set(2017, 3, 23)
        val c2 = Calendar.getInstance()
        c2.set(2017, 3, 27)
        val c3 = Calendar.getInstance()
        c3.set(2018, 4, 2)

        val myOptions = listOf(
            PojoOfJsonArray("name 1", c1, "20170323"),
            PojoOfJsonArray("name 2", c1, "20170323"),
            PojoOfJsonArray("name 3", c1, "20170323"),
            PojoOfJsonArray("name 3", c2, "20170327"),
            PojoOfJsonArray("name 3", c2, "20170327"),
            PojoOfJsonArray("name 3", c2, "20170327"),
            PojoOfJsonArray("name 3", c1, "20170323"),
            PojoOfJsonArray("name 2", c3, "20180402"),
            PojoOfJsonArray("name 3", c2, "20170327")
        ).sortedBy { it.date } // sort by date (ascending)

        val groupedMap: Map<String, List<PojoOfJsonArray>> = myOptions.groupBy {
            it.dateString
        } // creates a map of 'date' to a 'list of PojoOfJsonArray' - eg: key '20160605', value is a list containing 'name 2', 'name 3'

        val consolidatedList = mutableListOf<ListItem>()
        for (date:String in groupedMap.keys) {
            consolidatedList.add(DateItem(date)) // creates a DateItem class for each 'date' in groupedMap
            val groupItems: List<PojoOfJsonArray>? = groupedMap[date] // groupItems is a list of PojoOfJsonArray which corresponds to the above 'date'
            groupItems?.forEach {
                consolidatedList.add(GeneralItem(it.name)) // creates a GeneralItem class for each 'name' in above list
            }
        }

        val adapter = Adapter(consolidatedList)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        list.layoutManager = layoutManager
        list.adapter = adapter
    }
}