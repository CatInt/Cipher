import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

class AppTheme {

    class Colour {

        companion object {
            val Background = Color.hsl(0f, 0f, .88f)
            val Gray = Color.hsl(0f, .02f, .86f)
            val Green = Color.hsl(149f, .97f, .44f)
            val Yellowish = Color.hsl(55f, .79f, .83f)
            val Yellow = Color.hsl(55f, .97f, .58f)
            val Orange = Color.hsl(43f, .88f, .53f)
            val Blue = Color.hsl(207f, .69f, .65f)
        }
    }
    
    class Shape {

        companion object {
            val Rounded = RoundedCornerShape(8.dp)
        }
    }
}