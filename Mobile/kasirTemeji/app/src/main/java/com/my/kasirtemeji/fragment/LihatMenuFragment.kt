package com.my.kasirtemeji.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.my.kasirtemeji.R
import com.my.kasirtemeji.util.SessionManager
import java.text.SimpleDateFormat
import java.util.*

class LihatMenuFragment : Fragment() {

    private lateinit var sessionManager: SessionManager
    private lateinit var tableLayout: android.widget.TableLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.lihatmenu_owner, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())

        // Ambil CardView
        val cardView = view.findViewById<com.google.android.material.card.MaterialCardView>(
            R.id.card_table_menu
        )

        // Ambil TableLayout langsung dari ID
        tableLayout = cardView.findViewById(R.id.table_layout_menu)

        // Jika masih null, fallback cari dari root view
        if (tableLayout == null) {
            tableLayout = view.findViewById(R.id.table_layout_menu)
        }

    }

    private fun findTableLayout(view: View): android.widget.TableLayout? {
        return if (view is android.widget.TableLayout) {
            view
        } else if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val child = view.getChildAt(i)
                val found = findTableLayout(child)
                if (found != null) return found
            }
            null
        } else {
            null
        }
    }


    private fun createTextView(text: String, center: Boolean = false): TextView {
        return TextView(requireContext()).apply {
            this.text = text
            textSize = 12f
            setPadding(12.dpToPx(), 8.dpToPx(), 12.dpToPx(), 8.dpToPx())
            if (center) {
                gravity = android.view.Gravity.CENTER
            }
        }
    }

    private fun formatRupiah(amount: Int): String {
        return "Rp ${String.format("%,d", amount)}"
    }

    /*
    private fun getStockColor(stock: Int): Int {
        return if (stock <= 10) {
            resources.getColor(R.color.red, null)
        } else if (stock <= 20) {
            resources.getColor(R.color.orange, null)
        } else {
            resources.getColor(R.color.green, null)
        }
    }
*/
    private fun getStatusColor(status: Int): Int {
        return if (status == 1) {
            resources.getColor(R.color.green, null)
        } else {
            resources.getColor(R.color.red, null)
        }
    }

    private fun formatDate(dateString: String?): String {
        return if (dateString.isNullOrEmpty()) "-" else {
            try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val date = inputFormat.parse(dateString)
                outputFormat.format(date ?: Date())
            } catch (e: Exception) {
                dateString
            }
        }
    }

    private fun Int.dpToPx(): Int {
        val density = resources.displayMetrics.density
        return (this * density).toInt()
    }


    private fun showErrorMessage(message: String) {
        android.widget.Toast.makeText(requireContext(), message, android.widget.Toast.LENGTH_SHORT).show()
    }
}