package com.example.gameslibraryapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.gameslibraryapp.databinding.FragmentLoginBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        database = Firebase.database
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.dontHaveAccountBtn.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
        }

        binding.loginBtn.setOnClickListener {
            binding.usernameLayout.error = null
            binding.passwordLayout.error = null

            val username = binding.usernameInput.text.toString().trim()
            val password = binding.passwordInput.text.toString().trim()

            if (username.isBlank()) {
                binding.usernameLayout.error = "Username is required"
                return@setOnClickListener
            }
            if (password.isBlank()) {
                binding.passwordLayout.error = "Password is required"
                return@setOnClickListener
            }

            database.reference.child("username_to_email").child(username)
                .get()
                .addOnSuccessListener { dataSnapshot ->
                    val email = dataSnapshot.getValue(String::class.java)

                    if (email != null) {
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(requireActivity()) { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT)
                                        .show()
                                    findNavController().navigate(R.id.action_loginFragment_to_mainFragment)
                                } else {
                                    binding.passwordLayout.error = "Incorrect password"
                                }
                            }
                    } else {
                        binding.usernameLayout.error = "Username does not exist"
                    }
                }.addOnFailureListener {
                    Toast.makeText(context, "Error connecting to the database.", Toast.LENGTH_SHORT)
                        .show()
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}