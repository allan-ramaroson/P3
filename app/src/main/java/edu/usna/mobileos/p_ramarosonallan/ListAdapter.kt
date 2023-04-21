package edu.usna.mobileos.p_ramarosonallan

import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ToDoItemViewHolder(view: View): RecyclerView.ViewHolder(view), View.OnCreateContextMenuListener{
    private val textView : TextView = view.findViewById(R.id.itemTextView)

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        // adapterPosition gives the position in the adapter of the element displayed in this ViewHolder

        //    programmatically add elements of the menu, instead of inflating a menu
        //groupId (the first parameter) will be set to the adapterPosition
        //itemId (second parameter) can be hardcoded or taken from an XML layout

        menu?.add(adapterPosition, R.id.show, 1, "Show Details")
        menu?.add(adapterPosition, R.id.edit, 2, "Edit Details")
        menu?.add(adapterPosition, R.id.delete, 3, "Delete Item")
    }

    //in the method called from the Adapter onBindViewHolder
    fun bind(item: ImageFile, listener: RecyclerClickListener){
        textView.text = item.fname
        textView.setOnClickListener{listener.onItemClick(item)}
        textView.setOnCreateContextMenuListener(this)
    }
}

class ToDoAdapter(var data : ArrayList<ImageFile>, private val listener: RecyclerClickListener) : RecyclerView.Adapter<ToDoItemViewHolder>() {
    // inflates the layout onCreate
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_layout, parent, false)
        return ToDoItemViewHolder(view)
    }

    // binds the data
    override fun onBindViewHolder(holder: ToDoItemViewHolder, position: Int) {
        holder.bind(data[position], listener)
    }

    // gets data size
    override fun getItemCount() = data.size
}

interface RecyclerClickListener{
    fun onItemClick(todoItem : ImageFile)
}