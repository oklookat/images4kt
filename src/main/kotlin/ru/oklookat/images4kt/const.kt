package ru.oklookat.images4kt

// Icon parameters.

/** Image resolution of the icon is very small
 (11x11 pixels), therefore original image details
 are lost in downsampling, except when source images
 have very low resolution (e.g. favicons or simple
 logos). This is useful from the privacy perspective
 if you are to use generated icons in a large searchable
 database. */
const val IconSize = 11

/** Resampling rate defines how much information
 (how many pixels) from the source image are used
 to generate an icon. Too few will produce worse
 comparisons. Too many will consume too much compute. */
internal const val samples = 12

/** Cutoff value for color distance. */
internal const val colorDiff = 50

/** Cutoff coefficient for Euclidean distance (squared). */
internal const val euclCoeff = 0.2

/** Coefficient of sensitivity for Cb/Cr channels vs Y. */
internal const val chanCoeff = 2

/** Euclidean distance threshold (squared) for Y-channel. */
internal val thY = ((IconSize * IconSize) * (colorDiff * colorDiff)).toDouble() * euclCoeff

/** Euclidean distance threshold (squared) for Cb and Cr channels. */
internal val thCbCr = thY * chanCoeff.toDouble()

/** Proportion similarity threshold (5%). */
internal val thProp = 0.05

// Auxiliary constants.
internal const val numPix = IconSize * IconSize
internal const val largeIconSize = IconSize * 2 + 1
internal const val resizedImgSize = largeIconSize * samples
internal val invSamplePixels2 = 1.0 / (samples * samples)
internal val oneNinth = 1.0 / 9.0
internal val one255th = 1.0 / 255.0
internal val one255th2 = one255th * one255th
internal val sq255 = 255 * 255