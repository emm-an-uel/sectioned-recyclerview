package com.example.sectionedrecyclerview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    lateinit var list: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        list = findViewById(R.id.list)

        val myOptions = listOf(
            PojoOfJsonArray("name 1", "20170621"),
            PojoOfJsonArray("name 2", "20160605"),
            PojoOfJsonArray("name 3", "20160605"),
            PojoOfJsonArray("name 3", "20150517"),
            PojoOfJsonArray("name 3", "20150517"),
            PojoOfJsonArray("name 3", "20160517"),
            PojoOfJsonArray("name 3", "20160517"),
            PojoOfJsonArray("name 2", "20130605"),
            PojoOfJsonArray("name 3", "20160517")
        ).sortedBy { it.date }

        val groupedMap: Map<String, List<PojoOfJsonArray>> = myOptions.groupBy {
            it.date
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