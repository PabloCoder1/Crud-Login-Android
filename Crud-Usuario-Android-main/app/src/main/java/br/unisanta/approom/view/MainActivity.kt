package br.unisanta.approom.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import br.unisanta.approom.dao.UserDao
import br.unisanta.approom.database.AppDataBase
import br.unisanta.approom.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var db:AppDataBase
    private lateinit var userDao: UserDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = Room.databaseBuilder(
            applicationContext,
            AppDataBase::class.java,
            "App-db"
        ).fallbackToDestructiveMigration()
            .build()

        userDao = db.userDao()

        binding.btnLogin.setOnClickListener{
            lifecycleScope.launch {
                val usuarios = withContext(Dispatchers.IO){
                    userDao.getAll()
                }
                usuarios.forEach{
                    Log.d("USER", it.toString())
                }
                //necessario
                val Img = withContext(Dispatchers.IO){
                    userDao.getUserByEmail(binding.edtEmail.text.toString())
                }
                if (Img != null &&
                    Img.firstName == binding.edtFname.text.toString() &&
                    Img.email == binding.edtEmail.text.toString()){
                    val intent = Intent(this@MainActivity, Informacoes::class.java)
                    intent.putExtra("Email", Img.email)
                    startActivity(intent)

                }

            }
        }
        binding.btnCadastro.setOnClickListener{
            val intent = Intent(
                this,
                Cadastro::class.java
            )
            startActivity(intent)

        }
    }
}