package enterit.dataclasses

import java.util.*

data class SibPrimeT(
    val purNum: String,
    val hrefT: String,
    val hrefL: String,
    val purName: String,
    val pubDate: Date,
    val endDate: Date,
    var status: String,
    var placingWayName: String,
    var nameCus: String
) {
}