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


class SignupFragment : Fragment() {

    private lateinit var auth: FirebaseAuth;
    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!

    private var isEmailValid: Boolean = false
    private var isUsernameValid: Boolean = false
    private var isPasswordValid: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
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
            }
        })

        binding.usernameInput.addTextChangedListener(object : SimpleTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().length > 2 && s.toString().isNotBlank()) {
                    binding.usernameLayout.error = null
                    isUsernameValid = true
                } else {
                    binding.usernameLayout.error = "Username should use at least 3 letters"
                    isUsernameValid = false
                }
            }
        })

        val passwordWatcher = object : SimpleTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                isPasswordValid = validatePasswords()
            }
        }

        binding.passwordInput.addTextChangedListener(passwordWatcher)
        binding.confirmPasswordInput.addTextChangedListener(passwordWatcher)


        binding.haveAnAccountBtn.setOnClickListener {

            findNavController().navigate(R.id.action_signupFragment_to_loginFragment)
        }


        binding.signupBtn.setOnClickListener {
            val email = binding.emailInput.text.toString().trim()
            val password = binding.passwordInput.text.toString().trim()

            if (!isEmailValid || !isUsernameValid || !isPasswordValid) {
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        val firebaseUser = auth.currentUser
                        // TODO...username logic

                        Toast.makeText(
                            context,
                            "Account created successfully! Redirecting!",
                            Toast.LENGTH_SHORT,
                        ).show()

                        findNavController().navigate(R.id.action_signupFragment_to_loginFragment)

                    } else {
                        Toast.makeText(
                            context,
                            "Authentication failed: ${task.exception?.message}",
                            Toast.LENGTH_LONG,
                        ).show()
                    }
                }


        }
    }

    fun validatePasswords(): Boolean {
        val password = binding.passwordInput.text.toString().trim()
        val confirmPassword = binding.confirmPasswordInput.text.toString().trim()
        var passwordValid = false
        var confirmPasswordValid = false

        if (password.length < 6) {
            binding.passwordLayout.error = "Password must be at least 6 characters"
        } else {
            binding.passwordLayout.error = null
            passwordValid = true
        }

        if (confirmPassword.isNotEmpty() && password != confirmPassword) {
            binding.confirmPasswordLayout.error = "Passwords do not match"
        } else {
            binding.confirmPasswordLayout.error = null
            confirmPasswordValid = true
        }
        return passwordValid && confirmPasswordValid
    }


    private abstract class SimpleTextWatcher : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable?) {
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}