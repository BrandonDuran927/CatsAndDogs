package com.example.catsanddogs.app

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.catsanddogs.R
import com.example.catsanddogs.common.route.HomeScreenRoute
import com.example.catsanddogs.common.route.LoginScreenRoute
import com.example.catsanddogs.common.route.PetDetailsRoute
import com.example.catsanddogs.homeScreen.ui.theme.CatsAndDogsTheme
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlin.system.exitProcess

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Thread.sleep(3000)
        installSplashScreen()

        enableEdgeToEdge()
        setContent {
            CatsAndDogsTheme {
                val navController = rememberNavController()
                val currentBackStackEntry by navController.currentBackStackEntryAsState()
                val showBackIcon =
                    currentBackStackEntry?.destination?.route != HomeScreenRoute.ROUTE && currentBackStackEntry?.destination?.route != LoginScreenRoute.ROUTE

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "My cats and dogs",
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Icon(
                                        painter = painterResource(R.drawable.paws),
                                        modifier = Modifier.size(24.dp),
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.surfaceTint
                                    )
                                }

                            },
                            navigationIcon = {
                                if (showBackIcon) {
                                    IconButton(
                                        onClick = {
                                            navController.popBackStack()
                                        }
                                    ) {
                                        Icon(
                                            Icons.AutoMirrored.Default.ArrowBack,
                                            contentDescription = "Go back",
                                            tint = MaterialTheme.colorScheme.surfaceTint
                                        )
                                    }
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    NavGraph(navController, innerPadding)
                }
            }
        }

        this.actionBar?.hide()
    }
}



