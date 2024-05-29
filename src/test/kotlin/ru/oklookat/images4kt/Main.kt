package ru.oklookat.images4kt

import java.awt.image.BufferedImage

internal const val TEST_RESOURCES_DIR = "src/test/resources"

class Img(val buff: BufferedImage) : Image {
    override fun getRGB(x: Int, y: Int): Int {
        return buff.getRGB(x, y)
    }

    override fun setRGB(x: Int, y: Int, rgb: Int) {
        buff.setRGB(x, y, rgb)
    }

    override val width: Int = buff.width
    override val height: Int = buff.height
}

class ImgFact : ImageFactory {
    override fun make(width: Int, height: Int, iType: ImageType): Image {
        var type = 0
        when (iType) {
            ImageType.INT_ARGB -> type = BufferedImage.TYPE_INT_ARGB
        }
        val img = BufferedImage(width, height, type)
        return Img(img)
    }

}
