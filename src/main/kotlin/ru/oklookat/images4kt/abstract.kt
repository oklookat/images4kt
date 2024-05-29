package ru.oklookat.images4kt

enum class ImageType {
    /** Represents an image with 8-bit RGBA color components packed into integer pixels. */
    INT_ARGB
}

interface ImageFactory {
    fun make(width: Int, height: Int, type: ImageType): Image
}

interface Image {
    /** Returns an integer pixel in the default RGB color model (TYPE_INT_ARGB) and default sRGB colorspace. */
    fun getRGB(x: Int, y: Int): Int
    /** Sets a pixel in this Image to the specified RGB value. */
    fun setRGB(x: Int, y: Int, rgb: Int)
    /** Width of the Image. */
    val width: Int
    /** Height of the Image. */
    val height: Int
}

/** A point representing a location in (x,y) coordinate space, specified in integer precision. */
data class Point(val x: Int, val y: Int)
