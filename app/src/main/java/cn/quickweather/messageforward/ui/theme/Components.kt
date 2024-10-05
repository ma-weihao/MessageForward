package cn.quickweather.messageforward.ui.theme

import androidx.appcompat.widget.DialogTitle
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
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
    topCornerSize: Dp = 24.dp,
    bottomCornerSize: Dp = 24.dp,
    outerPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    innerPadding: PaddingValues = PaddingValues(12.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(outerPadding)
            .wrapContentHeight(),
        shape = RoundedCornerShape(
            topStart = topCornerSize,
            topEnd = topCornerSize,
            bottomStart = bottomCornerSize,
            bottomEnd = bottomCornerSize
        ),
        color = backgroundColor,
        contentColor = contentColor,
    ) {
        Column(modifier.padding(innerPadding)) {
            content()
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
                modifier = Modifier
                    .padding(end = 12.dp)
                    .size(22.dp),
                tint = MaterialTheme.colorScheme.error,
                contentDescription = null,
            )
            Text(text = message, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
internal fun Dialog(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
    actions: @Composable () -> Unit,
    dismissDialog: () -> Unit,
) {
    androidx.compose.ui.window.Dialog(
        onDismissRequest = dismissDialog,
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(16.dp)
            )

            content()

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.CenterEnd,
            ) {
                actions()
            }
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