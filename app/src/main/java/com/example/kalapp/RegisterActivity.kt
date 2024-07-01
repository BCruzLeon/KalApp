package com.example.kalapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import android.app.DatePickerDialog
import android.widget.DatePicker
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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

        val dobEditText: EditText = findViewById(R.id.dobRegister)
        dobEditText.setOnClickListener {
            showDatePickerDialog(dobEditText)
        }
    }

    private fun showDatePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this,
            { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                editText.setText(selectedDate)
            }, year, month, day)

        datePickerDialog.show()
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
        val gender: Spinner = findViewById(R.id.genderRegister)
        val disease: Spinner = findViewById(R.id.diseaseRegister)

        ArrayAdapter.createFromResource(
            this,
            R.array.gender_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            gender.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            this,
            R.array.disease_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            disease.adapter = adapter
        }

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
                                val userWeight = weight.text.toString()
                                val userHeight = height.text.toString()
                                val userGender = gender.selectedItem.toString()
                                val userDisease = disease.selectedItem.toString()
                                val idealWeight = calculateIdealWeight(userHeight.toInt(), userGender)
                                val userAge = calculateAge(userDob)
                                val userCalories = calculateCalories(userWeight.toInt(), userHeight.toInt(), userAge, userGender)
                                

                                val database = FirebaseDatabase.getInstance()
                                val userRef = database.getReference("users").child(userId)

                                val userMap = mapOf(
                                    "email" to email.text.toString(),
                                    "name" to name.text.toString(),
                                    "dob" to dob.text.toString(),
                                    "weight" to weight.text.toString(),
                                    "height" to height.text.toString(),
                                    "gender" to userGender,
                                    "disease" to userDisease,
                                    "idealWeight" to idealWeight.toString(),
                                    "age" to userAge.toString(),
                                    "calories" to userCalories.toString()
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

    private fun calculateIdealWeight(userHeight: Int, userGender: String): Double {
        return if (userGender == "Hombre") {
            (userHeight - 100) - ((userHeight - 150) / 4.0)
        } else {
            (userHeight - 100) - ((userHeight - 150) / 2.5)
        }
    }


    private fun calculateAge(dob: String): Int {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val dobDate = sdf.parse(dob)
        val today = Calendar.getInstance()
        val birthDate = Calendar.getInstance().apply { time = dobDate }

        var age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR)

        if (today.get(Calendar.DAY_OF_YEAR) < birthDate.get(Calendar.DAY_OF_YEAR)) {
            age--
        }
        return age
    }

    fun calculateCalories(weight: Int, height: Int, age: Int, gender: String): Int {
        return if (gender.toLowerCase() == "male") {
            Math.round(10 * weight + 6.25 * height - 5 * age + 5).toInt()
        } else {
            Math.round(10 * weight + 6.25 * height - 5 * age - 161).toInt()
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
