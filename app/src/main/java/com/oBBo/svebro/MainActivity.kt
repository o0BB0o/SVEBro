package com.oBBo.svebro

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.oBBo.svebro.ui.duel.DuelFragment
import com.oBBo.svebro.R
import com.oBBo.svebro.ui.diy.DIYFragment
import com.oBBo.svebro.ui.preduel.PreDuelFragment

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

//        val preDuel = PreDuelFragment()
//        val diy = DIYFragment()
//        val duel = DuelFragment()
//        if(savedInstanceState == null) { // initial transaction should be wrapped like this
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.main_fragment_container, preDuel)
//                .commitAllowingStateLoss()
//        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
