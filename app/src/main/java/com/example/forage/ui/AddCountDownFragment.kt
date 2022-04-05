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
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.forage.BaseApplication
import com.example.forage.R
import com.example.forage.databinding.FragmentAddCountdownBinding
import com.example.forage.model.CountDown
import com.example.forage.ui.viewmodel.CountDownViewModel
import com.example.forage.ui.viewmodel.CountDownViewModelFactory
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * A fragment to enter data for a new [CountDown] or edit data for an existing [CountDown].
 * [CountDown]s can be saved or deleted from this fragment.
 */
class AddCountDownFragment : Fragment() {

    private val navigationArgs: AddCountDownFragmentArgs by navArgs()

    private var _binding: FragmentAddCountdownBinding? = null

    private lateinit var countDown: CountDown

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!



    // TODO: Refactor the creation of the view model to take an instance of
    //  ForageableViewModelFactory. The factory should take an instance of the Database retrieved
    //  from BaseApplication
    private val viewModel: CountDownViewModel by activityViewModels {
        CountDownViewModelFactory(
            (activity?.application as BaseApplication).database.countDownDao()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentAddCountdownBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = navigationArgs.id
        if (id > 0) {


            // TODO: Observe a Forageable that is retrieved by id, set the forageable variable,
            //  and call the bindForageable method
            viewModel.retrieveCountDown(id).observe(this.viewLifecycleOwner) { selected ->
                countDown = selected
                bindCountDown(countDown)
            }
            binding.deleteBtn.visibility = View.VISIBLE
            binding.deleteBtn.setOnClickListener {
                deleteCountDown(countDown)
            }
        } else {
            binding.saveBtn.setOnClickListener {
                addCountDown()
            }
        }
        binding.datetimeInput.datePicker(requireActivity().supportFragmentManager,"tag")

    }

    private fun deleteCountDown(countDown: CountDown) {
        viewModel.deleteCountDown(countDown)
        findNavController().navigate(
            R.id.action_addCountdownFragment_to_countdownListFragment
        )
    }

    private fun addCountDown() {
        if (isValidEntry()) {
            viewModel.addCountDown(
                binding.nameInput.text.toString(),
                binding.locationAddressInput.text.toString(),
                binding.inSeasonCheckbox.isChecked,
                binding.notesInput.text.toString(),
                binding.datetimeInput.text.toString()
            )
            findNavController().navigate(
                R.id.action_addCountdownFragment_to_countdownListFragment
            )
        }
    }

    private fun updateCountDown() {
        if (isValidEntry()) {
            viewModel.updateCountDown(
                id = navigationArgs.id,
                name = binding.nameInput.text.toString(),
                address = binding.locationAddressInput.text.toString(),
                inSeason = binding.inSeasonCheckbox.isChecked,
                notes = binding.notesInput.text.toString(),
                date = binding.datetimeInput.text.toString()
            )
            findNavController().navigate(
                R.id.action_addCountdownFragment_to_countdownListFragment
            )
        }
    }

    private fun bindCountDown(countDown: CountDown) {
        binding.apply {
            nameInput.setText(countDown.name,TextView.BufferType.SPANNABLE)
            locationAddressInput.setText(countDown.address, TextView.BufferType.SPANNABLE)
            inSeasonCheckbox.isChecked = countDown.haveNotification
            notesInput.setText(countDown.notes, TextView.BufferType.SPANNABLE)
            datetimeInput.setText(countDown.date, TextView.BufferType.SPANNABLE)
            saveBtn.setOnClickListener {
                updateCountDown()
            }

        }

    }

    fun TextInputEditText.datePicker(fm: FragmentManager, tag: String) {

        isFocusableInTouchMode = false
        isClickable = true
        isFocusable = true

        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Date Selector")
                .build()

        setOnClickListener {

            datePicker.show(fm, "tag")
        }
        datePicker.addOnPositiveButtonClickListener {
            val date = datePicker.selection
            val formatDate = CountDownViewModel.formatter.format(date)
//            Toast.makeText(requireActivity(), "the day:$date",Toast.LENGTH_SHORT).show()
            val msdiff = date?.minus(Calendar.getInstance().timeInMillis)
            val daysDiff = msdiff?.let { it1 -> TimeUnit.MILLISECONDS.toDays(it1) }

            Toast.makeText(requireActivity(), "the days:$daysDiff",Toast.LENGTH_SHORT).show()
            binding.datetimeInput.setText(formatDate)
        }
    }


    private fun isValidEntry() = viewModel.isValidEntry(
        binding.nameInput.text.toString(),
        binding.datetimeInput.text.toString()
    )

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}







