import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.tools.screenshot.PreviewTest

@Preview
@PreviewTest
@Composable
fun TestPreview() {
    Box(
        modifier = Modifier.size(100.dp).background(Color.Red)
    ) {
        Box(Modifier.align(Alignment.Center).size(3.dp).background(Color.Green))
    }
}
