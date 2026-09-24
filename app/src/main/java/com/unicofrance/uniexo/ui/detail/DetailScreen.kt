package com.unicofrance.uniexo.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.unicofrance.uniexo.R
import com.unicofrance.uniexo.ui.lib.SvgIcon
import com.unicofrance.uniexo.utils.parseHexColor
import java.util.Calendar
import java.util.Date

@Composable
fun DetailScreen(
    modifier: Modifier = Modifier,
    backToMap: (String) -> Unit,
    viewModel: DetailViewModel
) {
    val container by viewModel.container.collectAsStateWithLifecycle()
    val containerDate =
        Calendar.getInstance().apply { time = Date((container?.creationDatetime ?: 0) * 1000) }

    if (container == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            BackButton(
                backToMap = backToMap,
                containerId = ""
            )
            Column(
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(14.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Container not found",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize
                )
                Text(
                    text = "Unable to retrieve info for the container with the id : ${viewModel.containerId}",
                    fontWeight = FontWeight.Light,
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    if (container != null) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(top = 50.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier.background(
                            parseHexColor(container?.streamColor),
                            MaterialTheme.shapes.large
                        )
                    ) {
                        SvgIcon(
                            url = container?.iconUrl ?: "",
                            modifier = Modifier.size(80.dp)
                        )
                    }
                    Text(
                        text = container?.label ?: "",
                        fontWeight = FontWeight.Bold,
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }
                BackButton(backToMap, container?.id)
            }
            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DetailInfos(
                    label = "ID",
                    content = container?.id,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    DetailInfos(
                        label = "Latitude",
                        content = container?.latitude?.toString()?.take(7),
                        modifier = Modifier.weight(1f)
                    )
                    DetailInfos(
                        label = "Longitude",
                        content = container?.longitude?.toString()?.take(8),
                        modifier = Modifier.weight(1f)
                    )
                }
                DetailInfos(
                    label = "Label du lieu production",
                    content = container?.producingPlaceLabel,
                    modifier = Modifier.fillMaxWidth()
                )
                DetailInfos(
                    label = "Date de création",
                    content = containerDate.get(Calendar.DAY_OF_MONTH)
                        .toString() + "/" +
                            (containerDate.get(Calendar.MONTH) + 1).toString() + "/" +
                            containerDate.get(
                                Calendar.YEAR
                            ).toString(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun BackButton(
    backToMap: (String) -> Unit,
    containerId: String? = null,
) {
    IconButton(
        modifier = Modifier.border(1.dp, Color.Gray, MaterialTheme.shapes.large),
        onClick = { backToMap(containerId ?: "") }
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            painter = painterResource(R.drawable.cross),
            contentDescription = "Back to Map"
        )
    }
}

@Composable
fun DetailInfos(label: String, content: String?, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .border(1.dp, Color.Gray, MaterialTheme.shapes.large)
            .background(Color(0xFFF4F4F4), MaterialTheme.shapes.large)
    ) {

        Column(
            modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(
                4.dp,
                Alignment.CenterVertically
            )
        ) {
            Text(
                text = label,
                fontWeight = FontWeight.Light,
                fontSize = MaterialTheme.typography.bodyMedium.fontSize
            )
            Text(
                text = content ?: "",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = Color(0xFF3A3B3F)
            )
        }
    }
}