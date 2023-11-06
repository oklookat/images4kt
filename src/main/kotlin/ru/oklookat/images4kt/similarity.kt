package ru.oklookat.images4kt

/** Similarity verdict based on Euclidean
and proportion similarity. */
fun similar(iconA: Icon, iconB: Icon): Boolean {
    if (!propSimilar(iconA, iconB)) {
        return false
    }
    if (!eucSimilar(iconA, iconB)) {
        return false
    }
    return true
}

/** Similarity verdict for image A and B based on
their height and width. When proportions are similar, it returns
true. */
internal fun propSimilar(iconA: Icon, iconB: Icon): Boolean {
    return propMetric(iconA, iconB) < TH_PROP
}

/** Image proportion similarity metric for image A
and B. The smaller the metric the more similar are images by their
x-y size. */
fun propMetric(iconA: Icon, iconB: Icon): Double {
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

/** Similarity verdict for image A and B based
on Euclidean distance between pixel values of their icons.
When the distance is small, the function returns true.
iconA and iconB are generated with the icon function.
eucSimilar wraps EucMetric with well-tested thresholds. */
internal fun eucSimilar(iconA: Icon, iconB: Icon): Boolean {
    val (m1, m2, m3) = eucMetric(iconA, iconB)
    return m1 < THY && // Luma as most sensitive.
            m2 < TH_CB_CR &&
            m3 < TH_CB_CR
}

/** Euclidean distances between 2 icons.
These are 3 metrics corresponding to each color channel.
Distances are squared, not to waste CPU on square root calculations.

Note: color channels of icons are YCbCr (not RGB). */
fun eucMetric(iconA: Icon, iconB: Icon): Triple<Double, Double, Double> {
    var cA: Double
    var cB: Double
    var m1 = 0.0
    var m2 = 0.0
    var m3 = 0.0
    for (i in 0..<NUM_PIX) {
        // Channel 1.
        cA = iconA.pixels[i].toDouble()
        cB = iconB.pixels[i].toDouble()
        m1 += (cA - cB) * ONE_255TH_2 * (cA - cB)
        // Channel 2.
        cA = iconA.pixels[i + NUM_PIX].toDouble()
        cB = iconB.pixels[i + NUM_PIX].toDouble()
        m2 += (cA - cB) * ONE_255TH_2 * (cA - cB)
        // Channel 3.
        cA = iconA.pixels[i + 2 * NUM_PIX].toDouble()
        cB = iconB.pixels[i + 2 * NUM_PIX].toDouble()
        m3 += (cA - cB) * ONE_255TH_2 * (cA - cB)
    }
    return Triple(m1, m2, m3)
}

/** Prints default thresholds for func similar. */
fun defaultThresholds() {
    println("*** Default thresholds ***")
    println("Euclidean distance thresholds (YCbCr): m1=$THY, m2=$TH_CB_CR, m3=$TH_CB_CR")
    println("Proportion threshold: m=$TH_PROP")
}

/** Works like 'similar' function, but also considers rotations of ±90°.
Those are rotations users might reasonably often do. */
fun similar90270(iconA: Icon, iconB: Icon): Boolean {
    if (similar(iconA, iconB)) return true

    // iconB rotated 90 degrees.
    if (similar(iconA, iconB.rotate90())) return true

    // As if iconB was rotated 270 degrees.
    if (similar(iconA.rotate90(), iconB)) return true

    return false
}