package com.example.catsanddogs.homeScreen.ui.screen.composables

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.catsanddogs.homeScreen.ui.SharedViewModel
import com.example.catsanddogs.homeScreen.ui.screen.pets.cats.CatsScreenViewModel
import com.example.catsanddogs.homeScreen.ui.screen.pets.dogs.DogsScreenViewModel
import kotlinx.coroutines.launch

@Composable
fun AlertDialogDeleteCat(
    sharedViewModel: SharedViewModel,
    catViewModel: CatsScreenViewModel
) {
    val isDelete by sharedViewModel.isDelete.observeAsState()
    val selectedPetToDelete by sharedViewModel.selectedPetToDelete.observeAsState()
    val coroutineScope = rememberCoroutineScope()
    val localContext = LocalContext.current

    LaunchedEffect(Unit) {
        Log.d("Selected Pet", "null? $selectedPetToDelete")
    }

    if (isDelete == true) {
        AlertDialog(
            onDismissRequest = {
                sharedViewModel.doneDeleting()
            },
            confirmButton = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                ) {
                    Text(
                        text = "Do you want to delete this pet?",
                        fontSize = 24.sp,
                        lineHeight = 24.sp,
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                sharedViewModel.doneDeleting()
                            },
                            colors = buttonColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Text(
                                text = "No",
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(Modifier.width(16.dp))

                        Button(
                            onClick = {
                                if (selectedPetToDelete?.pet_type == "cat") {
                                    sharedViewModel.deletePetWithImageFromROOM(pet = selectedPetToDelete!!)
                                    catViewModel.synchronizeDeletionImage()
                                    catViewModel.retrieveLocalCatImages()  // Images from local is retrieved
                                }
                            }
                        ) {
                            Text("Yes")
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun AlertDialogDeleteDog(
    sharedViewModel: SharedViewModel,
    dogViewModel: DogsScreenViewModel
) {
    val isDelete by sharedViewModel.isDelete.observeAsState()
    val selectedPetToDelete by sharedViewModel.selectedPetToDelete.observeAsState()
    val coroutineScope = rememberCoroutineScope()
    val localContext = LocalContext.current

    LaunchedEffect(Unit) {
        Log.d("Selected Pet", "null? $selectedPetToDelete")
    }

    if (isDelete == true) {
        AlertDialog(
            onDismissRequest = {
                sharedViewModel.doneDeleting()
            },
            confirmButton = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                ) {
                    Text(
                        text = "Do you want to delete this pet?",
                        fontSize = 24.sp,
                        lineHeight = 24.sp,
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                sharedViewModel.doneDeleting()
                            },
                            colors = buttonColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Text(
                                text = "No",
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(Modifier.width(16.dp))

                        Button(
                            onClick = {
                                //FIXME: There's a delay in deleting the entity itself..
                                if (selectedPetToDelete?.pet_type == "dog") {
                                    sharedViewModel.deletePetWithImageFromROOM(pet = selectedPetToDelete!!)
                                    dogViewModel.synchronizeDeletionImage()
                                    dogViewModel.retrieveLocalDogImages()  // Images from local is retrieved
                                }
                            }
                        ) {
                            Text("Yes")
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun AlertDialogCircularAddingImage() {
    AlertDialog(
        onDismissRequest = {},
        confirmButton = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
                Spacer(Modifier.height(20.dp))
                Text(
                    text = "Adding, please wait",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 22.sp
                )
            }
        }
    )
}

@Composable
fun AlertDialogCircularImageDelete() {
    AlertDialog(
        onDismissRequest = {},
        confirmButton = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
                Spacer(Modifier.height(20.dp))
                Text(
                    text = "Deleting...",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 22.sp
                )
            }
        }
    )
}




