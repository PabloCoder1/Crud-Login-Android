package br.unisanta.approom.view

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import br.unisanta.approom.dao.UserDao
import br.unisanta.approom.database.AppDataBase
import br.unisanta.approom.databinding.ActivityInformacoesBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class Informacoes : AppCompatActivity() {
    private lateinit var binding: ActivityInformacoesBinding
    private lateinit var db: AppDataBase
    private lateinit var userDao: UserDao
    private var Email: String = ""
    private var ID: Int = 0
    private fun bitmapToUri(bitmap: Bitmap, context: Context): Uri? {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Title", null)
        return Uri.parse(path)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInformacoesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = Room.databaseBuilder(
            applicationContext,
            AppDataBase::class.java,
            "App-db"
        ).fallbackToDestructiveMigration()
            .build()
        userDao = db.userDao()
        Email = intent.getStringExtra("Email")!!

        lifecycleScope.launch {

            val Img = withContext(Dispatchers.IO){
                    userDao.getUserByEmail(Email)
            }

            if (Img != null) {
                binding.Perfilft.setImageURI(bitmapToUri(Img.image,this@Informacoes))
                binding.Nomeperfil.setText(Img.firstName)
                binding.Emailperfil.setText(Img.email)
                ID = Img.uid
            }


        }
        binding.Atualizar.setOnClickListener{
            val intent = Intent(this@Informacoes, Update::class.java)
            intent.putExtra("ID", ID)
            startActivity(intent)
        }

            .
            binding.Deletar.setOnClickListener {
                lifecycleScope.launch {
                    val userToDelete = userDao.getUserByID(ID)
                    if (userToDelete != null) {
                        db.userDao().deleteUser(userToDelete)
                    }
                }
            }
       }
    }
