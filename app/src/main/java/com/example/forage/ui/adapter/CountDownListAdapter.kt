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
package com.example.forage.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.forage.databinding.ListItemCountdownBinding
import com.example.forage.model.CountDown
import com.example.forage.ui.viewmodel.CountDownViewModel

/**
 * ListAdapter for the list of [CountDown]s retrieved from the database
 */
class CountDownListAdapter(
    private val clickListener: (CountDown) -> Unit,
    private val shareLifecycleOwner: LifecycleOwner,
    private val viewModel: CountDownViewModel
) : ListAdapter<CountDown, CountDownListAdapter.CountDownViewHolder>(DiffCallback) {

    inner class CountDownViewHolder(
        private var binding: ListItemCountdownBinding
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(countDown: CountDown) {
            binding.apply {
                this.countDown = countDown
                executePendingBindings()
                this.setViewModel = viewModel
                this.lifecycleOwner = shareLifecycleOwner
            }
        }
    }

    companion object DiffCallback: DiffUtil.ItemCallback<CountDown>() {
        override fun areItemsTheSame(oldItem: CountDown, newItem: CountDown): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CountDown, newItem: CountDown): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountDownViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return CountDownViewHolder(
            ListItemCountdownBinding.inflate(layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CountDownViewHolder, position: Int) {
        val countDown = getItem(position)
        holder.itemView.setOnClickListener{
            clickListener(countDown)
        }
        holder.bind(countDown)
    }
}
