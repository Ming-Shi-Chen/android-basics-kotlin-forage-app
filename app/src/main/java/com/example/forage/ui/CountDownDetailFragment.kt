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

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.forage.BaseApplication
import com.example.forage.R
import com.example.forage.databinding.FragmentCountdownDetailBinding
import com.example.forage.model.CountDown
import com.example.forage.ui.viewmodel.CountDownViewModel
import com.example.forage.ui.viewmodel.CountDownViewModelFactory

/**
 * A fragment to display the details of a [CountDown] currently stored in the database.
 * The [AddCountDownFragment] can be launched from this fragment to edit the [CountDown]
 */
class CountDownDetailFragment : Fragment() {

    private val navigationArgs: CountDownDetailFragmentArgs by navArgs()

    // TODO: Refactor the creation of the view model to take an instance of
    //  ForageableViewModelFactory. The factory should take an instance of the Database retrieved
    //  from BaseApplication
    private val viewModel: CountDownViewModel by activityViewModels {
        CountDownViewModelFactory(
            (activity?.application as BaseApplication).database.countDownDao()
        )
    }

    private lateinit var countDown: CountDown

    private var _binding: FragmentCountdownDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCountdownDetailBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = navigationArgs.id
        // TODO: Observe a forageable that is retrieved by id, set the forageable variable,
        //  and call the bind forageable method
        viewModel.retrieveCountDown(id).observe(this.viewLifecycleOwner) { selected ->
            countDown = selected
            bindCountDown()
        }
    }

    private fun bindCountDown() {
        binding.apply {
            name.text = countDown.name
            location.text = countDown.address
            notes.text = countDown.notes
            date.text = countDown.date
            if (countDown.haveNotification) {
                season.text = getString(R.string.have_notification)
            } else {
                season.text = getString(R.string.None_notification)
            }
            editForageableFab.setOnClickListener {
                val action = CountDownDetailFragmentDirections
                    .actionCountdownDetailFragmentToAddCountdownFragment(
                        id = countDown.id,
                    title = getString(R.string.label_edit)
                    )
                findNavController().navigate(action)
            }

            location.setOnClickListener {
                launchMap()
            }
        }
    }

    private fun launchMap() {
        val address = countDown.address.let {
            it.replace(", ", ",")
            it.replace(". ", " ")
            it.replace(" ", "+")
        }
        val gmmIntentUri = Uri.parse("geo:0,0?q=$address")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
    }
}
