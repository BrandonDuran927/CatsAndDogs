package com.example.catsanddogs.homeScreen.ui.screen.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.catsanddogs.R
import com.example.catsanddogs.common.route.PetDetailsRoute
import com.example.catsanddogs.homeScreen.data.local.ImageEntity
import com.example.catsanddogs.homeScreen.domain.model.Pet
import com.example.catsanddogs.homeScreen.ui.SharedViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PetCard(
    imageEntity: ImageEntity?,
    pet: Pet,
    navController: NavController,
    sharedViewModel: SharedViewModel
) {
    val haptics = LocalHapticFeedback.current
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(15.dp)
            .combinedClickable(
                onClick = {
                    sharedViewModel.selectPet(pet, petID = pet.id)
                    navController.navigate(PetDetailsRoute)
                },
                onLongClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    sharedViewModel.deletingPet(pet = pet, petID = pet.id)
                },
            )
    ) {
        Box {
            PetCardImage(imageEntity = imageEntity, petType = pet.pet_type)
            Text(
                text = pet.name,
                color = Color.Black,
                modifier = Modifier.align(Alignment.BottomCenter),
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

    }
}

@Composable
fun PetCardImage(imageEntity: ImageEntity?, petType: String) {
    when {
        imageEntity?.imagePath != null -> {
            AsyncImage(
                modifier = Modifier.fillMaxSize(),
                model = imageEntity.imagePath.toUri(),
                contentScale = ContentScale.Crop,
                contentDescription = "Image of pet"
            )
        }

        petType == "cat" -> {
            Image(
                painter = painterResource(R.drawable.cat),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        }

        else -> {
            Image(
                painter = painterResource(R.drawable.dog),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun ButtonCard(
    addPet: () -> Unit
) {
    ElevatedButton(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(15.dp),
        onClick = addPet,
        shape = RoundedCornerShape(7)
    ) {
        Icon(
            Icons.Default.Add,
            contentDescription = null,
            modifier = Modifier
                .size(100.dp)
                .alpha(0.6f)
        )
    }
}