package components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.icons.TablerIcons
import compose.icons.tablericons.Trash


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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomIconButton(
    onClick: () -> Unit,
    modifier: Modifier,
    tooltipText: String,
    buttonRadius: Dp,
    buttonSize: Dp,
    backgroundColor: Color,
    icon: ImageVector
) {
    Box (
        modifier = modifier
    ) {
        PlainTooltipBox(
            tooltip = {Text(tooltipText, color = Color.White)}
        ) {
            CompositionLocalProvider(
                LocalMinimumInteractiveComponentEnforcement provides false
            ) {
                IconButton(
                    onClick = onClick,
                    modifier = Modifier
                        .then(Modifier.size(buttonRadius))
                        .statusBarsPadding()
                        .background(
                            color = backgroundColor,
                            shape = CircleShape
                        ).tooltipAnchor(),

                    ) {
                    Icon(
                        imageVector = (icon),
                        contentDescription = tooltipText,
                        modifier = Modifier.size(buttonSize)
                    )
                }
            }
        }
    }
}