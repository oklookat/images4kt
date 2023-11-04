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
        var got = arrIndex(Point(x, y), size, ch)
        var expected = 46
        assertEquals(got, expected)

        x = 1
        y = 1
        ch = 1
        got = arrIndex(Point(x, y), size, ch)
        expected = 21
        assertEquals(got, expected)

        x = 3
        y = 3
        ch = 0
        got = arrIndex(Point(x, y), size, ch)
        expected = 15
        assertEquals(got, expected)
    }

    @Test
    fun set() {
        val icon = sizedIcon(4)
        set(icon, 4, Point(1, 1), 13.5, 29.9, 95.9)
        val expected = sizedIcon(4)
        val expectedPixels = listOf(0.0, 0.0, 0.0, 0.0, 0.0, 13.5, 0.0, 0.0, 0.0, 0.0, 0.0,
            0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 29.9, 0.0, 0.0, 0.0, 0.0,
            0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 95.9, 0.0, 0.0, 0.0,
            0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
        for (i in expectedPixels.indices) {
            expected.pixels[i] = (expectedPixels[i] * 255).toInt().toUShort()
        }
        assertEquals(expected.pixels, icon.pixels)
        assertEquals(expected.imgSize, icon.imgSize)
    }

    @Test
    fun get() {
        val icon = sizedIcon(4)
        val iconPix = listOf(0.0, 0.0, 0.0, 0.0, 0.0, 13.5, 0.0, 0.0, 0.0, 0.0, 0.0,
            0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 29.9, 0.0, 0.0, 0.0, 0.0,
            0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 95.9, 0.0, 0.0, 0.0,
            0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
        for (i in iconPix.indices) {
            icon.pixels[i] = (iconPix[i] * 255).toInt().toUShort()
        }
        val (c1, c2,c3) = get(icon, 4, Point(1, 1))
        assertTrue(abs(c1 -13.5) < 0.1)
        assertTrue(abs(c2 -29.9) < 0.1)
        assertTrue(abs(c3-95.9) < 0.1)
    }

    /** Only checking that image size is correct. */
    @Test
    fun icon() {
        val testDir = "resample"
        val imageName = "nearest533x400.png"
        val imgFile = Path(resourcesDir, testDir, imageName).toFile()
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
        assertTrue(abs(y-eY) < 0.1)
        assertTrue(abs(cb-eCb) < 0.1)
        assertTrue(abs(cr-eCr) < 0.1)

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

    private fun testNormalize(src: IconT, want: IconT) {
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
        src.pixels = mutableListOf(8670u, 45801u, 29935u, 11700u, 53747u, 33743u, 44189u, 48647u, 8000u, 49182u, 20434u, 15423u, 46834u, 32946u, 37230u, 63317u, 28058u, 6485u, 29179u, 29196u, 59058u, 49234u, 50741u, 19913u, 64476u, 31201u, 30996u, 28808u, 720u, 48844u, 41325u, 19517u, 30908u, 58705u, 20865u, 4306u, 2909u, 58380u, 28762u, 27511u, 48562u, 5041u, 51122u, 30882u, 57739u, 29392u, 35254u, 61898u, 39625u, 23720u, 59995u, 51153u, 50919u, 28488u, 35064u, 33029u, 33237u, 36843u, 20078u, 25135u, 6877u, 11867u, 56143u, 10160u, 15597u, 43740u, 8877u, 35459u, 60119u, 20334u, 16937u, 56416u, 6827u, 38948u, 44732u, 54001u, 60061u, 60972u, 57112u, 16523u, 61155u, 5701u, 40190u, 55897u, 7134u, 16251u, 39892u, 19855u, 43222u, 54575u, 59722u, 54342u, 23580u, 26257u, 61420u, 815u, 39345u, 10940u, 31442u, 63002u, 52973u, 40687u, 12674u, 32538u, 33552u, 30224u, 40345u, 10436u, 24821u, 50417u, 47839u, 62478u, 37337u, 18230u, 33532u, 45803u, 23903u, 38849u, 35630u, 3627u, 38659u, 31402u, 19608u, 62399u, 48530u, 3753u, 36802u, 25329u, 53769u, 5365u, 12726u, 63463u, 904u, 7375u, 62226u, 451u, 22133u, 221u, 63653u, 62807u, 42256u, 50027u, 21033u, 36161u, 3588u, 46813u, 37872u, 35987u, 986u, 36660u, 1202u, 44719u, 58405u, 36560u, 26745u, 13657u, 23196u, 28188u, 26293u, 56759u, 17684u, 18406u, 54165u, 3998u, 45305u, 53890u, 41361u, 6359u, 7405u, 3328u, 32031u, 56429u, 47514u, 22217u, 16040u, 16167u, 6175u, 48735u, 36290u, 54622u, 41591u, 13457u, 50097u, 41033u, 21848u, 4299u, 49897u, 41964u, 35484u, 52596u, 29367u, 56402u, 10443u, 27805u, 16583u, 25625u, 30523u, 62152u, 23240u, 64471u, 64466u, 59924u, 7174u, 43059u, 46756u, 55581u, 1999u, 24458u, 7400u, 21940u, 57965u, 40574u, 23153u, 51189u, 51073u, 27884u, 61911u, 52357u, 14291u, 5207u, 34548u, 43078u, 19790u, 34009u, 44402u, 27855u, 57427u, 1081u, 62896u, 64235u, 24699u, 22668u, 42119u, 18484u, 56684u, 4080u, 11400u, 6986u, 58352u, 57121u, 48421u, 2320u, 58727u, 32763u, 50307u, 20600u, 2464u, 31682u, 57633u, 36149u, 44016u, 747u, 47489u, 44600u, 29839u, 61025u, 48568u, 8425u, 31064u, 53872u, 11855u, 49332u, 10546u, 21119u, 11459u, 54084u, 42448u, 324u, 11798u, 53552u, 42757u, 25887u, 22761u, 36103u, 2156u, 49624u, 9194u, 47341u, 60071u, 14493u, 26676u, 34587u, 57408u, 64326u, 55415u, 59744u, 34885u, 42007u, 33864u, 49781u, 21178u, 48073u, 58192u, 62091u, 49217u, 30950u, 64026u, 6972u, 59068u, 31061u, 10621u, 18316u, 12150u, 5498u, 29100u, 52325u, 42576u, 55699u, 6835u, 14422u, 27324u, 15251u, 53763u, 53195u, 63126u, 14229u, 29628u, 8018u, 55615u, 16097u, 64658u, 3592u, 876u, 8917u, 20167u, 13926u, 57807u, 17979u, 55333u, 14765u, 20319u, 29759u, 42546u, 41141u, 22034u, 49653u, 44053u, 40404u, 7205u, 13305u, 46645u, 14602u, 15398u, 3116u, 15038u, 6957u, 56078u, 31672u, 19903u, 4166u, 4415u, 38343u, 10208u, 34337u, 40584u, 17486u, 42009u, 27122u, 31383u, 6076u, 50009u, 8451u, 12074u)

        val want = emptyIcon()
        want.pixels = mutableListOf(8108u, 45978u, 29796u, 11198u, 54082u, 33680u, 44334u, 48880u, 7424u, 49426u, 20106u, 14995u, 47031u, 32867u, 37236u, 63842u, 27882u, 5879u, 29025u, 29042u, 59499u, 49479u, 51016u, 19575u, 65025u, 31087u, 30878u, 28647u, 0u, 49081u, 41413u, 19171u, 30788u, 59139u, 20545u, 3657u, 2232u, 58807u, 28600u, 27324u, 48794u, 4407u, 51405u, 30762u, 58153u, 29242u, 35221u, 62395u, 39679u, 23457u, 60454u, 51436u, 51198u, 28320u, 35027u, 32952u, 33164u, 36841u, 19743u, 24900u, 6279u, 11368u, 56526u, 9627u, 15173u, 43876u, 8319u, 35430u, 60581u, 20004u, 16539u, 56804u, 6228u, 38988u, 44888u, 54341u, 60522u, 61451u, 57514u, 16117u, 61637u, 5080u, 40255u, 56275u, 6541u, 15840u, 39951u, 19515u, 43347u, 54926u, 60176u, 54689u, 23315u, 26045u, 61908u, 96u, 39393u, 10423u, 31333u, 63521u, 53293u, 40762u, 12191u, 32451u, 33485u, 30091u, 40413u, 9909u, 24580u, 50686u, 48056u, 62987u, 37345u, 17858u, 33465u, 45980u, 23644u, 38887u, 35604u, 2964u, 38694u, 31557u, 19620u, 62928u, 48891u, 3574u, 37022u, 25410u, 54193u, 5206u, 12655u, 64004u, 691u, 7240u, 62752u, 232u, 22176u, 0u, 64197u, 63340u, 42542u, 50406u, 21063u, 36373u, 3407u, 47154u, 38105u, 36197u, 774u, 36878u, 992u, 45034u, 58885u, 36777u, 26843u, 13598u, 23252u, 28304u, 26386u, 57219u, 17673u, 18404u, 54594u, 3822u, 45627u, 54316u, 41636u, 6212u, 7270u, 3144u, 32193u, 56885u, 47863u, 22261u, 16009u, 16138u, 6025u, 49099u, 36504u, 55057u, 41869u, 13395u, 50477u, 41304u, 21887u, 4127u, 50275u, 42246u, 35688u, 53006u, 29497u, 56858u, 10345u, 27916u, 16559u, 25710u, 30667u, 62678u, 23296u, 65025u, 65019u, 60423u, 7036u, 43354u, 47096u, 56027u, 1799u, 24529u, 7265u, 21980u, 58440u, 40839u, 23208u, 51582u, 51465u, 27996u, 62434u, 52764u, 14239u, 5046u, 34741u, 43373u, 19805u, 34195u, 44713u, 27967u, 57896u, 870u, 63431u, 64786u, 24773u, 22717u, 42403u, 18483u, 57144u, 3905u, 11313u, 6846u, 58832u, 57586u, 48781u, 2124u, 59030u, 32787u, 50519u, 20493u, 2162u, 31694u, 57924u, 36209u, 44161u, 427u, 47671u, 44751u, 29832u, 61352u, 48762u, 8188u, 31070u, 54123u, 11654u, 49534u, 10331u, 21018u, 11254u, 54337u, 42576u, 0u, 11597u, 53799u, 42888u, 25837u, 22677u, 36163u, 1851u, 49829u, 8965u, 47522u, 60388u, 14321u, 26635u, 34631u, 57697u, 64689u, 55682u, 60058u, 34932u, 42130u, 33900u, 49988u, 21077u, 48261u, 58489u, 62430u, 49418u, 30954u, 64386u, 6719u, 59374u, 31067u, 10407u, 18185u, 11953u, 5229u, 29085u, 52559u, 42705u, 55969u, 6580u, 14249u, 27290u, 15087u, 54012u, 53438u, 63476u, 14054u, 29618u, 7776u, 55884u, 15942u, 65025u, 3303u, 557u, 8685u, 20056u, 13748u, 58100u, 17844u, 55599u, 14596u, 20209u, 29751u, 42675u, 41255u, 21943u, 49858u, 44198u, 40510u, 6954u, 13120u, 46818u, 14431u, 15235u, 2821u, 14872u, 6704u, 56352u, 31684u, 19789u, 3883u, 4134u, 38427u, 9990u, 34378u, 40692u, 17346u, 42132u, 27085u, 31392u, 5813u, 50218u, 8214u, 11876u)

        assertDoesNotThrow {
            testNormalize(src, want)
        }
    }

    @Test
    fun rotate() {
        val testDir = "rotate"

        val imgFile0 = Path(resourcesDir, testDir, "0.jpg").toFile()
        val img0 = ImageIO.read(imgFile0)
        val icon0 = icon(img0)

        val imgFile90 = Path(resourcesDir, testDir, "90.jpg").toFile()
        val img90 = ImageIO.read(imgFile90)
        val icon90 = icon(img90)

        assertTrue(similar(rotate90(icon0), icon90))
    }
}