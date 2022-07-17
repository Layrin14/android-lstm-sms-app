package com.layrin.smsapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Telephony
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.core.app.ActivityCompat
import androidx.core.view.get
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.viewpager2.widget.ViewPager2
import com.layrin.smsapp.common.requestDefaultApp
import com.layrin.smsapp.databinding.ActivityMainBinding
import com.layrin.smsapp.ui.main.ViewPagerAdapter

class MainActivity : AppCompatActivity() {

    companion object {
        val ARR_PERMS = arrayOf(
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_SMS,
            Manifest.permission.SEND_SMS,
            Manifest.permission.RECEIVE_SMS
        )
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private val onDefaultAppResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (PackageManager.PERMISSION_DENIED in Array(ARR_PERMS.size) {
                    ActivityCompat.checkSelfPermission(this, ARR_PERMS[it])
                }) {
                ActivityCompat.requestPermissions(this, ARR_PERMS, 0)
            }
        }

    private var actionMode: ActionMode? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHost =
            supportFragmentManager.findFragmentById(binding.fragmentContainerView.id) as NavHostFragment
        navController = navHost.navController

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.conversationFragment, R.id.newConversationFragment -> changeFragment()
                R.id.startFragment -> {
                    changeFragment()
                    binding.appBarLayout.visibility = View.GONE
                }
                else -> {
                    binding.navView.visibility = View.VISIBLE
                    binding.fabNewMsg.visibility = View.VISIBLE
                    binding.mainViewpager.visibility = View.VISIBLE
                    binding.appBarLayout.visibility = View.VISIBLE
                }
            }
        }

        setupViewPager()
        setSupportActionBar(binding.toolbar)
        setupActionBarWithNavController(navController)

        when {
            packageName != Telephony.Sms.getDefaultSmsPackage(this) -> requestDefaultApp(
                onDefaultAppResult)
            ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED -> {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_CONTACTS),
                    0
                )
            }
        }

        binding.fabNewMsg.setOnClickListener {
            findNavController(binding.fragmentContainerView.id)
                .navigate(R.id.action_mainFragment_to_newConversationFragment)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.contains(PackageManager.PERMISSION_DENIED)) {
            finish()
            return
        }
    }

    private fun changeFragment() {
        binding.apply {
            mainViewpager.visibility = View.GONE
            navView.visibility = View.GONE
            fabNewMsg.visibility = View.GONE
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun setupBottomNavigation() {
        binding.navView.setupWithNavController(navController)
        for (i in 0..2) {
            val stringResource = Array(3) {
                val label = resources.getStringArray(R.array.fragment_labels)
                label[it]
            }
            binding.navView.menu.add(
                0, i, 400 + i, stringResource[i]
            )
            val iconResource = resources.obtainTypedArray(R.array.fragment_icons)
            binding.navView.menu[i].setIcon(iconResource.getResourceId(i, 0))
            iconResource.recycle()

            val badge = binding.navView.getOrCreateBadge(i)
            badge.apply {
                isVisible = true
                number = 99
            }
        }

        binding.navView.setOnItemSelectedListener {
            binding.mainViewpager.setCurrentItem(
                it.itemId, false
            )
            true
        }
    }

    private fun setupViewPager() {
        // setup bottom navigation with ViewPager2
        setupBottomNavigation()

        // setup ViewPager2
        binding.mainViewpager.apply {
            adapter = ViewPagerAdapter(
                arrayOf(0, 1, 2),
                supportFragmentManager,
                lifecycle
            )
            offscreenPageLimit = 3

            // set bottom navigation when page change
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    actionMode?.finish()
                    binding.navView.selectedItemId = position
                    super.onPageSelected(position)
                }
            })
        }
    }

    override fun onSupportActionModeStarted(mode: ActionMode) {
        actionMode = mode
        super.onSupportActionModeStarted(mode)
    }

    override fun onSupportActionModeFinished(mode: ActionMode) {
        actionMode = null
        super.onSupportActionModeFinished(mode)
    }

}