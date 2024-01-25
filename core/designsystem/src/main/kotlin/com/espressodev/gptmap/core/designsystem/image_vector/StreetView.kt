import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

private var Street_ViewMut: ImageVector? = null

val StreetView: ImageVector
    get() {
        if (Street_ViewMut != null) {
            return Street_ViewMut!!
        }
        Street_ViewMut = ImageVector.Builder(
            name = "StreetView",
            defaultWidth = 18.dp,
            defaultHeight = 24.dp,
            viewportWidth = 18f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF000000)),
                fillAlpha = 1.0f,
                stroke = null,
                strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(9f, 0f)
                curveTo(7.7593f, 0f, 6.75f, 1.0093f, 6.75f, 2.25f)
                curveTo(6.75f, 3.4907f, 7.7593f, 4.5f, 9f, 4.5f)
                curveTo(10.2407f, 4.5f, 11.25f, 3.4907f, 11.25f, 2.25f)
                curveTo(11.25f, 1.0093f, 10.2407f, 0f, 9f, 0f)
                close()
                moveTo(9f, 0.75f)
                curveTo(9.8276f, 0.75f, 10.5f, 1.4224f, 10.5f, 2.25f)
                curveTo(10.5f, 3.0776f, 9.8276f, 3.75f, 9f, 3.75f)
                curveTo(8.1724f, 3.75f, 7.5f, 3.0776f, 7.5f, 2.25f)
                curveTo(7.5f, 1.4224f, 8.1724f, 0.75f, 9f, 0.75f)
                close()
                moveTo(9f, 4.71533f)
                curveTo(8.4697f, 4.7153f, 7.9394f, 4.7871f, 7.4238f, 4.9351f)
                curveTo(6.5947f, 5.1724f, 5.8726f, 5.6514f, 5.335f, 6.3237f)
                curveTo(4.7959f, 6.9976f, 4.5f, 7.8428f, 4.5f, 8.7056f)
                verticalLineTo(12.1875f)
                curveTo(4.5f, 12.9111f, 5.0889f, 13.5f, 5.8125f, 13.5f)
                curveTo(5.9941f, 13.5f, 6.167f, 13.4634f, 6.3252f, 13.396f)
                lineTo(6.15088f, 17.0522f)
                curveTo(2.4053f, 17.5269f, 0f, 18.8394f, 0f, 20.4375f)
                curveTo(0f, 22.4678f, 3.8687f, 24f, 9f, 24f)
                curveTo(14.1313f, 24f, 18f, 22.4678f, 18f, 20.4375f)
                curveTo(18f, 18.8438f, 15.5918f, 17.5269f, 11.8491f, 17.0522f)
                lineTo(11.6733f, 13.3945f)
                curveTo(11.8315f, 13.4634f, 12.0059f, 13.5f, 12.1875f, 13.5f)
                curveTo(12.9111f, 13.5f, 13.5f, 12.9111f, 13.5f, 12.1875f)
                verticalLineTo(8.70557f)
                curveTo(13.5f, 7.8428f, 13.2026f, 6.9976f, 12.665f, 6.3252f)
                curveTo(12.1274f, 5.6528f, 11.4053f, 5.1724f, 10.5762f, 4.9351f)
                curveTo(10.0605f, 4.7871f, 9.5303f, 4.7153f, 9f, 4.7153f)
                close()
                moveTo(9f, 5.46533f)
                curveTo(9.4614f, 5.4653f, 9.9229f, 5.5283f, 10.3711f, 5.6558f)
                curveTo(11.0493f, 5.8506f, 11.6396f, 6.2432f, 12.0806f, 6.7925f)
                curveTo(12.5112f, 7.3345f, 12.75f, 8.0127f, 12.75f, 8.7056f)
                verticalLineTo(12.1875f)
                curveTo(12.75f, 12.498f, 12.498f, 12.75f, 12.1875f, 12.75f)
                curveTo(11.877f, 12.75f, 11.625f, 12.498f, 11.625f, 12.1875f)
                verticalLineTo(8.10938f)
                curveTo(11.625f, 7.9028f, 11.4565f, 7.7344f, 11.25f, 7.7344f)
                curveTo(11.0435f, 7.7344f, 10.875f, 7.9028f, 10.875f, 8.1094f)
                verticalLineTo(12.1875f)
                curveTo(10.875f, 12.189f, 10.875f, 12.1904f, 10.875f, 12.1919f)
                verticalLineTo(12.3926f)
                lineTo(11.1973f, 19.0898f)
                curveTo(11.2061f, 19.2979f, 11.1328f, 19.4941f, 10.9907f, 19.6436f)
                curveTo(10.8472f, 19.793f, 10.6538f, 19.875f, 10.4458f, 19.875f)
                curveTo(10.0459f, 19.875f, 9.7163f, 19.563f, 9.6958f, 19.1616f)
                lineTo(9.375f, 13.106f)
                curveTo(9.3633f, 12.9067f, 9.1992f, 12.75f, 9f, 12.75f)
                curveTo(8.8008f, 12.75f, 8.6367f, 12.9067f, 8.625f, 13.106f)
                lineTo(8.3042f, 19.1616f)
                curveTo(8.2837f, 19.5615f, 7.9556f, 19.875f, 7.5527f, 19.875f)
                curveTo(7.3462f, 19.875f, 7.1528f, 19.793f, 7.0093f, 19.6436f)
                curveTo(6.8672f, 19.4941f, 6.7939f, 19.2979f, 6.8027f, 19.0913f)
                lineTo(6.87891f, 17.4946f)
                curveTo(6.895f, 17.4463f, 6.9009f, 17.395f, 6.8965f, 17.3423f)
                curveTo(6.895f, 17.3335f, 6.8906f, 17.3276f, 6.8892f, 17.3203f)
                lineTo(7.125f, 12.375f)
                verticalLineTo(8.12549f)
                curveTo(7.125f, 7.9189f, 6.9565f, 7.7505f, 6.75f, 7.7505f)
                curveTo(6.5435f, 7.7505f, 6.375f, 7.9189f, 6.375f, 8.1255f)
                verticalLineTo(12.1875f)
                curveTo(6.375f, 12.498f, 6.1231f, 12.75f, 5.8125f, 12.75f)
                curveTo(5.5019f, 12.75f, 5.25f, 12.498f, 5.25f, 12.1875f)
                verticalLineTo(8.70557f)
                curveTo(5.25f, 8.0127f, 5.4873f, 7.333f, 5.9194f, 6.7925f)
                curveTo(6.3604f, 6.2432f, 6.9507f, 5.8506f, 7.6289f, 5.6558f)
                curveTo(8.0771f, 5.5283f, 8.5386f, 5.4639f, 9f, 5.4653f)
                close()
                moveTo(6.11279f, 17.814f)
                lineTo(6.0542f, 19.0562f)
                curveTo(6.0352f, 19.4707f, 6.1816f, 19.8618f, 6.4688f, 20.1621f)
                curveTo(6.7544f, 20.4609f, 7.1396f, 20.625f, 7.5542f, 20.625f)
                curveTo(8.2412f, 20.625f, 8.8227f, 20.1636f, 9f, 19.5293f)
                curveTo(9.1773f, 20.1636f, 9.7588f, 20.625f, 10.4473f, 20.625f)
                curveTo(10.8604f, 20.625f, 11.2456f, 20.4609f, 11.5312f, 20.1621f)
                curveTo(11.8184f, 19.8618f, 11.9648f, 19.4707f, 11.9458f, 19.0562f)
                lineTo(11.8872f, 17.814f)
                curveTo(15.4058f, 18.2827f, 17.25f, 19.4707f, 17.25f, 20.4375f)
                curveTo(17.25f, 21.7676f, 13.8618f, 23.25f, 9f, 23.25f)
                curveTo(4.1382f, 23.25f, 0.75f, 21.7676f, 0.75f, 20.4375f)
                curveTo(0.75f, 19.4663f, 2.5928f, 18.2813f, 6.1128f, 17.814f)
                close()
                moveTo(13.1411f, 21.1904f)
                curveTo(13.0928f, 21.1816f, 13.043f, 21.1831f, 12.9917f, 21.1948f)
                curveTo(12.6606f, 21.2725f, 12.2974f, 21.3398f, 11.9165f, 21.397f)
                curveTo(11.7114f, 21.4277f, 11.5693f, 21.6182f, 11.6001f, 21.8232f)
                curveTo(11.6279f, 22.0093f, 11.7876f, 22.1426f, 11.9707f, 22.1426f)
                curveTo(11.9897f, 22.1426f, 12.0073f, 22.1426f, 12.0264f, 22.1382f)
                curveTo(12.4277f, 22.0796f, 12.8101f, 22.0078f, 13.1616f, 21.9258f)
                curveTo(13.3638f, 21.8789f, 13.4883f, 21.6782f, 13.4414f, 21.4761f)
                curveTo(13.4062f, 21.3237f, 13.2847f, 21.2153f, 13.1411f, 21.1904f)
                close()
                moveTo(4.85889f, 21.1919f)
                curveTo(4.7153f, 21.2168f, 4.5938f, 21.3237f, 4.5586f, 21.4746f)
                curveTo(4.5117f, 21.6768f, 4.6362f, 21.8789f, 4.8384f, 21.9258f)
                curveTo(6.0352f, 22.2026f, 7.4751f, 22.3491f, 9f, 22.3491f)
                curveTo(9.4746f, 22.3491f, 9.9463f, 22.3359f, 10.4048f, 22.3066f)
                curveTo(10.6113f, 22.2935f, 10.7681f, 22.1162f, 10.7563f, 21.9082f)
                curveTo(10.7432f, 21.7031f, 10.5688f, 21.5479f, 10.3579f, 21.5581f)
                curveTo(9.9155f, 21.5859f, 9.4585f, 21.5991f, 9f, 21.5991f)
                curveTo(7.5513f, 21.5991f, 6.1348f, 21.4556f, 5.0083f, 21.1948f)
                curveTo(4.9585f, 21.1831f, 4.9072f, 21.1831f, 4.8589f, 21.1919f)
                close()
            }
        }.build()
        return Street_ViewMut!!
    }

