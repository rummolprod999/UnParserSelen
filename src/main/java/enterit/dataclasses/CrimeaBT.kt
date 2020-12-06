package enterit.dataclasses

import java.util.*

data class CrimeaBT(
    val purNum: String,
    val hrefT: String,
    val purName: String,
    val pubDate: Date,
    val endDate: Date,
    var placingWayName: String,
    val nmck: String,
    val currency: String
) {
}