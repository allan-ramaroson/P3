package edu.usna.mobileos.p_ramarosonallan

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.FileProvider
import java.io.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    // global variables
    lateinit var description : EditText
    lateinit var filetext : TextView
    lateinit var mainImg : ImageView
    lateinit var cameraBtn : Button
    lateinit var filePath: String
    lateinit var currImgDate : String
    private val HI_RES_REQUEST_CODE = 456
    private val TAG : String = "IT472"
    private var imageFiles = arrayListOf<ImageFile>()
    private val appFile = "imgFiles"
    private val previousCode = 2001


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        description = findViewById(R.id.description)
        mainImg = findViewById(R.id.mainImg)
        cameraBtn = findViewById(R.id.cameraBtn)
        filetext = findViewById(R.id.filename)
        imageFiles = retrieveTasks()

        mainImg.setImageResource(R.drawable.empty)
    }

    // create options menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        return true
    }

    // handles click events for adding an item
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.previous -> {
                val previousIntent = Intent(this, FileManager::class.java)
                previousIntent.putExtra("files", imageFiles)
                startActivityForResult(previousIntent, previousCode)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun onClickCamera(v: View?){
        // intent to launch default camera app
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        // calls custom method that creates a file name based on the current Date
        val fileName = createFileName()
        currImgDate = createFileName()

        //  store picture to app specific folder.  Files will be deleted if app is deleted
        filePath = getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString() + "/" + fileName
        val cameraFile = File(filePath)

        //get URI - this uses a FileProvider
        val fileURI: Uri = FileProvider.getUriForFile(
            this,
            "edu.usna.mobileos.p_ramarosonallan.fileprovider",
            cameraFile
        )

        //set the URI as an extra for this intent
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileURI)

        // start the image capture Intent
        startActivityForResult(intent, HI_RES_REQUEST_CODE)
    }

    fun onClickSave(v: View?){
        val currImgFile = ImageFile(currImgDate, filePath, description.text.toString())
        val i = index(currImgFile)
        if(i != -1){
            imageFiles[i] = currImgFile

        }
        else{
            imageFiles.add(currImgFile)
        }
    }

    fun index(ifile : ImageFile) : Int{
        for(i in imageFiles.indices){
            if(ifile.fname == imageFiles[i].fname){
                return i
            }
        }
        return -1
    }

    // creates a filename based off of current time and day
    private fun createFileName(): String {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        return "IMG_$timeStamp.jpg"
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == HI_RES_REQUEST_CODE) {
            when(resultCode){
                RESULT_OK ->{
                    // get the high-quality image created if intent extra used
                    val photo = BitmapFactory.decodeFile(filePath)
                    mainImg.setImageBitmap(photo)
                    filetext.text = currImgDate
                }
                RESULT_CANCELED -> Log.i(TAG, "cancelled")
                else -> Log.i(TAG, "failed")
            }
        }
        else if(requestCode == previousCode){
            val currImg : ImageFile = data?.getSerializableExtra("image") as ImageFile
            val photo = BitmapFactory.decodeFile(currImg.path)
            mainImg.setImageBitmap(photo)
            filetext.text = currImg.fname
            // TODO("Last error was that currImgDate was not init when we open an old picture upon starting the app")
            currImgDate = currImg.fname.toString()
            description.setText(currImg.descript)
        }
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

    override fun onPause() {
        super.onPause()
        saveObjectToFile(appFile, imageFiles)
    }

    override fun onStop() {
        super.onStop()
        saveObjectToFile(appFile, imageFiles)
    }

}

data class ImageFile(val fname : String?, val path: String?, val descript : String?) : Serializable