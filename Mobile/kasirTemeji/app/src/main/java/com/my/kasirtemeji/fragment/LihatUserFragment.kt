package com.my.kasirtemeji.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.my.kasirtemeji.R
import com.my.kasirtemeji.api.RetrofitInstance
import com.my.kasirtemeji.models.User
import com.my.kasirtemeji.util.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class LihatUserFragment : Fragment() {

    private lateinit var sessionManager: SessionManager
    private lateinit var tableLayout: TableLayout
    private var userList: List<User> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.lihatuser_owner, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())

        val cardView = view.findViewById<com.google.android.material.card.MaterialCardView>(
            R.id.card_table_user
        )

        tableLayout = cardView.findViewById(R.id.table_layout_user)

        if (tableLayout == null) {
            tableLayout = view.findViewById(R.id.table_layout_user)
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


    private fun populateTable() {
        // Clear existing rows except header
        while (tableLayout.childCount > 1) {
            tableLayout.removeViewAt(1)
        }

        userList.forEach { user ->
            val tableRow = TableRow(requireContext()).apply {
                layoutParams = TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT
                )
                setBackgroundResource(R.color.white)
                setPadding(0, 8.dpToPx(), 0, 8.dpToPx())
            }

            // Full Name
            tableRow.addView(createTextView(user.nama ?: "-"))

            // Email
            tableRow.addView(createTextView(user.email ?: "-"))

            // Username
            tableRow.addView(createTextView(user.username))

            // Role
            val roleView = createTextView(user.level)
            //roleView.setTextColor(getRoleColor(user.level))
            tableRow.addView(roleView)

            // Joined Date
            //val joinedDate = formatDate(user.createdAt)
            //tableRow.addView(createTextView(joinedDate))

            // Last Active
            //val lastActive = formatDate(user.updatedAt)
            //tableRow.addView(createTextView(lastActive))

            tableLayout.addView(tableRow)
        }
    }

    private fun createTextView(text: String): TextView {
        return TextView(requireContext()).apply {
            this.text = text
            textSize = 12f
            setPadding(12.dpToPx(), 8.dpToPx(), 12.dpToPx(), 8.dpToPx())
        }
    }
/*
    private fun getRoleColor(role: String): Int {
        return when (role.uppercase()) {
            "OWNER" -> resources.getColor(R.color.owner_color, null)
            "SPV" -> resources.getColor(R.color.spv_color, null)
            "KASIR" -> resources.getColor(R.color.kasir_color, null)
            "DAPUR" -> resources.getColor(R.color.dapur_color, null)
            else -> resources.getColor(R.color.gray, null)
        }slm    }
*/
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