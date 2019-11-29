package id.rezkyauliapratama.encryption

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item.view.*

class MainAdapter : RecyclerView.Adapter<MainAdapter.ViewHolder>() {

    private val items = mutableListOf<Item>()

    fun add(item: Item) {
        items.add(0, item)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return ViewHolder(layoutInflater)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: Item) {
            view.tvPlainText.text = item.plaintext
            item.cipherText?.apply {
                view.tvCipherText.visibility = View.VISIBLE
                view.tvCipherText.text = this
            }
        }
    }
}

data class Item(val plaintext: String, var cipherText: String? = null)