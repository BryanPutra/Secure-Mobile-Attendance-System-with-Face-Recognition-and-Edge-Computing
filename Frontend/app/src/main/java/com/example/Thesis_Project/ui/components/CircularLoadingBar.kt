package com.example.Thesis_Project.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.zIndex
import com.example.Thesis_Project.R

@Composable
fun CircularLoadingBar() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .alpha(0.5f)
            .background(colorResource(id = R.color.black))
            .zIndex(2f)
            .clickable {

            },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(
                modifier = Modifier,
                color = colorResource(id = R.color.blue_500)
            )
        }
    }
}