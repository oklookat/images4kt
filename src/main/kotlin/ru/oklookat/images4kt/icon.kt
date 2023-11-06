package ru.oklookat.images4kt

import java.awt.Point
import java.awt.image.BufferedImage

/** Generates a normalized image signature ("icon").
Generated icons can then be stored in a database and used
for comparison. Icon is the recommended function,
vs less robust func IconNN. */
fun icon(img: BufferedImage): Icon {
    val icon = iconNN(img)

    // Maximizing icon contrast. This to reflect on the human visual
    // experience, when high contrast (normalized) images are easier
    // to see. Normalization also compensates for considerable loss
    // of visual information during scars resampling during
    // icon creation step.
    icon.normalize()

    return icon
}

/** Generates a NON-normalized image signature (icon).
Icons made with iconNN can be used instead of icons made with
func icon, but mostly for experimental purposes, allowing
better understand how the algorithm works, or performing
less aggressive customized normalization. Not for general use. */
fun iconNN(img: BufferedImage): Icon {
    val (resImg, imgSize) = resizeByNearest(img, Point(RESIZED_IMG_SIZE, RESIZED_IMG_SIZE))
    val largeIcon = sizedIcon(LARGE_ICON_SIZE)

    for (x in 0..<LARGE_ICON_SIZE) {
        for (y in 0..<LARGE_ICON_SIZE) {
            var sumR = 0u
            var sumG = 0u
            var sumB = 0u
            for (m in 0..<SAMPLES) {
                for (n in 0..<SAMPLES) {
                    val rgb = resImg.getRGB(x * SAMPLES + m, y * SAMPLES + n)
                    sumR += (rgb ushr 16).toUInt()
                    sumG += (rgb ushr 8 and 0xFF).toUInt()
                    sumB += (rgb and 0xFF).toUInt()
                }
            }
            largeIcon.set(
                LARGE_ICON_SIZE, Point(x, y),
                sumR.toDouble() * INV_SAMPLE_PIXELS_2,
                sumG.toDouble() * INV_SAMPLE_PIXELS_2,
                sumB.toDouble() * INV_SAMPLE_PIXELS_2
            )
        }
    }

    val icon = sizedIcon(ICON_SIZE)

    for (x in 1..<LARGE_ICON_SIZE - 1 step 2) {
        val xd = x / 2
        for (y in 1..<LARGE_ICON_SIZE - 1 step 2) {
            val yd = y / 2
            var s1 = 0.0
            var s2 = 0.0
            var s3 = 0.0
            for (n in -1..1) {
                for (m in -1..1) {
                    val (c1, c2, c3) = largeIcon.get(LARGE_ICON_SIZE, Point(x + n, y + m))
                    s1 += c1
                    s2 += c2
                    s3 += c3
                }
            }
            val (yc, cb, cr) = yCbCr(s1 * ONE_NINTH, s2 * ONE_NINTH, s3 * ONE_NINTH)
            icon.set(ICON_SIZE, Point(xd, yd), yc, cb, cr)
        }
    }

    icon.imgSize = imgSize
    return icon
}

fun emptyIcon(): Icon {
    return Icon(mutableListOf(), Point(0, 0))
}


internal fun sizedIcon(size: Int): Icon {
    return Icon(
        MutableList(
            size * size * 3
        ) { 0u }, Point(0, 0)
    )
}

/** Icon has square shape. Its pixels are UShort values
in 3 channels. UShort is intentional to preserve color
relationships from the full-size image. It is a 255-
pre multiplied color value in [0, 255] range. **/
class Icon(var pixels: MutableList<UShort>, var imgSize: Point) {


    /** Rotates an icon by 90 degrees clockwise. */
    fun rotate90(): Icon {
        val rotated = sizedIcon(ICON_SIZE)

        for (x in 0..<ICON_SIZE) {
            for (y in 0..<ICON_SIZE) {
                val (c1, c2, c3) = get(ICON_SIZE, Point(y, ICON_SIZE - 1 - x))
                rotated.set(ICON_SIZE, Point(x, y), c1, c2, c3)
            }
        }

        // Swap image sizes.
        rotated.imgSize.x = this.imgSize.y
        rotated.imgSize.y = this.imgSize.x
        return rotated
    }

