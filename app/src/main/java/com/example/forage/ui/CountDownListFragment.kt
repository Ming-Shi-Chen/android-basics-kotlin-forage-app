/*
 * Copyright (C) 2021 The Android Open Source Project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.forage.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.forage.BaseApplication
import com.example.forage.R
import com.example.forage.databinding.FragmentCountdownListBinding
import com.example.forage.ui.adapter.CountDownListAdapter
import com.example.forage.ui.viewmodel.CountDownViewModel
import com.example.forage.ui.viewmodel.CountDownViewModelFactory

/**
 * A fragment to view the list of [Forageable]s stored in the database.
 * Clicking on a [Forageable] list item launches the [CountDownDetailFragment].
 * Clicking the [FloatingActionButton] launched the [AddCountDownFragment]
 */
class CountDownListFragment : Fragment() {

    // TODO: Refactor the creation of the view model to take an instance of
    //  ForageableViewModelFactory. The factory should take an instance of the Database retrieved
    //  from BaseApplication
    private val viewModel: CountDownViewModel by activityViewModels {
        CountDownViewModelFactory(
            (activity?.application as BaseApplication).database.countDownDao()
        )
    }

    private var _binding: FragmentCountdownListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentCountdownListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = CountDownListAdapter ({ countDown ->
            val action = CountDownListFragmentDirections
                .actionCountdownListFragmentToCountdownDetailFragment(countDown.id)
            findNavController().navigate(action)
        },
            this.viewLifecycleOwner,
            viewModel
        )

        // TODO: observe the list of forageables from the view model and submit it to the adapter
        viewModel.allCountDowns.observe(this.viewLifecycleOwner) { countDown ->
            countDown.let {
                adapter.submitList(it)
            }
        }

        binding.apply {
            recyclerView.adapter = adapter
            addForageableFab.setOnClickListener {
                findNavController().navigate(
                    R.id.action_countdownListFragment_to_addCountdownFragment
                )
            }


        }

    }
}
