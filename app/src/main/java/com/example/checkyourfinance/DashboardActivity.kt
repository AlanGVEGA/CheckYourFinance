package com.example.checkyourfinance

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.dashboard_root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        bindClicks()
    }

    private fun bindClicks() {
        findViewById<View>(R.id.button_notifications).setOnClickListener {
            Toast.makeText(this, R.string.toast_notifications_coming_soon, Toast.LENGTH_SHORT).show()
        }

        findViewById<View>(R.id.button_profile).setOnClickListener {
            Toast.makeText(this, R.string.toast_profile_settings_coming_soon, Toast.LENGTH_SHORT).show()
        }

        findViewById<View>(R.id.text_spending_view_all).setOnClickListener {
            Toast.makeText(this, R.string.toast_categories_view_coming_soon, Toast.LENGTH_SHORT).show()
        }

        findViewById<View>(R.id.text_recent_view_all).setOnClickListener {
            startActivity(Intent(this, ExpenseListActivity::class.java))
        }

        findViewById<View>(R.id.nav_home).setOnClickListener {
            // Already on Home (active)
        }
        findViewById<View>(R.id.nav_transactions).setOnClickListener {
            startActivity(Intent(this, ExpenseListActivity::class.java))
        }
        findViewById<View>(R.id.nav_add).setOnClickListener {
            Toast.makeText(this, R.string.toast_create_expense_coming_soon, Toast.LENGTH_SHORT).show()
        }
        findViewById<View>(R.id.nav_budget).setOnClickListener {
            Toast.makeText(this, R.string.toast_budget_view_coming_soon, Toast.LENGTH_SHORT).show()
        }
        findViewById<View>(R.id.nav_profile).setOnClickListener {
            Toast.makeText(this, R.string.toast_profile_view_coming_soon, Toast.LENGTH_SHORT).show()
        }
    }
}

