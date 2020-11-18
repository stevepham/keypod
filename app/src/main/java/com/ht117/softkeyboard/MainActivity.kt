package com.ht117.softkeyboard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.ht117.keyboard.KeyboardHandler
import com.ht117.softkeyboard.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater).apply {
            setContentView(root)
            pager.adapter = TabAdapter(supportFragmentManager, lifecycle)

            TabLayoutMediator(tabs, pager) { tab, pos ->
                tab.text = "Tab ${pos+1}"
            }.attach()

            pager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    if (position != 2) {
                        KeyboardHandler.watch(this@MainActivity)
                    } else {
                        KeyboardHandler.unwatch()
                    }
                }
            })
        }
    }

    inner class TabAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle): FragmentStateAdapter(fragmentManager, lifecycle) {

        override fun getItemCount() = 3

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> Fragment(R.layout.fragment_one)
                1 -> Fragment(R.layout.fragment_two)
                2 -> Fragment(R.layout.fragment_three)
                else -> Fragment(R.layout.fragment_one)
            }
        }
    }
}