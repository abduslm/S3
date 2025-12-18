package com.my.kasirtemeji.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.my.kasirtemeji.R

class DashboardFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dasboard_spv, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup data dashboard
        setupDashboardData()
    }

    private fun setupDashboardData() {
        // TODO: Implement dashboard data fetching
        // Contoh: update card data, chart, dll
    }
}