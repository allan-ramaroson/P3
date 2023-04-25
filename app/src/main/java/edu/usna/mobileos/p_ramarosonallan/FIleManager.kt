package edu.usna.mobileos.p_ramarosonallan

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class FileManager : AppCompatActivity(), RecyclerClickListener {
    lateinit var files : ArrayList<ImageFile>
    lateinit var fileListView : RecyclerView
    private val appFile = "imgFiles"
    private var count = 0
    private val tag = "IT472"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.file_manager)

        fileListView = findViewById(R.id.FileList)

        files = retrieveTasks()

        val data = intent.getSerializableExtra("newImg")
        if(data != null){
            files.add(data as ImageFile)
        }
        count = files.size
        Log.i(tag, "current list size onCreate in FileManager is $count")
        val toDoListAdapter = ToDoAdapter(files, this)
        fileListView.adapter = toDoListAdapter
    }

    // copy paste
    private fun saveObjectToFile(fileName: String, obj: Any) {
        try {
            ObjectOutputStream(openFileOutput(fileName, MODE_PRIVATE)).use {
                it.writeObject(obj)
            }
        }
        catch (e: IOException){
            Log.e("IT472", "IOException writing file $fileName")
        }
    }

    // copy paste
    private fun getObjectFromFile(fileName: String): Any? {
        try{
            ObjectInputStream(openFileInput(fileName)).use{
                return it.readObject()
            }
        }
        catch (e: IOException){
            Log.e("IT472", "IOException readin g file $fileName")
            return null
        }
    }

    // type casts the ArrayList object the
    private fun retrieveTasks() : ArrayList<ImageFile> {
        val result = getObjectFromFile(appFile)

        return if (result != null){
            Log.i("IT472","inside retrieve tasks")
            result as ArrayList<ImageFile> //as String would be redundant here
        } else{
            Log.i("IT472", "did not find string in file")
            arrayListOf<ImageFile>()
        }
    }

    override fun onItemClick(img: ImageFile) {
        val resultIntent = Intent()
        resultIntent.putExtra("image", img)
        setResult(RESULT_OK, resultIntent)
        finish()
    }

    override fun onPause() {
        super.onPause()
        saveObjectToFile(appFile, files)
    }

    override fun onStop() {
        super.onStop()
        saveObjectToFile(appFile, files)
    }
}