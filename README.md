# Find similar images with Kotlin

**Fork of https://github.com/vitali-fedulov/images4**

## Installation

I haven't figured out how to publish Java projects, so there are two installation options:

1. Download the .jar from the [releases](https://github.com/oklookat/images4kt/releases).
2. Include this project in your project.

## Example of comparing 2 images

```kotlin
import ru.oklookat.images4kt.icon
import ru.oklookat.images4kt.similar
import java.net.URL
import javax.imageio.ImageIO

fun compare() { 
    // Photos to compare.
    val photo1 = URL("https://upload.wikimedia.org/wikipedia/en/c/c1/The_Weeknd_-_After_Hours.png")
    val photo2 = URL("https://upload.wikimedia.org/wikipedia/ru/4/47/LanaDelRey_BornToDie.jpg")
    
    // Read images.
    val img1 = ImageIO.read(photo1)
    val img2 = ImageIO.read(photo2)
    
    // Icons are compact image representations (image "hashes").
    val icon1 = icon(img1)
    val icon2 = icon(img2)
    
    // Comparison. Images are not used directly. Icons are used instead, because they have tiny memory footprint and fast to compare. If you need to include images rotated right and left use func Similar90270.
    if (similar(icon1, icon2)) {
        println("Images are similar.")
    } else {
        println("Images are distinct.")
    }
}
```

## Main functions

- `icon` produces "image hashes" called "icons", which will be used for comparision.

- `icon.rotate90` turns an icon 90° clockwise. This is useful for developing custom similarity function for rotated images with `eucMetric` and `propMetric`. With the function you can also compare to images rotated 180° (by applying `rotate90` twice).

- `similar` gives a verdict whether 2 images are similar with well-tested default thresholds. To see the thresholds use `defaultThresholds`. Rotations and mirrors are not taken in account.

- `similar90270` is a superset of `similar` by additional comparison to images rotated ±90°. Such rotations are relatively common, even by accident when taking pictures on mobile phones.

- `eucMetric` can be used instead of `similar` when you need different precision or want to sort by similarity. [Example](https://github.com/egor-romanov/png2gif/blob/main/main.go#L450) (not mine) of custom similarity function.

- `propMetric` allows customization of image proportion threshold.

- `defaultThresholds` prints default thresholds used in func `similar` and `similar90270`, as a starting point for selecting thresholds on `eucMetric` and `propMetric`.

## Algorithm

[Detailed explanation](https://vitali-fedulov.github.io/similar.pictures/algorithm-for-perceptual-image-comparison.html), also as a [PDF](https://github.com/vitali-fedulov/research/blob/main/Algorithm%20for%20perceptual%20image%20comparison.pdf).

Summary: Images are resized in a special way to squares of fixed size called "icons". Euclidean distance between the icons is used to give the similarity verdict. Also image proportions are used to avoid matching images of distinct shape.

## Customization suggestions

**To increase precision** you can either use your own thresholds in func `eucMetric` (and `propMetric`) OR generate icons for image sub-regions and compare those icons.

**To speedup file processing** you may want to generate icons for available image thumbnails. Specifically, many JPEG images contain [EXIF thumbnails](https://vitali-fedulov.github.io/similar.pictures/jpeg-thumbnail-reader.html), you could considerably speedup the reads by using decoded thumbnails to feed into func `icon`. External packages to read thumbnails: [1](https://github.com/dsoprea/go-exif) and [2](https://github.com/rwcarlsen/goexif). A note of caution: in rare cases there could be [issues](https://security.stackexchange.com/questions/116552/the-history-of-thumbnails-or-just-a-previous-thumbnail-is-embedded-in-an-image/201785#201785) with thumbnails not matching image content. EXIF standard specification: [1](https://www.media.mit.edu/pia/Research/deepview/exif.html) and [2](https://www.exif.org/Exif2-2.PDF).
