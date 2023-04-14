package edu.usna.mobileos.p_ramarosonallan

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
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
    lateinit var description : TextView
    lateinit var mainImg : ImageView
    lateinit var camBtn : Button
    lateinit var filePath: String
    private val LO_RES_REQUEST_CODE = 123
    private val HI_RES_REQUEST_CODE = 456
    val dog : String = "ruffagain243"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        description = findViewById(R.id.description)
        mainImg = findViewById(R.id.mainImg)
        camBtn = findViewById(R.id.cameraBtn)
    }

    fun launchCamera(v : View?){
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
            "edu.usna.mobileos.cameraexamples.fileprovider",
            cameraFile
        )


        //set the URI as an extra for this intent
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileURI)

        // start the image capture Intent
        startActivityForResult(intent, HI_RES_REQUEST_CODE)
    }

    private fun createFileName(): String {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        return "IMG_$timeStamp.jpg"
    }
}