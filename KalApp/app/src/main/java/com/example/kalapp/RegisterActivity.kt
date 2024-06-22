package com.example.kalapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val registerHelp: TextView = findViewById(R.id.registerhelp)

        registerHelp.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        setup()
    }

    private fun setup() {
        title = "Registro"
        val registerButton: Button = findViewById(R.id.registerbutton)
        val email: EditText = findViewById(R.id.emailRegister)
        val password: EditText = findViewById(R.id.passwordRegister)
        val name: EditText = findViewById(R.id.nameRegister)
        val dob: EditText = findViewById(R.id.dobRegister)
        val weight: EditText = findViewById(R.id.weightRegister)
        val height: EditText = findViewById(R.id.heightRegister)

        registerButton.setOnClickListener {
            if (email.text.isNotEmpty() && password.text.isNotEmpty() && name.text.isNotEmpty() && dob.text.isNotEmpty()) {
                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(email.text.toString(),
                        password.text.toString()).addOnCompleteListener {
                        if (it.isSuccessful) {
                            val user = it.result?.user
                            user?.let {
                                val userId = user.uid
                                val userEmail = user.email
                                val userName = name.text.toString()
                                val userDob = dob.text.toString()


                                val database = FirebaseDatabase.getInstance()
                                val userRef = database.getReference("users").child(userId)

                                val userMap = mapOf(
                                    "email" to email.text.toString(),
                                    "name" to name.text.toString(),
                                    "dob" to dob.text.toString(),
                                    "weight" to weight.text.toString(),
                                    "height" to height.text.toString()
                                )

                                userRef.setValue(userMap).addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        showMenu(userEmail ?: "", ProviderType.BASIC)
                                    } else {
                                        showAlert()
                                    }
                                }
                            }
                        } else {
                            this.showAlert()
                        }
                    }
            } else {
                showAlert()
            }
        }
    }

    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Error en el registro. Por favor, verifica tus datos e intenta nuevamente.")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showMenu(email: String, provider: ProviderType) {
        val menuIntent = Intent(this, MenuActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(menuIntent)
    }
}