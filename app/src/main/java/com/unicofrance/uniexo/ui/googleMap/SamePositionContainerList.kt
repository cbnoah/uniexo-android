package com.unicofrance.uniexo.ui.googleMap

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unicofrance.uniexo.data.local.database.entities.Container
import com.unicofrance.uniexo.ui.detail.MarkerInfo
import kotlin.collections.forEach

@Composable
fun SamePositionContainerList(
    showMarkerInfo: MutableState<Boolean>,
    containerAtSamePosition: List<Container>,
    onNavigationToDetail: (String) -> Unit
) {
    AnimatedVisibility(
        visible = showMarkerInfo.value,
        enter = slideInVertically { it },
        exit = slideOutVertically { it }) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = null,
                    indication = null
                ) {
                    showMarkerInfo.value = false
                }
        ) {
            if (containerAtSamePosition.isEmpty()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 15.dp)
                        .padding(bottom = 25.dp)
                        .height(100.dp)
                        .fillMaxWidth()
                        .shadow(elevation = 4.dp, shape = RoundedCornerShape(8.dp))
                        .background(
                            Color.White,
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Information du container introuvable",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 25.dp),
                    verticalArrangement = Arrangement.spacedBy(
                        8.dp,
                        Alignment.CenterVertically
                    ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    containerAtSamePosition.let { containers ->
                        containers.forEach { container ->
                            MarkerInfo(container, onNavigationToDetail, modifier = Modifier)
                        }
                    }
                }
            }
        }
    }
}