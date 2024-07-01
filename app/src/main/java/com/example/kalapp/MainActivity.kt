package com.example.kalapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var email: EditText
    private lateinit var password: EditText

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        email = findViewById(R.id.emailEditText)
        password = findViewById(R.id.passwordEditText)
        val signUpLink: TextView = findViewById(R.id.signuplink)

        signUpLink.setOnClickListener {
            // Iniciar RegisterActivity
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        setup()
    }

    private fun setup() {
        title = "Login"
        val loginButton: Button = findViewById(R.id.loginbutton)

        loginButton.setOnClickListener {
            if (email.text.isNotEmpty() && password.text.isNotEmpty()) {
                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(email.text.toString(),
                        password.text.toString()).addOnCompleteListener{
                            if (it.isSuccessful) {
                                showMenu(it.result?.user?.email ?: "", ProviderType.BASIC)
                            } else {
                                this.showAlert()
                            }
                    }
            }

        }
    }

    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Usuario no registrado, por favor registrese para continuar")
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




