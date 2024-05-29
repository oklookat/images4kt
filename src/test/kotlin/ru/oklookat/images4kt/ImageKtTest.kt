package ru.oklookat.images4kt

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import javax.imageio.ImageIO
import kotlin.io.path.Path
import kotlin.test.assertTrue


private data class ResizeByNearestTestCase(
    val inFile: String, val srcX: Int,
    val srcY: Int,
    val outFile: String,
    val dstX: Int, val dstY: Int
)

class ImageKtTest {

    @Test
    fun resizeByNearest() {
        val testDir = Path(TEST_RESOURCES_DIR, "resample")
        val tables = listOf(
            ResizeByNearestTestCase(
                "original.png", 533, 400,
                "nearest100x100.png", 100, 100
            ),
            ResizeByNearestTestCase(
                "nearest100x100.png", 100, 100,
                "nearest533x400.png", 533, 400
            ),
        )
        for (table in tables) {
            val inImgFile = Path(testDir.toString(), table.inFile).toFile()
            val inImg = Img(ImageIO.read(inImgFile))
            val outImgFile = Path(testDir.toString(), table.outFile).toFile()
            val outImg = Img(ImageIO.read(outImgFile))

            val (resampled, srcSize) = resizeByNearest(ImgFact(), inImg, Point(table.dstX, table.dstY))
            assertTrue(imagesEqual(resampled, outImg))
            assertEquals(table.srcX, srcSize.x)
            assertEquals(table.srcY, srcSize.y)
        }
    }

    private fun imagesEqual(img1: Image, img2: Image): Boolean {
        if (img1.width == img2.width && img1.height == img2.height) {
            for (x in 0..<img1.width) {
                for (y in 0..<img1.height) {
                    if (img1.getRGB(x, y) != img2.getRGB(x, y)) return false
                }
            }
        } else {
            return false
        }
        return true
    }
}
