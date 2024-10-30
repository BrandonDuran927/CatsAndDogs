package com.example.catsanddogs.homeScreen.ui.screen.pet

import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults.textFieldColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.catsanddogs.homeScreen.ui.SharedViewModel
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.catsanddogs.homeScreen.ui.screen.composables.AlertDialogCircularAddingImage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetDetails(
    viewModel: PetScreenViewModel = hiltViewModel(),
    sharedViewModel: SharedViewModel,
    innerPadding: PaddingValues,
    onHomeScreen: (String) -> Unit
) {
    val state by viewModel.petState.collectAsStateWithLifecycle()
    val currentPet by sharedViewModel.selectedPet.observeAsState()
    val currentUser by sharedViewModel.currentUser.observeAsState()
    val imageEntity by sharedViewModel.selectedImageEntity.observeAsState()
    val isAddingPet by sharedViewModel.isAddingPet.observeAsState()
    val petType by sharedViewModel.petType.observeAsState()
    val networkAvailability by sharedViewModel.isNetworkAvailable.observeAsState()

    val firebaseUser by sharedViewModel.currentFirebaseUser.observeAsState()

    val coroutineScope = rememberCoroutineScope()
    val localContext = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            if (uri != null && uri.path != null) {
                viewModel.setImage(uri = uri, filePath = uri.toString())
            }
        }
    )


    LaunchedEffect(Unit) {
        Log.d(
            "Selected Pet", "Pet name: ${currentPet?.name}, id: ${currentPet?.id}, image: " +
                    "${imageEntity?.imagePath}, imageId: ${imageEntity?.id}"
        )
        if (currentPet != null) {
            viewModel.setName(currentPet!!.name)
            viewModel.setDescription(currentPet!!.description)
        }
    }

    LaunchedEffect(state.uri) {
        Log.d("State URI", "is imageEnt null or not? ${imageEntity?.isImageNull}")
        // Retrieve the URI then add it to the ROOM and firestore using a viewmodel
        if (state.uri != null && currentPet?.pet_type?.isNotEmpty() == true) {
            Log.d("State URI", "State uri has changed and id not null")
            //Update here
            viewModel.updateImage(id = currentPet!!.id, context = localContext, uri = state.uri!!)
        } else if (state.uri != null) {
            Log.d("State URI", "id(selectedPet): ${state.selectedPet.id}")
            viewModel.updateImage(id = state.selectedPet.id, context = localContext, uri = state.uri!!)
        }
    }


    if (state.isLoading) {
        AlertDialogCircularAddingImage()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        item {
            Card(
                modifier = Modifier
                    .aspectRatio(1f)
                    .padding(30.dp)
                    .clickable {
                        launcher.launch("image/*")  // Pick an image (uri) from local storage
                    }
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (state.uri != null) {
                        AsyncImage(
                            model = state.uri,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxHeight(),
                            contentScale = ContentScale.Crop,
                        )
                    } else if (imageEntity?.imagePath != null) {
                        AsyncImage(
                            model = imageEntity!!.imagePath?.toUri(),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxHeight(),
                            contentScale = ContentScale.Crop,
                        )
                    } else {
                        Row {
                            Icon(
                                Icons.Default.AddCircle,
                                contentDescription = "Add image",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Add Image",
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = if (isAddingPet == true) state.name else state.name,
                    onValueChange = { newName ->
                        viewModel.setName(newName)
                    },
                    placeholder = {
                        Text(
                            modifier = Modifier.alpha(0.6f),
                            text = "Name of pet",
                            fontSize = 42.sp
                        )
                    },
                    textStyle = TextStyle.Default.copy(
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    colors = textFieldColors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedIndicatorColor = MaterialTheme.colorScheme.surface,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.surface,
                        disabledContainerColor = MaterialTheme.colorScheme.surface,
                        disabledIndicatorColor = MaterialTheme.colorScheme.surface,
                        disabledTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    enabled = state.isEditingInfo,
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                if (state.isEditingInfo) {
                    IconButton(
                        onClick = {
                            viewModel.doneEditing()
                            if (isAddingPet == true && state.name.isNotEmpty() && state.description.isNotEmpty() && petType != null) {
                                coroutineScope.launch {
                                    viewModel.addImageAndPet(
                                        uid = firebaseUser!!.uid,
                                        context = localContext,
                                        petType = petType!!,
                                        name = state.name,
                                        description = state.description,
                                        networkStatus = networkAvailability
                                    )
                                    // must return a pet
                                    sharedViewModel.doneAdding(state.selectedPet)
                                }
                            }

                            if (currentPet != null) {
                                viewModel.overwritePet(
                                    firebaseUser!!.uid,
                                    currentPet!!,
                                    state.name,
                                    state.description
                                )
                            }
                        }
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null
                        )
                    }
                } else {
                    IconButton(
                        onClick = {
                            viewModel.editInfo()
                        },
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = null
                        )
                    }
                }
                Spacer(Modifier.width(14.dp))
            }
        }

        item {
            TextField(
                value = if (isAddingPet == true) state.description else state.description,
                onValueChange = { description ->
                    viewModel.setDescription(description)
                },
                placeholder = {
                    Text(modifier = Modifier.alpha(0.6f), text = "Description..", fontSize = 16.sp)
                },
                textStyle = TextStyle.Default.copy(
                    fontSize = 16.sp
                ),
                colors = textFieldColors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = MaterialTheme.colorScheme.surface,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.surface,
                    disabledContainerColor = MaterialTheme.colorScheme.surface,
                    disabledIndicatorColor = MaterialTheme.colorScheme.surface,
                    disabledTextColor = MaterialTheme.colorScheme.onSurface
                ),
                enabled = state.isEditingInfo
            )
        }
    }
}


