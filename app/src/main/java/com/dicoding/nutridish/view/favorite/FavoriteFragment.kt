package com.dicoding.nutridish.view.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.nutridish.ViewModelFactory
import com.dicoding.nutridish.databinding.FragmentFavoriteBinding
import com.dicoding.nutridish.data.api.response.RecipeSearchResponseItem

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    private lateinit var favoriteAdapter: FavoriteAdapter
    private var dataList: List<RecipeSearchResponseItem> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoriteBinding.inflate(layoutInflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = ViewModelFactory.getInstance(requireActivity())
        val viewModel: FavoriteViewModel by viewModels { factory }

        // Setup Adapter
        favoriteAdapter = FavoriteAdapter()

        // Mengamati LiveData dari ViewModel
        viewModel.favoriteNutri.observe(viewLifecycleOwner) { favoriteList ->
            if (favoriteList != null) {
                val items = favoriteList.map {
                    RecipeSearchResponseItem(title = it.title, image = it.mediaCover, rating = it.rating)

                }
                dataList = items
                favoriteAdapter.submitList(items)
                updateUI(items.isEmpty())
            }
        }

        // Setup RecyclerView
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = favoriteAdapter
        }

        // Setup SearchView
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrEmpty()) {
                    viewModel.searchFavoritesByTitle(newText).observe(viewLifecycleOwner) { filteredList ->
                        val items = filteredList.map {
                            RecipeSearchResponseItem(
                                title = it.title,
                                image = it.mediaCover,
                                rating = it.rating
                            )
                        }
                        favoriteAdapter.submitList(items)
                        updateUI(items.isEmpty())
                    }
                } else {
                    viewModel.favoriteNutri.observe(viewLifecycleOwner) { favoriteList ->
                        val items = favoriteList.map {
                            RecipeSearchResponseItem(
                                title = it.title,
                                image = it.mediaCover,
                                rating = it.rating
                            )
                        }
                        favoriteAdapter.submitList(items)
                        updateUI(items.isEmpty())
                    }
                }
                return true
            }
        })
    }



    private fun updateUI(isEmpty: Boolean) {
        binding.recyclerView.visibility = if (isEmpty) View.GONE else View.VISIBLE
        binding.tvEmpty.visibility = if (isEmpty) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
