package com.unicofrance.uniexo.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unicofrance.uniexo.R
import com.unicofrance.uniexo.data.local.database.entities.Container
import com.unicofrance.uniexo.ui.lib.SvgIcon
import com.unicofrance.uniexo.utils.parseHexColor

@Composable
fun MarkerInfo(
    container: Container,
    onNavigationToDetail: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    Box(
        modifier = modifier
            .padding(horizontal = 15.dp)
            .height(100.dp)
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(8.dp))
            .background(
                Color.White,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable {
                onNavigationToDetail(container.id)
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.background(
                    parseHexColor(container.streamColor),
                    MaterialTheme.shapes.extraLarge
                )
            ) {
                SvgIcon(
                    url = container.iconUrl ?: "",
                    modifier = Modifier.size(80.dp)
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top),
            ) {
                Text(
                    text = container.label,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = container.description,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Light,
                    color = Color.Black,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis
                )
            }
            VerticalDivider(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(vertical = 8.dp),
                color = Color.Gray,
                thickness = 1.dp
            )
            Icon(
                painter = painterResource(id = R.drawable.arrow_forward),
                contentDescription = "Forward",
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(24.dp)
            )
        }
    }
}