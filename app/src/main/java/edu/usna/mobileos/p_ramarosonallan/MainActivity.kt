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
import com.google.android.material.snackbar.Snackbar
import java.io.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    // global variables
    private val c: Calendar = Calendar.getInstance()
    private var date : String = "${c.get(Calendar.MONTH)}/${c.get(Calendar.DATE)}/${c.get(Calendar.YEAR)}"
    var newDate : String
    lateinit var description : EditText
    lateinit var filetext : TextView
    lateinit var mainImg : ImageView
    lateinit var cameraBtn : Button
    lateinit var filePath: String
    lateinit var currImage : ImageFile
    lateinit var currImgFName : String
    lateinit var currImgPath : String
    private val HI_RES_REQUEST_CODE = 456
    private val TAG : String = "IT472"
    private val previousCode = 2001
    private val appFile = "imgFiles"
    private lateinit var imgList : ArrayList<ImageFile>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imgList = retrieveTasks()

        description = findViewById(R.id.description)
        mainImg = findViewById(R.id.mainImg)
        cameraBtn = findViewById(R.id.cameraBtn)
        filetext = findViewById(R.id.filename)

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
                startActivityForResult(previousIntent, previousCode)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun onClickCamera(v: View?){
        if(newC != c){
            Snackbar.make(findViewById(android.R.id.content),
                "Can't retake picture. TOO OLD!",
                Snackbar.LENGTH_LONG)
                .show()
        }
        else{
            // intent to launch default camera app
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            // calls custom method that creates a file name based on the current Date
            val fileName = createFileName()
            currImgFName = fileName

            //  store picture to app specific folder.  Files will be deleted if app is deleted
            filePath = getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString() + "/" + fileName
            val cameraFile = File(filePath)
            currImgPath = filePath

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
    }

    // creates a filename based off of current time and day
    private fun createFileName(): String {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        return "IMG_$timeStamp.jpg"
    }

    fun onClickSave(v: View?){
        currImage = ImageFile(date, currImgFName, currImgPath, description.text.toString())
        val saveIntent = Intent(this, FileManager::class.java)
        saveIntent.putExtra("newImg", currImage)
        Snackbar.make(findViewById(android.R.id.content),
            "Saved picture taken on ${currImage.date}...${currImage.count} take(s)",
            Snackbar.LENGTH_LONG)
            .show()
        startActivity(saveIntent)
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
                    filetext.text = currImgFName
                }
                RESULT_CANCELED -> Log.i(TAG, "cancelled")
                else -> Log.i(TAG, "failed")
            }
        }
        else if(requestCode == previousCode){
            when(resultCode){
                RESULT_OK ->{
                    currImage = data?.getSerializableExtra("image") as ImageFile
                    if(date == currImage.date){
                        cameraBtn.text = "RETAKE?"
                    }
                    date = currImage.date
                    currImgPath = currImage.path
                    currImgFName = currImage.fname
                    val photo = BitmapFactory.decodeFile(currImage.path)
                    mainImg.setImageBitmap(photo)
                    filetext.text = date
                    description.setText(currImage.descript)
                }
                RESULT_CANCELED -> Log.i(TAG, "cancelled")
                else -> Log.i(TAG, "failed")
            }
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
}

data class ImageFile(val date : String, val fname : String, val path: String, val descript : String, var count : Int = 0) : Serializable