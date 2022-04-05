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
package com.example.forage.ui.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.example.forage.data.CountDownDao
import com.example.forage.model.CountDown
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
import java.text.SimpleDateFormat
import java.time.Duration
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Shared [ViewModel] to provide data to the [ForageableListFragment], [ForageableDetailFragment],
 * and [AddForageableFragment] and allow for interaction the the [CountDownDao]
 */

// TODO: pass a ForageableDao value as a parameter to the view model constructor
class CountDownViewModel(
    private val countDownDao: CountDownDao
): ViewModel() {

    companion object {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    }

    // TODO: create a property to set to a list of all forageables from the DAO
    val allCountDowns: LiveData<List<CountDown>> = countDownDao.getCountDowns().asLiveData()

    // TODO : create method that takes id: Long as a parameter and retrieve a Forageable from the
    //  database by id via the DAO.
    fun retrieveCountDown(id: Long): LiveData<CountDown> {
        return countDownDao.getCountDown(id).asLiveData()
    }

    fun addCountDown(
        name: String,
        address: String,
        inSeason: Boolean,
        notes: String,
        date: String
    ) {
        val countDown = CountDown(
            name = name,
            address = address,
            haveNotification = inSeason,
            notes = notes,
            date = date
        )

    // TODO: launch a coroutine and call the DAO method to add a Forageable to the database within it
        viewModelScope.launch {
            countDownDao.insert(countDown)
        }

    }

    fun updateCountDown(
        id: Long,
        name: String,
        address: String,
        inSeason: Boolean,
        notes: String,
        date: String
    ) {
        val countDown = CountDown(
            id = id,
            name = name,
            address = address,
            haveNotification = inSeason,
            notes = notes,
            date = date
        )
        viewModelScope.launch(Dispatchers.IO) {
            // TODO: call the DAO method to update a forageable to the database here
            countDownDao.update(countDown)
        }
    }

    fun deleteCountDown(countDown: CountDown) {
        viewModelScope.launch(Dispatchers.IO) {
            // TODO: call the DAO method to delete a forageable to the database here
            countDownDao.delete(countDown)
        }
    }

    fun isValidEntry(name: String, date: String): Boolean {
        return name.isNotBlank() && date.isNotBlank()
    }

    fun daysCount( date : String): String {

        val msdiff = formatter.parse(date).time.minus(Calendar.getInstance().timeInMillis)
        val daysDiff = TimeUnit.MILLISECONDS.toDays(msdiff)
        return "$daysDiff days"
    }


}

// TODO: create a view model factory that takes a ForageableDao as a property and
//  creates a ForageableViewModel

class CountDownViewModelFactory(private val countDownDao: CountDownDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(CountDownViewModel::class.java)) {
            @Suppress("UNCHECKED_CASE")
            return CountDownViewModel(countDownDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }


}
