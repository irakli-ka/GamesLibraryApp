package com.example.gameslibraryapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.gameslibraryapp.SignupFragment.SimpleTextWatcher
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


        binding.usernameInput.addTextChangedListener(object : SimpleTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val username = s.toString().trim()
                val invalidCharsRegex = Regex("[^a-zA-Z0-9]")

                if (username.length <= 2) {
                    binding.usernameLayout.error = "Username must be at least 3 characters"
                    binding.loginBtn.isEnabled = false
                } else if (username.contains(invalidCharsRegex)) {
                    binding.usernameLayout.error = "Only letters and numbers are allowed"
                    binding.loginBtn.isEnabled = false
                } else {
                    binding.usernameLayout.error = null
                    binding.loginBtn.isEnabled = true
                }
            }
        })

        binding.dontHaveAccountBtn.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
        }

        binding.loginBtn.setOnClickListener {
            val username = binding.usernameInput.text.toString().trim()
            val password = binding.passwordInput.text.toString().trim()

            database.reference.child("username_to_email").child(username).get()
                .addOnSuccessListener { snapshot ->
                    val email = snapshot.getValue(String::class.java)

                    if (email != null) {
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(requireActivity()) { task ->
                                if (task.isSuccessful) {
                                    findNavController().navigate(R.id.action_loginFragment_to_mainFragment)
                                } else {
                                    binding.passwordLayout.error = "Invalid password"
                                }
                            }
                    } else {
                        binding.usernameLayout.error = "User not found"
                    }
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}