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
import evan.idv.kotlinapplication.menu.CameraFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private val TAG: String = MainActivity::class.java.simpleName as String

    private var lastShowFragment: Int = 0
    private var lastShowMenuFragment: Int = 0

    private lateinit var selectedMenuListener: OnNavigationItemSelectedListener
    private lateinit var backListener: View.OnClickListener

    private lateinit var toggle: ActionBarDrawerToggle

    private lateinit var fragments: List<Fragment>
    private lateinit var menuFragments: List<Fragment>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        this.toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(this.toggle)
        this.toggle.syncState()

        initFragments()
        initListener()

        nav_view.setNavigationItemSelectedListener(this.selectedMenuListener)

        navigationTab.setOnNavigationItemSelectedListener({ item ->
            val current: Boolean? = (item.order == this.lastShowFragment).takeIf { !it }.apply {
                switchTab(this@MainActivity.lastShowFragment, item.order)
                this@MainActivity.lastShowFragment = item.order

            }
            current != null && current != true
        })
    }

    private fun switchMenu(lastIndex: Int, index: Int) {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.hide(this.fragments[this@MainActivity.lastShowFragment])
        transaction.hide(this.menuFragments[lastIndex])

        if (!this.menuFragments[index].isAdded) {
            transaction.add(R.id.fragment_container, this.menuFragments[index])
        }

        (index == this.lastShowMenuFragment).takeIf { !it }?.apply {
            menuFragments[index].onResume()
        }

        transaction.show(this.menuFragments[index]).commitAllowingStateLoss()

    }

    private fun switchTab(lastIndex: Int, index: Int) {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.hide(this.fragments[lastIndex])
        if (lastIndex < this.menuFragments.size) {
            transaction.hide(this.menuFragments[lastIndex])
        }

        if (!this.fragments[index].isAdded) {
            transaction.add(R.id.fragment_container, this.fragments[index])
        }

        (index == this.lastShowFragment).takeIf { !it }?.apply {
            fragments[index].onResume()
        }

        transaction.show(this.fragments[index]).commitAllowingStateLoss()
    }

    private fun initFragments() {
        val homeFragment: Fragment = HomeFragment()
        val dashboardFragment: Fragment = DashboardFragment()
        val notifyFragment: Fragment = NotifyFragment()
        HomeFragment::class.java.newInstance()
        this.fragments = listOf(homeFragment, dashboardFragment, notifyFragment)
        this.lastShowFragment = 0

        supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, homeFragment)
                .show(homeFragment)
                .commit()

        // init Menu Fragment
        val cameraFragment: Fragment = CameraFragment()
        this.menuFragments = listOf(cameraFragment)

    }

    private fun initListener() {
        this.selectedMenuListener = OnNavigationItemSelectedListener { item ->
            val current: Boolean? = (item.order == this.lastShowMenuFragment).takeIf { !it }.apply {
                switchMenu(this@MainActivity.lastShowMenuFragment, item.order)
                this@MainActivity.lastShowMenuFragment = item.order
                navigationTab.visibility = View.GONE
                drawer_layout.closeDrawer(GravityCompat.START)
                drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                toolbar.navigationIcon = getDrawable(android.R.drawable.ic_menu_add)
                toolbar.setNavigationOnClickListener (this@MainActivity.backListener)
            }
            current != null && current != true
            true
        }

        this.backListener = View.OnClickListener { view ->
            switchTab(this@MainActivity.lastShowMenuFragment, this@MainActivity.lastShowFragment)
            navigationTab.visibility = View.VISIBLE
            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)

            toolbar.setNavigationOnClickListener(null)
            this@MainActivity.toggle = ActionBarDrawerToggle(this@MainActivity, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
            drawer_layout.addDrawerListener(this@MainActivity.toggle)
            this@MainActivity.toggle.syncState()
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

