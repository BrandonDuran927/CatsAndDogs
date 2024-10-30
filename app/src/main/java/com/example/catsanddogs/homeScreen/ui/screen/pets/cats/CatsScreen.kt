package com.example.catsanddogs.homeScreen.ui.screen.pets.cats

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.catsanddogs.common.route.PetDetailsRoute
import com.example.catsanddogs.homeScreen.ui.SharedViewModel
import com.example.catsanddogs.homeScreen.ui.screen.composables.AlertDialogCircularImageDelete
import com.example.catsanddogs.homeScreen.ui.screen.composables.ButtonCard
import com.example.catsanddogs.homeScreen.ui.screen.composables.PetCard
import com.example.catsanddogs.common.utilities.NetworkStatus
import com.example.catsanddogs.common.utilities.NetworkViewModel
import com.example.catsanddogs.homeScreen.ui.screen.composables.AlertDialogDeleteCat
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatsScreen(
    viewModel: CatsScreenViewModel = hiltViewModel(),
    navController: NavController,
    sharedViewModel: SharedViewModel,
    networkViewModel: NetworkViewModel,
) {
    val isDeleting by sharedViewModel.isDelete.observeAsState()
    val stateViewModel by viewModel.state.collectAsStateWithLifecycle()
    val stateSharedViewModel by sharedViewModel.state.collectAsStateWithLifecycle()
    val networkStatus by networkViewModel.networkStatus.collectAsState()
    val firebaseUser by sharedViewModel.currentFirebaseUser.observeAsState()

    val scope = rememberCoroutineScope()
    val localContext = LocalContext.current
    val pullToRefreshState = rememberPullToRefreshState()
    val listState = rememberLazyStaggeredGridState()

    val imageMapLocal = remember(stateViewModel.imagesOfPetLocal) {
        stateViewModel.imagesOfPetLocal.associateBy({ it.id }, { it })
    }

    if (stateSharedViewModel.isLoading) {
        AlertDialogCircularImageDelete()
    }

    // TODO: Is this necessary?
    if (stateViewModel.isError) {
        viewModel.errorSetToFalse()
    }

    LaunchedEffect(Unit) {
        Log.d("USER", "Logged in user: ${firebaseUser?.email} / ${firebaseUser?.uid}")

        viewModel.retrieveOnlineCatImages(context = localContext)  // Images from firebase storage is retrieved
        sharedViewModel.deselectImage()  // Deselect a set image to become null
        viewModel.retrieveLocalCatImages()  // Images from local is retrieved
    }

    LaunchedEffect(key1 = networkStatus, key2 = stateViewModel.imagesOfPetLocal) {
        if (networkStatus == NetworkStatus.Available) {
            Log.d("Network Status", "Available")
            sharedViewModel.setNetworkAvailable()
            viewModel.synchronizeDeletionImage()
        } else {
            Log.d("Network Status", "Unavailable")
            sharedViewModel.setNetworkIsNotAvailable()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(pullToRefreshState.nestedScrollConnection)
    ) {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            state = listState
        ) {
            items(stateViewModel.pets) { cat ->
                val filePath = imageMapLocal[cat.id]
                val imageLocal = stateViewModel.imagesOfPetLocal
                    .find { it.id == filePath?.id }

                PetCard(
                    imageEntity = imageLocal,
                    pet = cat,
                    navController = navController,
                    sharedViewModel = sharedViewModel
                )
            }


            item {
                ButtonCard(
                    addPet = {
                        sharedViewModel.addingNewPet("cat")
                        navController.navigate(PetDetailsRoute)
                    }
                )
            }

//            item {
//                IconButton(
//                    onClick = {
//                        viewModel.truncateTable()
//                    }
//                ) {
//                    Icon(
//                        Icons.Default.Delete,
//                        contentDescription = null
//                    )
//                }
//            }
        }

        PullToRefreshContainer(
            state = pullToRefreshState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-50).dp)
        )
    }

    if (pullToRefreshState.isRefreshing) {
        LaunchedEffect(Unit) {
            scope.launch {
                viewModel.retrieveOnlineCatImages(context = localContext)
                delay(2500L)
                pullToRefreshState.endRefresh()
            }
        }
    }

    if (isDeleting == true) {
        AlertDialogDeleteCat(
            sharedViewModel = sharedViewModel,
            catViewModel = viewModel
        )
    }
}