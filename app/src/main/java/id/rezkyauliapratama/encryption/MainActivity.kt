package id.rezkyauliapratama.encryption

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import java.nio.charset.Charset

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val adapter = MainAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        //initialize cipherImpl
        val cipherImpl = KeystoreCipherImpl(this)
        cipherImpl.createAesKey()

        btnAdd.setOnClickListener {
            if (etPlainText.text.isNotEmpty()) {
                val cipherText = cipherImpl.encrypt(etPlainText.text.toString())
                val plainText = cipherImpl.decrypt(cipherText)
                val item = Item(plaintext = plainText, cipherText = cipherText)
                adapter.add(item)
            }
        }
    }
}
