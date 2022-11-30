package com.example.sectionedrecyclerview

import android.app.LauncherActivity
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
            PojoOfJsonArray("name 1", "2016-06-21"),
            PojoOfJsonArray("name 2", "2016-06-05"),
            PojoOfJsonArray("name 2", "2016-06-05"),
            PojoOfJsonArray("name 3", "2016-05-17"),
            PojoOfJsonArray("name 3", "2016-05-17"),
            PojoOfJsonArray("name 3", "2016-05-17"),
            PojoOfJsonArray("name 3", "2016-05-17"),
            PojoOfJsonArray("name 2", "2016-06-05"),
            PojoOfJsonArray("name 3", "2016-05-17")
        )

        val groupedMapMap: Map<String, List<PojoOfJsonArray>> = myOptions.groupBy {
            it.date
        }

        val consolidatedList = mutableListOf<ListItem>()
        for (date:String in groupedMapMap.keys) {
            consolidatedList.add(DateItem(date))
            val groupItems: List<PojoOfJsonArray>? = groupedMapMap[date]
            groupItems?.forEach {
                consolidatedList.add(GeneralItem(it.name))
            }
        }

        val adapter = Adapter(consolidatedList)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        list.layoutManager = layoutManager
        list.adapter = adapter
    }
}