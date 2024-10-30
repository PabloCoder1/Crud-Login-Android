package br.unisanta.approom.view

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import br.unisanta.approom.dao.UserDao
import br.unisanta.approom.database.AppDataBase
import br.unisanta.approom.databinding.ActivityCadastroBinding
import br.unisanta.approom.databinding.ActivityUpdateBinding
import br.unisanta.approom.model.User
import kotlinx.coroutines.launch
import java.io.InputStream

class Update : AppCompatActivity() {
    private lateinit var binding: ActivityUpdateBinding
    private lateinit var db: AppDataBase
    private lateinit var userDao: UserDao
    private lateinit var imgURi: Bitmap
    private var ID: Int = 0

    private fun uriToBitmap(uri: Uri): Bitmap? {
        return try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeStream(inputStream, null, options)


            val requiredWidth = 200
            val requiredHeight = 200


            options.inSampleSize = calculateInSampleSize(options, requiredWidth, requiredHeight)
            options.inJustDecodeBounds = false
            inputStream?.close()
            val inputStream2: InputStream? = contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream2, null, options)
        } catch (e: Exception) {
            Log.e("URI to Bitmap", "Erro ao converter Uri para Bitmap", e)
            null
        }
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val (height: Int, width: Int) = options.outHeight to options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ID = intent.getIntExtra("ID",0)
        binding = ActivityUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root);
        db = Room.databaseBuilder(
            applicationContext,
            AppDataBase::class.java,
            "App-db"
        ).fallbackToDestructiveMigration()
            .build()
        userDao = db.userDao()
        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->

            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")
                binding.UpdtImg.setImageURI(uri)
                imgURi = uriToBitmap(uri)!!
            } else {
                Log.d("PhotoPicker", "No media selected")
            }

        }
        binding.BTNIMG2.setOnClickListener{
            Log.d("Aviso", "Apertado")

            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))


        }
        binding.UpdtBTN.setOnClickListener{
            if (imgURi != null){
                val fname = binding.UpdtNome.text.toString()
                val lname = binding.UpdtEmail.text.toString()
                var bitmap = imgURi
                lifecycleScope.launch {

                    var usertoupdate = db.userDao().getUserByID(ID)

                    if (usertoupdate != null){
                        usertoupdate.firstName = fname
                        usertoupdate.email = lname
                        usertoupdate.image = bitmap
                        db.userDao().updateUser(usertoupdate)
                    }

                }
                binding.UpdtNome.setText("")
                binding.UpdtEmail.setText("")
                Toast.makeText(this, "Sucesso!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@Update, Informacoes::class.java)
                intent.putExtra("Email", lname)
                startActivity(intent)
            }else{
                Toast.makeText(this, "Informe os dados!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}