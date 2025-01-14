package com.dicoding.nutridish.view.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.dicoding.nutridish.R
import com.dicoding.nutridish.ViewModelFactory
import com.dicoding.nutridish.data.pref.UserPreference
import com.dicoding.nutridish.data.pref.dataStore
import com.dicoding.nutridish.databinding.FragmentProfileBinding
import com.dicoding.nutridish.main.MainActivity
import com.dicoding.nutridish.view.profile.settings.AboutActivity
import kotlinx.coroutines.launch


class ProfileFragment : Fragment() {

    private lateinit var userPreference: UserPreference
    private var _binding: FragmentProfileBinding? = null
    private val bindingSafe get() = _binding
    private lateinit var viewModel : ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userPreference = UserPreference.getInstance(requireContext().dataStore)
        lifecycleScope.launch {
            setupProfile()
        }

        val buttonAbout = view.findViewById<View>(R.id.btnAboutApp)
        val buttonLogout = view.findViewById<View>(R.id.btnLogout)

        val viewModel = ViewModelProvider(this, ViewModelFactory.getInstance(requireActivity()))[ProfileViewModel::class.java]

        buttonAbout.setOnClickListener{
            val intent = Intent(activity, AboutActivity::class.java)
            startActivity(intent)
        }

        buttonLogout.setOnClickListener{
            lifecycleScope.launch {
                viewModel.logout()
                val intent = Intent(activity, MainActivity::class.java)
                startActivity(intent)
                activity?.finish()
            }
        }
    }

    private fun setupProfile() {
        val factory = ViewModelFactory.getInstance(requireActivity())
        val viewModel: ProfileViewModel by viewModels { factory }
        lifecycleScope.launch {
            val userId = getUserId()
            if (userId != null) {
                viewModel.getUser(userId)
            }
            viewModel.userResult.observe(viewLifecycleOwner) { user ->
                bindingSafe?.apply {
                    textGreeting.text = "Hello, ${user?.userName}"
                    textName.text = user?.userName
                    textEmail.text = user?.email
                }
            }
        }
    }

    private suspend fun getUserId(): String? {
        return userPreference.getUserId()
    }

}