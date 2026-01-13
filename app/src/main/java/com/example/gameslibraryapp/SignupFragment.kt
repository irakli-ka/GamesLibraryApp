package com.example.gameslibraryapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.gameslibraryapp.databinding.FragmentSignupBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database


class SignupFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!

    private var isEmailValid: Boolean = false
    private var isUsernameValid: Boolean = false
    private var isPasswordValid: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        database = Firebase.database
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.emailInput.addTextChangedListener(object : SimpleTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val email = binding.emailInput.text.toString().trim()
                isEmailValid =
                    email.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                        .matches()
                if (!isEmailValid) {
                    binding.emailLayout.error = "Enter a valid email address"
                } else {
                    binding.emailLayout.error = null
                }
                updateButtonState()
            }
        })

        binding.usernameInput.addTextChangedListener(object : SimpleTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val username = s.toString().trim()
                isUsernameValid = username.length > 2
                if (!isUsernameValid) {
                    binding.usernameLayout.error = "Username must be at least 3 characters"
                } else {
                    binding.usernameLayout.error = null
                }
                updateButtonState()
            }
        })

        binding.passwordInput.addTextChangedListener(object : SimpleTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val password = s.toString().trim()
                isPasswordValid = password.length >= 6
                if (!isPasswordValid) {
                    binding.passwordLayout.error = "Password must be at least 6 characters"
                } else {
                    binding.passwordLayout.error = null
                }
                updateButtonState()
            }
        })


        binding.haveAnAccountBtn.setOnClickListener {
            findNavController().navigate(R.id.action_signupFragment_to_loginFragment)
        }

        binding.signupBtn.setOnClickListener {
            val email = binding.emailInput.text.toString().trim()
            val username = binding.usernameInput.text.toString().trim()
            val password = binding.passwordInput.text.toString().trim()

            database.reference.child("username_to_email").child(username).get()
                .addOnSuccessListener { dataSnapshot ->
                    if (dataSnapshot.exists()) {
                        binding.usernameLayout.error = "Username is already taken"
                        Toast.makeText(
                            context,
                            "Please choose a different username",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        binding.usernameLayout.error = null
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(requireActivity()) { task ->
                                if (task.isSuccessful) {
                                    val firebaseUser = auth.currentUser
                                    if (firebaseUser != null) {
                                        val usernamesRef =
                                            database.reference.child("username_to_email")
                                        usernamesRef.child(
                                            binding.usernameInput.text.toString().trim()
                                        ).setValue(email).addOnSuccessListener {
                                            Toast.makeText(
                                                context,
                                                "Account created successfully!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            findNavController().navigate(R.id.action_global_mainFragment)
                                        }
                                            .addOnFailureListener { e ->
                                                Toast.makeText(
                                                    context,
                                                    " ${e.message}",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                    }
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Authentication failed: ${task.exception?.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }

                    }
                }.addOnFailureListener {
                    Toast.makeText(
                        context,
                        "Error checking username: ${it.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }


    }

    private fun updateButtonState() {
        binding.signupBtn.isEnabled = isEmailValid && isUsernameValid && isPasswordValid
    }

    private abstract class SimpleTextWatcher : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {}
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
