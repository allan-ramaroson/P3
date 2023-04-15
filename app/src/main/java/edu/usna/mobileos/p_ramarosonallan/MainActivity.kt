package edu.usna.mobileos.p_ramarosonallan

import android.content.Intent
import android.graphics.Bitmap
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
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    // global variables
    lateinit var description : TextView
    lateinit var mainImg : ImageView
    lateinit var cameraBtn : Button
    lateinit var filePath: String
    private val HI_RES_REQUEST_CODE = 456
    private val TAG : String = "IT472"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        description = findViewById(R.id.description)
        mainImg = findViewById(R.id.mainImg)
        cameraBtn = findViewById(R.id.cameraBtn)

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
                val addIntent = Intent(this, AddItem::class.java)
                startActivityForResult(addIntent, requestCodeAdd)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun onClick(v: View?){
        // intent to launch default camera app
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        // calls custom method that creates a file name based on the current Date
        val fileName = createFileName()

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
                    description.text = filePath
                }
                RESULT_CANCELED -> Log.i(TAG, "cancelled")
                else -> Log.i(TAG, "failed")
            }
        }
    }
}