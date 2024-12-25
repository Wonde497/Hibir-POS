package net.geidea.payment.tlv

object BerTlvLogger {
    fun log(aPadding: String, aTlv: BerTlvs, aLogger: IBerTlvLogger) {
        for (tlv in aTlv.list) {
            log(aPadding, tlv, aLogger)
        }
    }

    fun log(aPadding: String, aTlv: BerTlv?, aLogger: IBerTlvLogger) {
        if (aTlv == null) {
            aLogger.debug("{} is null", aPadding)
            return
        }
        if (aTlv.isConstructed) {
            aLogger.debug("{} [{}]", aPadding, HexUtil.toHexString(aTlv.tag.bytes))
            for (child in aTlv.values!!) {
                log("$aPadding    ", child, aLogger)
            }
        } else {
            aLogger.debug(
                "{} [{}] {}",
                aPadding,
                HexUtil.toHexString(aTlv.tag.bytes),
                aTlv.hexValue
            )
        }
    }
}