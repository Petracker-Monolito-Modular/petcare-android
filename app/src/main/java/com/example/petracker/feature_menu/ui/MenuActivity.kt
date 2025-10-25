package com.example.petracker.feature_menu.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.petracker.R
import com.example.petracker.feature_pets.ui.PetCreateActivity
import com.example.petracker.feature_pets.ui.PetsListActivity
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MenuActivity: ComponentActivity() {
    private var autoScrollJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val vp = findViewById<ViewPager2>(R.id.viewPager)
        vp.adapter = ImageSliderAdapter(listOf(R.drawable.slide1, R.drawable.slide2, R.drawable.slide3))

        findViewById<android.widget.Button>(R.id.btnAddPet).setOnClickListener {
            startActivity(Intent(this, PetCreateActivity::class.java))
        }
        findViewById<android.widget.Button>(R.id.btnListPets).setOnClickListener {
            startActivity(Intent(this, PetsListActivity::class.java))
        }
        autoScrollJob = lifecycleScope.launch {
            while (true) {
                delay(3000)
                vp.currentItem = (vp.currentItem + 1) % (vp.adapter?.itemCount ?: 1)
            }
        }
    }
    override fun onDestroy() { autoScrollJob?.cancel(); super.onDestroy() }
}