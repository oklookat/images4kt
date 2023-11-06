package ru.oklookat.images4kt

/** similar returns similarity verdict based on Euclidean
and proportion similarity. */
fun similar(iconA: IconT, iconB: IconT): Boolean {
    if (!propSimilar(iconA, iconB)) {
        return false
    }
    if (!eucSimilar(iconA, iconB)) {
        return false
    }
    return true
}

/** propSimilar gives a similarity verdict for image A and B based on
their height and width. When proportions are similar, it returns
true. */
internal fun propSimilar(iconA: IconT, iconB: IconT): Boolean {
    return propMetric(iconA, iconB) < thProp
}

/** propMetric gives image proportion similarity metric for image A
and B. The smaller the metric the more similar are images by their
x-y size. */
fun propMetric(iconA: IconT, iconB: IconT): Double {
    // Filtering is based on rescaling a narrower side of images to 1,
    // then cutting off at threshold of a longer image vs shorter image.
    var xA = iconA.imgSize.x.toDouble()
    var yA = iconA.imgSize.y.toDouble()
    var xB = iconB.imgSize.x.toDouble()
    var yB = iconB.imgSize.y.toDouble()

    val m: Double
    if (xA <= yA) { // x to 1.
        yA /= xA
        yB /= xB
        m = if (yA > yB) {
            (yA - yB) / yA
        } else {
            (yB - yA) / yB
        }
    } else { // y to 1.
        xA /= yA
        xB /= yB
        m = if (xA > xB) {
            (xA - xB) / xA
        } else {
            (xB - xA) / xB
        }
    }
    return m
}

/** eucSimilar gives a similarity verdict for image A and B based
on Euclidean distance between pixel values of their icons.
When the distance is small, the function returns true.
iconA and iconB are generated with the Icon function.
eucSimilar wraps EucMetric with well-tested thresholds. */
internal fun eucSimilar(iconA: IconT, iconB: IconT): Boolean {
    val (m1, m2, m3) = eucMetric(iconA, iconB)
    return m1 < thY && // Luma as most sensitive.
            m2 < thCbCr &&
            m3 < thCbCr
}

/** eucMetric returns Euclidean distances between 2 icons.
These are 3 metrics corresponding to each color channel.
Distances are squared, not to waste CPU on square root calculations.
Note: color channels of icons are YCbCr (not RGB). */
fun eucMetric(iconA: IconT, iconB: IconT): Triple<Double, Double, Double> {
    var cA: Double
    var cB: Double
    var m1 = 0.0
    var m2 = 0.0
    var m3 = 0.0
    for (i in 0..<numPix) {
        // Channel 1.
        cA = iconA.pixels[i].toDouble()
        cB = iconB.pixels[i].toDouble()
        m1 += (cA - cB) * one255th2 * (cA - cB)
        // Channel 2.
        cA = iconA.pixels[i + numPix].toDouble()
        cB = iconB.pixels[i + numPix].toDouble()
        m2 += (cA - cB) * one255th2 * (cA - cB)
        // Channel 3.
        cA = iconA.pixels[i + 2 * numPix].toDouble()
        cB = iconB.pixels[i + 2 * numPix].toDouble()
        m3 += (cA - cB) * one255th2 * (cA - cB)
    }
    return Triple(m1, m2, m3)
}

/** Print default thresholds for func similar. */
fun defaultThresholds() {
    println("*** Default thresholds ***")
    println("Euclidean distance thresholds (YCbCr): m1=$thY, m2=$thCbCr, m3=$thCbCr")
    println("Proportion threshold: m=$thProp")
}

/** Similar90270 works like Similar, but also considers rotations of ±90°.
Those are rotations users might reasonably often do. */
fun similar90270(iconA: IconT, iconB: IconT): Boolean {
    if (similar(iconA, iconB)) return true

    // iconB rotated 90 degrees.
    if (similar(iconA, rotate90(iconB))) return true

    // As if iconB was rotated 270 degrees.
    if (similar(rotate90(iconA), iconB)) return true

    return false
}