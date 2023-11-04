package ru.oklookat.images4kt

import java.awt.Point
import java.awt.image.BufferedImage

/** Icon has square shape. Its pixels are uint16 values
in 3 channels. uint16 is intentional to preserve color
relationships from the full-size image. It is a 255-
pre multiplied color value in [0, 255] range. **/
data class IconT(var pixels: MutableList<UShort>, var imgSize: Point)

/** Icon generates a normalized image signature ("icon").
Generated icons can then be stored in a database and used
for comparison. Icon is the recommended function,
vs less robust func IconNN. */
fun icon(img: BufferedImage): IconT {
    val icon = iconNN(img)

    // Maximizing icon contrast. This to reflect on the human visual
    // experience, when high contrast (normalized) images are easier
    // to see. Normalization also compensates for considerable loss
    // of visual information during scars resampling during
    // icon creation step.
    icon.normalize()

    return icon
}

/** IconNN generates a NON-normalized image signature (icon).
Icons made with IconNN can be used instead of icons made with
func Icon, but mostly for experimental purposes, allowing
better understand how the algorithm works, or performing
less aggressive customized normalization. Not for general use. */
private fun iconNN(img: BufferedImage): IconT {
    val (resImg, imgSize) = resizeByNearest(img, Point(resizedImgSize, resizedImgSize))
    val largeIcon = sizedIcon(largeIconSize)
    var r: UInt
    var g: UInt
    var b: UInt
    var sumR: UInt
    var sumG: UInt
    var sumB: UInt

    for (x in 0..<largeIconSize) {
        for (y in 0..<largeIconSize) {
            sumR = 0u
            sumG = 0u
            sumB = 0u
            for (m in 0..<samples) {
                for (n in 0..<samples) {
                    val rgb = resImg.getRGB(x * samples + m, y * samples + n)
                    r = (rgb ushr 16).toUInt()
                    g = (rgb ushr 8 and 0xFF).toUInt()
                    b = (rgb and 0xFF).toUInt()
                    sumR += r
                    sumG += g
                    sumB += b
                }
            }
            set(largeIcon, largeIconSize, Point(x, y),
                sumR.toDouble() * invSamplePixels2,
                sumG.toDouble() * invSamplePixels2,
                sumB.toDouble() * invSamplePixels2
            )
        }
    }

    val icon = sizedIcon(IconSize)
    var xd: Int
    var yd: Int
    var c1: Double
    var c2: Double
    var c3: Double
    var s1: Double
    var s2: Double
    var s3: Double

    for (x in 1..<largeIconSize - 1 step 2) {
        xd = x / 2
        for (y in 1..<largeIconSize - 1 step 2) {
            yd = y / 2
            s1 = 0.0
            s2 = 0.0
            s3 = 0.0
            for (n in -1..1) {
                for (m in -1..1) {
                    val (c1Value, c2Value, c3Value) = get(largeIcon, largeIconSize, Point(x + n, y + m))
                    c1 = c1Value
                    c2 = c2Value
                    c3 = c3Value
                    s1 += c1
                    s2 += c2
                    s3 += c3
                }
            }
            val (yc, cb, cr) = yCbCr(s1 * oneNinth, s2 * oneNinth, s3 * oneNinth)
            set(icon, IconSize, Point(xd, yd), yc, cb, cr)
        }
    }

    icon.imgSize = imgSize
    return icon
}

internal fun sizedIcon(size: Int): IconT {
    return IconT(
        MutableList(
            size * size * 3
        ) { 0u }, Point(0, 0)
    )
}

fun emptyIcon(): IconT {
    return IconT(mutableListOf(), Point(0, 0))
}

/* Set places pixel values in an icon at a point.
c1, c2, c3 are color values for each channel
(RGB for example). Size is icon size.
Public to be used in package imagehash. */
fun set(icon: IconT, size: Int, p: Point, c1: Double, c2: Double, c3: Double) {
    // Multiplication by 255 is basically encoding float64 as uint16.
    icon.pixels[arrIndex(p, size, 0)] = (c1 * 255).toInt().toUShort()
    icon.pixels[arrIndex(p, size, 1)] = (c2 * 255).toInt().toUShort()
    icon.pixels[arrIndex(p, size, 2)] = (c3 * 255).toInt().toUShort()
}

