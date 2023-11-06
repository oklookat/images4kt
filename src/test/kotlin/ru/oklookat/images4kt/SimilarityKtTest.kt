package ru.oklookat.images4kt

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import javax.imageio.ImageIO
import kotlin.io.path.Path

private typealias simExecutor = (Icon, Icon) -> Boolean

class SimilarityKtTest {

    @Test
    fun propSimilar() {
        val executor: simExecutor = { i1, i2 ->
            propSimilar(i1, i2)
        }
        val subDir = "proportions"
        execSim("100x130.png", "100x124.png", subDir,true, executor)
        execSim("100x130.png", "100x122.png", subDir,false, executor)
        execSim("130x100.png", "260x200.png", subDir,true, executor)
        execSim("200x200.png", "260x200.png", subDir,false, executor)
        execSim("130x100.png", "124x100.png", subDir,true, executor)
        execSim("130x100.png", "122x100.png", subDir,false, executor)
        execSim("130x100.png", "130x100.png", subDir,true, executor)
        execSim("100x130.png", "130x100.png", subDir,false, executor)
        execSim("124x100.png", "260x200.png", subDir,true, executor)
        execSim("122x100.png", "260x200.png", subDir,false, executor)
        execSim("100x124.png", "100x130.png", subDir,true, executor)
    }

    @Test
    fun eucSimilar() {
        val executor: simExecutor = { i1, i2 ->
            eucSimilar(i1, i2)
        }
        val subDir = "euclidean"
        execSim("large.jpg", "distorted.jpg", subDir,true, executor)
        execSim("large.jpg", "flipped.jpg",  subDir,false,executor)
        execSim("large.jpg", "small.jpg",  subDir,true, executor)
        execSim("small.gif", "small.jpg",  subDir,true, executor) // GIF test.
        execSim("uniform-black.png", "uniform-black.png", subDir, true, executor)
        execSim("uniform-black.png", "uniform-white.png",  subDir,false, executor)
        execSim("uniform-green.png", "uniform-green.png",  subDir,true, executor)
        execSim("uniform-green.png", "uniform-white.png",  subDir,false, executor)
        execSim("uniform-white.png", "uniform-white.png",  subDir,true, executor)
    }


    @Test
    fun similar90270() {
        val executor: simExecutor = { i1, i2 ->
            similar90270(i1, i2)
        }
        val subDir = "rotate"
        execSim("0.jpg", "90.jpg",  subDir,true, executor)
        execSim("0.jpg", "180.jpg",  subDir,false, executor)
        execSim("0.jpg", "270.jpg",  subDir,true, executor)
        execSim("90.jpg", "180.jpg",  subDir,true, executor)
        execSim("90.jpg", "270.jpg",  subDir,false, executor)
    }

    private fun execSim(
        fA: String,
        fB: String,
        subDir: String,
        isSimilar: Boolean,
        exec: simExecutor
    ) {
        var dir = Path(TEST_RESOURCES_DIR, subDir, fA).toFile()
        val imgA = ImageIO.read(dir)
        dir = Path(TEST_RESOURCES_DIR, subDir, fB).toFile()
        val imgB = ImageIO.read(dir)
        val iconA = icon(imgA)
        val iconB = icon(imgB)
        if(isSimilar) {
            assertTrue(exec(iconA, iconB))
        } else {
            assertFalse(exec(iconA, iconB))
        }
    }

}
