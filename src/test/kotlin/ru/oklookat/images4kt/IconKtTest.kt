package ru.oklookat.images4kt

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.awt.Point
import javax.imageio.ImageIO
import kotlin.io.path.Path
import kotlin.math.abs

class IconKtTest {

    @Test
    fun sizedIcon() {
        val icon = sizedIcon(4)
        val expected = 4 * 4 * 3
        val got = icon.pixels.size
        assertEquals(expected, got)
    }

    @Test
    fun arrIndex() {
        var x = 2
        var y = 3
        val size = 4
        var ch = 2
        var got = Point(x, y).arrIndex(size, ch)
        var expected = 46
        assertEquals(got, expected)

        x = 1
        y = 1
        ch = 1
        got = Point(x, y).arrIndex(size, ch)
        expected = 21
        assertEquals(got, expected)

        x = 3
        y = 3
        ch = 0
        got = Point(x, y).arrIndex(size, ch)
        expected = 15
        assertEquals(got, expected)
    }

    @Test
    fun set() {
        val icon = sizedIcon(4)
        icon.set(4, Point(1, 1), 13.5, 29.9, 95.9)
        val expected = sizedIcon(4)
        val expectedPixels = listOf(
            0.0, 0.0, 0.0, 0.0, 0.0, 13.5, 0.0, 0.0, 0.0, 0.0, 0.0,
            0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 29.9, 0.0, 0.0, 0.0, 0.0,
            0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 95.9, 0.0, 0.0, 0.0,
            0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0
        )
        for (i in expectedPixels.indices) {
            expected.pixels[i] = (expectedPixels[i] * 255).toInt().toUShort()
        }
        assertEquals(expected.pixels, icon.pixels)
        assertEquals(expected.imgSize, icon.imgSize)
    }

    @Test
    fun get() {
        val icon = sizedIcon(4)
        val iconPix = listOf(
            0.0, 0.0, 0.0, 0.0, 0.0, 13.5, 0.0, 0.0, 0.0, 0.0, 0.0,
            0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 29.9, 0.0, 0.0, 0.0, 0.0,
            0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 95.9, 0.0, 0.0, 0.0,
            0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0
        )
        for (i in iconPix.indices) {
            icon.pixels[i] = (iconPix[i] * 255).toInt().toUShort()
        }
        val (c1, c2, c3) = icon.get(4, Point(1, 1))
        assertTrue(abs(c1 - 13.5) < 0.1)
        assertTrue(abs(c2 - 29.9) < 0.1)
        assertTrue(abs(c3 - 95.9) < 0.1)
    }

    /** Only checking that image size is correct. */
    @Test
    fun icon() {
        val testDir = "resample"
        val imageName = "nearest533x400.png"
        val imgFile = Path(TEST_RESOURCES_DIR, testDir, imageName).toFile()
        val img = ImageIO.read(imgFile)
        val icon = icon(img)
        assertEquals(icon.imgSize.x, 533)
        assertEquals(icon.imgSize.y, 400)
    }

    @Test
    fun yCbCr() {
        var r = 255.0
        var g = 255.0
        var b = 255.0
        var eY = 255.0
        var eCb = 128.0
        var eCr = 128.0
        val (y, cb, cr) = yCbCr(r, g, b)
        // Int values, so the test does not become brittle.
        assertTrue(abs(y - eY) < 0.1)
        assertTrue(abs(cb - eCb) < 0.1)
        assertTrue(abs(cr - eCr) < 0.1)

        r = 14.0
        g = 22.0
        b = 250.0
        // From the original external formula.
        eY = 45.6
        eCb = 243.3
        eCr = 105.5
        val (y2, cb2, cr2) = yCbCr(r, g, b)
        // Int values, so the test does not become brittle.
        assertTrue(y2.toInt() == eY.toInt())
        assertTrue(cb2.toInt() == eCb.toInt())
        assertTrue(cr2.toInt() == eCr.toInt())
    }

    private fun testNormalize(src: Icon, want: Icon) {
        src.normalize()
        src.pixels.forEachIndexed { i, _ ->
            if (abs(src.pixels[i].toDouble() - want.pixels[i].toDouble()) / want.pixels[i].toDouble() > 1) {
                throw Exception("Want $want, got $src.")
            }
        }
    }

    @Test
    fun normalize() {
        val src = emptyIcon()
        src.pixels = getNormalizeSourcePixels()

        val want = emptyIcon()
        want.pixels = getNormalizeWantPixels()

        assertDoesNotThrow {
            testNormalize(src, want)
        }
    }

    @Test
    fun rotate() {
        val testDir = "rotate"

        val imgFile0 = Path(TEST_RESOURCES_DIR, testDir, "0.jpg").toFile()
        val img0 = ImageIO.read(imgFile0)
        val icon0 = icon(img0)

        val imgFile90 = Path(TEST_RESOURCES_DIR, testDir, "90.jpg").toFile()
        val img90 = ImageIO.read(imgFile90)
        val icon90 = icon(img90)

        assertTrue(similar(icon0.rotate90(), icon90))
    }
}
