package ru.oklookat.images4kt

// Icon parameters.

/** Image resolution of the icon is very small
(11x11 pixels), therefore original image details
are lost in downsampling, except when source images
have very low resolution (e.g. favicons or simple
logos). This is useful from the privacy perspective
if you are to use generated icons in a large searchable
database. */
const val ICON_SIZE = 11

/** Resampling rate defines how much information
(how many pixels) from the source image are used
to generate an icon. Too few will produce worse
comparisons. Too many will consume too much compute. */
internal const val SAMPLES = 12

/** Cutoff value for color distance. */
internal const val COLOR_DIFF = 50

/** Cutoff coefficient for Euclidean distance (squared). */
internal const val EUCLIDEAN_CF = 0.2

/** Coefficient of sensitivity for Cb/Cr channels vs Y. */
internal const val CHAN_CF = 2

/** Euclidean distance threshold (squared) for Y-channel. */
internal const val THY = ((ICON_SIZE * ICON_SIZE) * (COLOR_DIFF * COLOR_DIFF)).toDouble() * EUCLIDEAN_CF

/** Euclidean distance threshold (squared) for Cb and Cr channels. */
internal const val TH_CB_CR = THY * CHAN_CF.toDouble()

/** Proportion similarity threshold (5%). */
internal const val TH_PROP = 0.05

// Auxiliary constants.
internal const val NUM_PIX = ICON_SIZE * ICON_SIZE
internal const val LARGE_ICON_SIZE = ICON_SIZE * 2 + 1
internal const val RESIZED_IMG_SIZE = LARGE_ICON_SIZE * SAMPLES
internal const val INV_SAMPLE_PIXELS_2 = 1.0 / (SAMPLES * SAMPLES)
internal const val ONE_NINTH = 1.0 / 9.0
internal const val ONE_255TH = 1.0 / 255.0
internal const val ONE_255TH_2 = ONE_255TH * ONE_255TH
internal const val SQ_255 = 255 * 255