/** Get reads pixel values in an icon at a point.
c1, c2, c3 are color values for each channel
(RGB for example).
Exported to be used in package imagehash.*/
internal fun get(icon: IconT, size: Int, p: Point): Triple<Double, Double, Double> {
    // Division by 255 is basically decoding uint16 into float64.
    val c1 = (icon.pixels[arrIndex(p, size, 0)]).toDouble() * one255th
    val c2 = (icon.pixels[arrIndex(p, size, 1)]).toDouble() * one255th
    val c3 = (icon.pixels[arrIndex(p, size, 2)]).toDouble() * one255th
    return Triple(c1, c2, c3)
}

/** ArrIndex gets a pixel position in 1D array from a point
of 2D array. ch is color channel index (0 to 2). */
internal fun arrIndex(p: Point, size: Int, ch: Int): Int {
    return size * (ch * size + p.y) + p.x
}

/** yCbCr transforms RGB components to YCbCr. This is a high
precision version different from the Golang image library
operating on uint8.*/
internal fun yCbCr(r: Double, g: Double, b: Double): Triple<Double, Double, Double> {
    val yc = 0.299000 * r + 0.587000 * g + 0.114000 * b
    val cb = 128 - 0.168736 * r - 0.331264 * g + 0.500000 * b
    val cr = 128 + 0.500000 * r - 0.418688 * g - 0.081312 * b
    return Triple(yc, cb, cr)
}

/** Normalize stretches histograms for the 3 channels of an icon, so that
min/max values of each channel are 0/255 correspondingly.
Note: values of IconT are pre multiplied by 255, thus having maximum
value of sq255 constant corresponding to display color value of 255. */
internal fun IconT.normalize() {
    var c1Min: UShort = UShort.MAX_VALUE
    var c2Min: UShort = UShort.MAX_VALUE
    var c3Min: UShort = UShort.MAX_VALUE
    var c1Max: UShort = 0u
    var c2Max: UShort = 0u
    var c3Max: UShort = 0u
    var scale: Double
    var n = 0

    // Looking for extreme values.
    while (n < numPix) {
        // Channel 1.
        if (this.pixels[n] > c1Max) {
            c1Max = this.pixels[n]
        }
        if (this.pixels[n] < c1Min) {
            c1Min = this.pixels[n]
        }
        // Channel 2.
        if (this.pixels[n + numPix] > c2Max) {
            c2Max = this.pixels[n + numPix]
        }
        if (this.pixels[n + numPix] < c2Min) {
            c2Min = this.pixels[n + numPix]
        }
        // Channel 3.
        if (this.pixels[n + 2 * numPix] > c3Max) {
            c3Max = this.pixels[n + 2 * numPix]
        }
        if (this.pixels[n + 2 * numPix] < c3Min) {
            c3Min = this.pixels[n + 2 * numPix]
        }
        n++
    }

    // Normalization.
    if (c1Max != c1Min) { // Must not divide by zero.
        scale = sq255 / (c1Max.toDouble() - c1Min.toDouble())
        n = 0
        while (n < numPix) {
            val out = (this.pixels[n].toDouble() - c1Min.toDouble()) * scale
            this.pixels[n] = out.toInt().toUShort()
            n++
        }
    }
    if (c2Max != c2Min) { // Must not divide by zero.
        scale = sq255 / (c2Max.toDouble() - c2Min.toDouble())
        n = 0
        while (n < numPix) {
            val out = (this.pixels[n + numPix].toDouble() - c2Min.toDouble()) * scale
            this.pixels[n + numPix] = out.toInt().toUShort()
            n++
        }
    }
    if (c3Max != c3Min) { // Must not divide by zero.
        scale = sq255 / (c3Max.toDouble() - c3Min.toDouble())
        n = 0
        while (n < numPix) {
            val out = (this.pixels[n + 2 * numPix].toDouble() - c3Min.toDouble()) * scale
            this.pixels[n + 2 * numPix] = out.toInt().toUShort()
            n++
        }
    }
}

/** Rotate rotates an icon by 90 degrees clockwise. */
internal fun rotate90(icon: IconT): IconT {
    val rotated = sizedIcon(IconSize)

    for (x in 0..<IconSize) {
        for (y in 0..<IconSize) {
            val (c1, c2, c3) = get(icon, IconSize, Point(y, IconSize - 1 - x))
            set(rotated, IconSize, Point(x, y), c1, c2, c3)
        }
    }

    // Swap image sizes.
    rotated.imgSize.x = icon.imgSize.y
    rotated.imgSize.y = icon.imgSize.x
    return rotated
}