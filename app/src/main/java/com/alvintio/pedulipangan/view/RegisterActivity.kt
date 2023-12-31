package com.alvintio.pedulipangan.view

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.alvintio.pedulipangan.MainActivity
import com.alvintio.pedulipangan.R
import com.alvintio.pedulipangan.databinding.ActivityRegisterBinding
import com.alvintio.pedulipangan.util.ViewUtils
import com.alvintio.pedulipangan.viewmodel.AuthenticationViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.firestore

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    private lateinit var auth: FirebaseAuth

    private lateinit var viewModel: AuthenticationViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        playAnimation()

        viewModel = ViewModelProvider(this).get(AuthenticationViewModel::class.java)

        ViewUtils.setupFullScreen(this)
        setupRegister()
    }

    private fun setupRegister() {
        binding.progressBar.visibility = View.GONE

        binding.btnRegister.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()

            if (name.isEmpty()) {
                binding.edRegisterName.error = getString(R.string.input_name)
                return@setOnClickListener
            }

            if (email.isEmpty()) {
                binding.edRegisterEmail.error = getString(R.string.input_email)
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                binding.edRegisterPassword.error = getString(R.string.input_password)
                return@setOnClickListener
            }

            binding.progressBar.visibility = View.VISIBLE

            viewModel.register(name, email, password)

            registerWithEmailAndPassword(name, email, password)
        }
    }


    private fun saveUserDataToFirestore(userId: String, name: String, email: String) {
        val db = Firebase.firestore
        val usersCollection = db.collection("users")

        val user = com.alvintio.pedulipangan.model.User(name, email, userId)

        usersCollection.document(userId)
            .set(user)
            .addOnSuccessListener {
                Log.d("Firestore", "Data telah tersimpan di Firestore!")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Data belum tersimpan di Firestore!", e)
            }
    }

    private fun registerWithEmailAndPassword(name: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                binding.progressBar.visibility = View.GONE

                if (task.isSuccessful) {
                    val user = task.result?.user
                    if (user != null) {
                        Log.d("Register", "User created successfully")
                        saveUserDataToFirestore(user.uid, name, email)

                        Toast.makeText(
                            this,
                            "Registrasi berhasil!",
                            Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    Toast.makeText(
                        this,
                        "Registrasi gagal, tolong ulang kembali!!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.ivLogo, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.tvTitle, View.ALPHA, 1f).setDuration(300)
        val message =
            ObjectAnimator.ofFloat(binding.tvSubtitle, View.ALPHA, 1f).setDuration(300)
        val nameTextView =
            ObjectAnimator.ofFloat(binding.tvName, View.ALPHA, 1f).setDuration(300)
        val nameEditTextLayout =
            ObjectAnimator.ofFloat(binding.tilName, View.ALPHA, 1f).setDuration(300)
        val emailTextView =
            ObjectAnimator.ofFloat(binding.tvEmail, View.ALPHA, 1f).setDuration(300)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.tilEmail, View.ALPHA, 1f).setDuration(300)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.tvPassword, View.ALPHA, 1f).setDuration(300)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.tilPassword, View.ALPHA, 1f).setDuration(300)
        val register = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(300)

        AnimatorSet().apply {
            playSequentially(
                title,
                message,
                nameTextView,
                nameEditTextLayout,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                register
            )
            startDelay = 100
        }.start()
    }
}