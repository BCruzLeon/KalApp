package com.example.kalapp

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*

enum class ProviderType {
    BASIC
}

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val bundle = intent.extras
        val email = bundle?.getString("email")
        val provider = bundle?.getString("provider")
        setup(email ?: "", provider ?: "")
    }

    private fun setup(email: String, provider: String) {
        val logOut: LinearLayout = findViewById(R.id.logoutbutton)
        val nombreUsuario: TextView = findViewById(R.id.userName)
        val pesoUsuario: TextView = findViewById(R.id.userWeight)
        val alturaUsuario: TextView = findViewById(R.id.userHeight)
        val pesoMeta: TextView = findViewById(R.id.userGoal)
        val caloriasMeta: TextView = findViewById(R.id.dailyCalorieGoal)


        title = "Inicio"

        // Obtén el userId del usuario actual
        val userId = FirebaseAuth.getInstance().currentUser?.uid


        if (userId != null) {
            val database = FirebaseDatabase.getInstance()
            val userRef = database.getReference("users").child(userId)

            // Lee los datos del usuario desde Firebase
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userName = snapshot.child("name").value.toString()
                    nombreUsuario.text = userName
                    val userWeight = snapshot.child("weight").value.toString()
                    pesoUsuario.text = userWeight
                    val userHeight = snapshot.child("height").value.toString()
                    alturaUsuario.text = userHeight
                    val userDob = snapshot.child("dob").value.toString()
                    val userGoal = snapshot.child("idealWeight").value.toString()
                    pesoMeta.text = "Peso meta: $userGoal Kg"
                    val dailyCalorieGoal = snapshot.child("calories").value.toString()
                    caloriasMeta.text = dailyCalorieGoal
                }

                override fun onCancelled(error: DatabaseError) {
                    // Maneja el error de lectura de datos
                    nombreUsuario.text = "Nombre no disponible"
                }
            })
        }

        logOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

}

