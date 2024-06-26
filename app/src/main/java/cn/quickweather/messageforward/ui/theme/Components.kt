package cn.quickweather.messageforward.ui.theme

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cn.quickweather.messageforward.R

/**
 * Created by maweihao on 5/25/24
 */

@Composable
internal fun ContentCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .wrapContentHeight(),
        shape = RoundedCornerShape(24.dp),
        color = backgroundColor,
    ) {
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            Column(modifier.padding(12.dp)) {
                content()
            }
        }
    }
}

@Composable
internal fun ErrorCard(
    message: String,
    modifier: Modifier = Modifier,
) {
    ContentCard(
        modifier = modifier,
        backgroundColor = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.error,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_lightbulb),
                modifier = Modifier.padding(end = 12.dp).size(22.dp),
                tint = MaterialTheme.colorScheme.error,
                contentDescription = null,
            )
            Text(text = message, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun ErrorCardPreview() {
    MessageForwardTheme {
        ErrorCard("error")
    }
}