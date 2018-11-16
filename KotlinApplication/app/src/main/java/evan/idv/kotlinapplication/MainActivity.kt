package evan.idv.kotlinapplication

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import evan.idv.kotlinapplication.main.tab.DashboardFragment
import evan.idv.kotlinapplication.main.tab.HomeFragment
import evan.idv.kotlinapplication.main.tab.NotifyFragment
import evan.idv.kotlinapplication.main.tab.OtherFragment
import evan.idv.kotlinapplication.menu.CameraFragment
import evan.idv.kotlinapplication.menu.GalleryFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {
    //等於this
    companion object {
        private val TAG: String = MainActivity::class.java.simpleName as String
        private var lastShowFragment: Int = 0
        private var lastShowMenuFragment: Int = 0
        private lateinit var selectedMenuListener: OnNavigationItemSelectedListener
        private lateinit var backListener: View.OnClickListener
        private lateinit var toggle: ActionBarDrawerToggle
        private lateinit var fragments: List<Fragment>
        private lateinit var menuFragments: List<Fragment>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        Companion.toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(Companion.toggle)
        Companion.toggle.syncState()

        initFragments()
        initListener()

        nav_view.setNavigationItemSelectedListener(Companion.selectedMenuListener)

        navigationTab.setOnNavigationItemSelectedListener({ item ->
            val current: Boolean? = (item.order == Companion.lastShowFragment).takeIf { !it }.apply {
                switchTab(Companion.lastShowFragment, item.order)
                Companion.lastShowFragment = item.order

            }
            current != null && current != true
        })
    }

    private fun switchMenu(lastIndex: Int, index: Int) {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.hide(Companion.fragments[Companion.lastShowFragment])
        transaction.hide(Companion.menuFragments[lastIndex])

        if (!Companion.menuFragments[index].isAdded) {
            transaction.add(R.id.fragment_container, Companion.menuFragments[index])
        }

        (index == Companion.lastShowMenuFragment).takeIf { !it }?.apply {
            menuFragments[index].onResume()
        }

        transaction.show(Companion.menuFragments[index]).commitAllowingStateLoss()

    }

    private fun switchTab(lastIndex: Int, index: Int) {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.hide(Companion.fragments[lastIndex])
        if (lastIndex < Companion.menuFragments.size) {
            transaction.hide(Companion.menuFragments[lastIndex])
        }

        if (!Companion.fragments[index].isAdded) {
            transaction.add(R.id.fragment_container, Companion.fragments[index])
        }

        (index == Companion.lastShowFragment).takeIf { !it }?.apply {
            fragments[index].onResume()
        }

        transaction.show(Companion.fragments[index]).commitAllowingStateLoss()
    }


    private fun initFragments() {
        val homeFragment: Fragment = HomeFragment()
        val dashboardFragment: Fragment = DashboardFragment()
        val notifyFragment: Fragment = NotifyFragment()
        val otherFragment : Fragment = OtherFragment()
        HomeFragment::class.java.newInstance()
        Companion.fragments = listOf(homeFragment, dashboardFragment, notifyFragment, otherFragment)
        Companion.lastShowFragment = 0

        supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, homeFragment)
                .show(homeFragment)
                .commit()

        // init Menu Fragment
        val cameraFragment: Fragment = CameraFragment()
        val galleryFragment : Fragment = GalleryFragment()
        Companion.menuFragments = listOf(cameraFragment, galleryFragment)

    }

    private fun initListener() {
        Companion.selectedMenuListener = OnNavigationItemSelectedListener { item ->
            val current: Boolean? = (item.order == Companion.lastShowMenuFragment).takeIf { !it }.apply {
                switchMenu(Companion.lastShowMenuFragment, item.order)
                Companion.lastShowMenuFragment = item.order
                navigationTab.visibility = View.GONE
                drawer_layout.closeDrawer(GravityCompat.START)
                drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                toolbar.navigationIcon = getDrawable(android.R.drawable.ic_menu_add)
                toolbar.setNavigationOnClickListener (Companion.backListener)
            }
            current != null && current != true
            true
        }

        Companion.backListener = View.OnClickListener { view ->
            switchTab(Companion.lastShowMenuFragment, Companion.lastShowFragment)
            navigationTab.visibility = View.VISIBLE
            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)

            toolbar.setNavigationOnClickListener(null)
            Companion.toggle = ActionBarDrawerToggle(this@MainActivity, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
            drawer_layout.addDrawerListener(Companion.toggle)
            Companion.toggle.syncState()
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.i(TAG, requestCode.toString())

    }


    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

}

