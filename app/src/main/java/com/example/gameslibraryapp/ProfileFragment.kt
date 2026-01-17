package com.example.gameslibraryapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.gameslibraryapp.adapter.LibraryAdapter
import com.example.gameslibraryapp.databinding.FragmentProfileBinding
import com.example.gameslibraryapp.viewmodel.MainViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var libraryAdapter: LibraryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        mainViewModel.userProfile.observe(viewLifecycleOwner) { userProfile ->
            userProfile?.let {
                binding.username.text = it.username
                binding.email.text = it.email
                Glide.with(this)
                    .load(it.profileImageUrl)
                    .placeholder(R.drawable.ic_person)
                    .error(R.drawable.ic_person)
                    .circleCrop()
                    .into(binding.profileImage)
            }
        }

        mainViewModel.libraryGames.observe(viewLifecycleOwner) { games ->
            libraryAdapter.submitList(games)
        }

        binding.logoutBtn.setOnClickListener {
            Firebase.auth.signOut()
            findNavController().navigate(R.id.action_global_loginFragment)
        }
    }

    private fun setupRecyclerView() {
        libraryAdapter = LibraryAdapter(
            onGameClicked = { game ->
                val action =
                    ProfileFragmentDirections.actionProfileFragmentToGameDetailsFragment(game.id)
                findNavController().navigate(action)
            },
            onSaveGameClicked = { game ->
                mainViewModel.saveGameToLibrary(game)
                Toast.makeText(context, "${game.name} added", Toast.LENGTH_SHORT).show()
            },
            onRemoveGameClicked = { game ->
                mainViewModel.removeGameFromLibrary(game.id)
                Toast.makeText(context, "${game.name} removed", Toast.LENGTH_SHORT).show()
            },
            getLibraryIds = { mainViewModel.libraryGameIds.value },
            getAuthState = { mainViewModel.authState.value }
        )
        binding.libraryRv.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = libraryAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}