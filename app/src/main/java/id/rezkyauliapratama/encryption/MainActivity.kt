package id.rezkyauliapratama.encryption

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val adapter = MainAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        btnAdd.setOnClickListener {
            if (etPlainText.text.isNotEmpty()) {
                val item = Item(etPlainText.text.toString())
                adapter.add(item)
            }
        }
    }
}
