package com.example.catsanddogs.homeScreen.ui

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.catsanddogs.R
import com.example.catsanddogs.common.route.HomeScreenRoute
import com.example.catsanddogs.homeScreen.ui.screen.pets.cats.CatsScreen
import com.example.catsanddogs.homeScreen.ui.screen.pets.dogs.DogsScreen
import com.example.catsanddogs.common.utilities.NetworkViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    username: String,
    navController: NavController,
    sharedViewModel: SharedViewModel,
    networkViewModel: NetworkViewModel,
    innerPadding: PaddingValues
) {
    val pagerState = rememberPagerState {
        2
    }
    val scope = rememberCoroutineScope()

    val context = LocalContext.current
    val isBackPressed = remember { mutableStateOf(false) }
    val enabled = remember { mutableStateOf(true) }

    BackHandler(enabled.value && !isBackPressed.value) {
        isBackPressed.value = true
        Toast.makeText(context, "Press back again to exit", Toast.LENGTH_SHORT).show()
        scope.launch {
            delay(2000L)
            isBackPressed.value = false
        }
    }

    LaunchedEffect(Unit) {
        sharedViewModel.setUser(username = username)
        Log.d("Username", "Hello $username!")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        TabRow(
            selectedTabIndex = pagerState.pageCount,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                    height = 2.dp,
                    color = MaterialTheme.colorScheme.surfaceTint
                )
            }
        ) {
            Tab(
                selected = pagerState.pageCount == 0,
                text = {
                    Text("Cats")
                },
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(0)
                    }
                }
            )
            Tab(
                selected = pagerState.pageCount == 0,
                text = {
                    Text("Dogs")
                },
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(1)
                    }
                }
            )
        }

        HorizontalPager(
            state = pagerState
        ) { page: Int ->
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                if (page == 0) {
                    CatsScreen(
                        navController = navController,
                        sharedViewModel = sharedViewModel,
                        networkViewModel = networkViewModel,
                    )
                } else {
                    DogsScreen(
                        navController = navController,
                        sharedViewModel = sharedViewModel,
                        networkViewModel = networkViewModel,
                    )
                }
            }
        }
    }

}