package com.ieee.codelink.ui.main.search.searchScreens.teams

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ieee.codelink.core.BaseFragment
import com.ieee.codelink.databinding.FragmentSearchTeamsBinding
import com.ieee.codelink.domain.tempModels.TempTeam
import com.ieee.codelink.ui.adapters.tempAdapters.TeamsAdapter
import com.ieee.codelink.ui.main.search.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchTeamsFragment : BaseFragment<FragmentSearchTeamsBinding>(FragmentSearchTeamsBinding::inflate) {

    override val viewModel: SearchViewModel by viewModels()
    private val navArgs by navArgs<SearchTeamsFragmentArgs>()
    private lateinit var teamsAdapter: TeamsAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRv()
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        binding.apply {
            llCreateTeam.setOnClickListener{
                openCreateTeamScreen()
            }
        }
    }

    private fun setRv() {
        lifecycleScope.launch(Dispatchers.Main) {
            teamsAdapter = TeamsAdapter(
                viewModel.getFakeDataProvider().fakeTeams ,
                joinTeam = {
                    showToast("Join Team")
                },
                openTeam = {
                    openTeam(it)
                }
            )
            binding.rvTeams.adapter = teamsAdapter
        }
    }

    private fun openTeam(team: TempTeam) {
       findNavController().navigate(SearchTeamsFragmentDirections.actionSearchTeamsFragmentToTeamDetailsFragment(team))
    }

    private fun openCreateTeamScreen() {
        findNavController().navigate(SearchTeamsFragmentDirections.actionSearchTeamsFragmentToCreateTeamFragment())

    }



}