    /** Set places pixel values in an icon at a point.
    c1, c2, c3 are color values for each channel
    (RGB for example). Size is icon size. */
    fun set(size: Int, p: Point, c1: Double, c2: Double, c3: Double) {
        // Multiplication by 255 is basically encoding Double as UShort.
        this.pixels[arrIndex(p, size, 0)] = (c1 * 255).toInt().toUShort()
        this.pixels[arrIndex(p, size, 1)] = (c2 * 255).toInt().toUShort()
        this.pixels[arrIndex(p, size, 2)] = (c3 * 255).toInt().toUShort()
    }

    /** Reads pixel values in an icon at a point.
    c1, c2, c3 are color values for each channel
    (RGB for example). */
    fun get(size: Int, p: Point): Triple<Double, Double, Double> {
        // Division by 255 is basically decoding UShort into Double.
        val c1 = (this.pixels[arrIndex(p, size, 0)]).toDouble() * ONE_255TH
        val c2 = (this.pixels[arrIndex(p, size, 1)]).toDouble() * ONE_255TH
        val c3 = (this.pixels[arrIndex(p, size, 2)]).toDouble() * ONE_255TH
        return Triple(c1, c2, c3)
    }

    /** Stretches histograms for the 3 channels of an icon, so that
    min/max values of each channel are 0/255 correspondingly.

    Note: values of Icon are pre multiplied by 255, thus having maximum
    value of SQ_255 constant corresponding to display color value of 255. */
    internal fun normalize() {
        var c1Min: UShort = UShort.MAX_VALUE
        var c2Min: UShort = UShort.MAX_VALUE
        var c3Min: UShort = UShort.MAX_VALUE
        var c1Max: UShort = 0u
        var c2Max: UShort = 0u
        var c3Max: UShort = 0u


        // Looking for extreme values.
        for (n in 0..<NUM_PIX) {
            // Channel 1.
            if (this.pixels[n] > c1Max) {
                c1Max = this.pixels[n]
            }
            if (this.pixels[n] < c1Min) {
                c1Min = this.pixels[n]
            }
            // Channel 2.
            if (this.pixels[n + NUM_PIX] > c2Max) {
                c2Max = this.pixels[n + NUM_PIX]
            }
            if (this.pixels[n + NUM_PIX] < c2Min) {
                c2Min = this.pixels[n + NUM_PIX]
            }
            // Channel 3.
            if (this.pixels[n + 2 * NUM_PIX] > c3Max) {
                c3Max = this.pixels[n + 2 * NUM_PIX]
            }
            if (this.pixels[n + 2 * NUM_PIX] < c3Min) {
                c3Min = this.pixels[n + 2 * NUM_PIX]
            }
        }

        // Normalization.
        var scale: Double
        if (c1Max != c1Min) { // Must not divide by zero.
            scale = SQ_255 / (c1Max.toDouble() - c1Min.toDouble())
            for (n in 0 ..< NUM_PIX) {
                val out = (this.pixels[n].toDouble() - c1Min.toDouble()) * scale
                this.pixels[n] = out.toInt().toUShort()
            }
        }
        if (c2Max != c2Min) { // Must not divide by zero.
            scale = SQ_255 / (c2Max.toDouble() - c2Min.toDouble())
            for (n in 0 ..< NUM_PIX)  {
                val out = (this.pixels[n + NUM_PIX].toDouble() - c2Min.toDouble()) * scale
                this.pixels[n + NUM_PIX] = out.toInt().toUShort()
            }
        }
        if (c3Max != c3Min) { // Must not divide by zero.
            scale = SQ_255 / (c3Max.toDouble() - c3Min.toDouble())
            for (n in 0 ..< NUM_PIX)  {
                val out = (this.pixels[n + 2 * NUM_PIX].toDouble() - c3Min.toDouble()) * scale
                this.pixels[n + 2 * NUM_PIX] = out.toInt().toUShort()
            }
        }
    }
}

/** Pixel position in 1D array from a point
of 2D array.

ch: color channel index (0 to 2). */
internal fun arrIndex(p: Point, size: Int, ch: Int): Int {
    return size * (ch * size + p.y) + p.x
}

/** Transforms RGB components to YCbCr. */
internal fun yCbCr(r: Double, g: Double, b: Double): Triple<Double, Double, Double> {
    val yc = 0.299000 * r + 0.587000 * g + 0.114000 * b
    val cb = 128 - 0.168736 * r - 0.331264 * g + 0.500000 * b
    val cr = 128 + 0.500000 * r - 0.418688 * g - 0.081312 * b
    return Triple(yc, cb, cr)
}
