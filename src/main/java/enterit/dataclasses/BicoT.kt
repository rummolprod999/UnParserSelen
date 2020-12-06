package enterit.dataclasses

import java.util.*

data class BicoT(
    val purNum: String,
    val hrefT: String,
    val purName: String,
    val pubDate: Date,
    val endDate: Date,
    val placingWayName: String,
    val nmck: String,
    val currency: String,
    val region: String,
    val otr: String
) {
}