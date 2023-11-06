package ru.oklookat.images4kt

import java.awt.Color
import java.awt.Point
import java.awt.image.BufferedImage

/** Resizes an image to the destination size
with the nearest neighbour method. It also returns the source
image size. */
internal fun resizeByNearest(src: BufferedImage, dstSize: Point): Pair<BufferedImage, Point> {
    val xMax = src.width
    val yMax = src.height
    val xScale = xMax.toDouble() / dstSize.x
    val yScale = yMax.toDouble() / dstSize.y
    val dst = BufferedImage(dstSize.x, dstSize.y, BufferedImage.TYPE_INT_ARGB)

    for (y in 0..<dstSize.y) {
        for (x in 0..<dstSize.x) {
            val sampleX = (x.toDouble() * xScale).toInt().coerceIn(0, xMax - 1)
            val sampleY = (y.toDouble() * yScale).toInt().coerceIn(0, yMax - 1)
            dst.setRGB(x, y, src.getRGB(sampleX, sampleY))
        }
    }

    return Pair(dst, Point(xMax, yMax))
}
