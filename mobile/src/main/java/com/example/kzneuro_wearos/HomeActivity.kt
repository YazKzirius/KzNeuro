package com.example.kzneuro_wearos

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {
    var fragment: Fragment = Dashboard()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //This function creates new fragments and adds basic functionality
        createFragments()
    }
    //This function creates new fragments based of the navigation bar
    fun createFragments() {
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        //Setting Dashboard to default
        supportFragmentManager.beginTransaction()
            .replace(R.id.main, fragment)
            .commit()
        bottomNavigationView.setOnItemSelectedListener { item ->
            val id = item.itemId
            fragment = when (id) {
                R.id.nav_dashboard -> Dashboard()
                R.id.nav_reports -> Reports()
                R.id.nav_settings -> Settings()
                else -> Dashboard()
            }
            if (fragment != null) {
                openFragment(fragment)
                return@setOnItemSelectedListener true
            } else {
                return@setOnItemSelectedListener false
            }
        }
    }
    //This function opens a fragment based of navigation bar
    private fun openFragment(fragment: Fragment) {
        // Example: Replace fragment or perform any action
        supportFragmentManager.beginTransaction()
            .replace(R.id.main, fragment)
            .commit()
    }

}