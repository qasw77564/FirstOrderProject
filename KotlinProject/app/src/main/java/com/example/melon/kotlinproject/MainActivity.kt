package com.example.melon.kotlinproject

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_bottom.*
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    val TAG: String = "MainActivity"
    val manager = supportFragmentManager
    val list = listOf<String>("1,2,3,4,5")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = "Home"
        textTest.setText("hello")
        home.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this,Home2Activity::class.java))
        })
        navigationTab.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            tabPager.setCurrentItem(item.getOrder())
            when (item.itemId) {
                R.id.navigation_home -> {
                    tabPager
//                    message.setText(R.string.title_home)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_dashboard -> {
//                    message.setText(R.string.title_dashboard)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_notifications -> {
//                    message.setText(R.string.title_notifications)
                    return@OnNavigationItemSelectedListener true
                }
            }

            false
        })
        val adapter = ViewPagerAdapter(getSupportFragmentManager())
        adapter.addFragment(HomeFragment(), "HOME")
        adapter.addFragment(DashboardFragment(), "Dashboard")
        tabPager.adapter = adapter

        tabPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                Log.i(TAG, "")

                when (position) {
                    0 -> {  navigationTab.selectedItemId = R.id.navigation_home  }
                    1 -> {  navigationTab.selectedItemId = R.id.navigation_dashboard  }

                }
            }
        })

    }
}

class ViewPagerAdapter(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager) {

    private val fragments = ArrayList<Fragment>()

    private val titles = ArrayList<String>()

    fun addFragment(fragment: Fragment, title: String) {
        fragments.add(fragment)
        titles.add(title)

    }

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return titles[position]
    }
}
