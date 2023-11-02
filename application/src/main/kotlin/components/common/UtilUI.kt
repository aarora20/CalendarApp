package components.common

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun DividerComposable(text: String) {
    Row(modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Divider(modifier = Modifier.fillMaxWidth()
            .weight(1f),
            color = Color.LightGray,
            thickness = 1.dp)
        Text(text = text, fontSize = 14.sp, modifier = Modifier.padding(8.dp))
        Divider(modifier = Modifier.fillMaxWidth().weight(1f),
            color = Color.LightGray,
            thickness = 1.dp)
    }
}