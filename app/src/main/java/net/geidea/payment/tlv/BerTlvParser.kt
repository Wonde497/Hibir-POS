package net.geidea.payment.tlv

class BerTlvParser @JvmOverloads constructor(private val log: IBerTlvLogger = EMPTY_LOGGER) {
    @JvmOverloads
    fun parseConstructed(aBuf: ByteArray, aOffset: Int, aLen: Int): BerTlv {
        val result = parseWithResult(0, aBuf, aOffset, aLen)
        return result.tlv
    }

    @JvmOverloads
    fun parse(aBuf: ByteArray, aOffset: Int=0, aLen: Int=aBuf.size): BerTlvs {
        val tlvs: MutableList<BerTlv> = ArrayList()
        if (aLen == 0) {
            return BerTlvs(tlvs)
        }
        var offset = aOffset
        for (i in 0..199) {
            val result = parseWithResult(0, aBuf, offset, aLen - offset)
            tlvs.add(result.tlv)
            if (result.offset >= aOffset + aLen) {
                break
            }
            offset = result.offset
        }
        return BerTlvs(tlvs)
    }

    private fun parseWithResult(
        aLevel: Int,
        aBuf: ByteArray,
        aOffset: Int,
        aLen: Int
    ): ParseResult {
        val levelPadding = createLevelPadding(aLevel)
        check(aOffset + aLen <= aBuf.size) {
            "Length is out of the range " +
                    "[offset=" + aOffset + ", " + " len=" + aLen + ", " +
                    "array.length=" + aBuf.size + ", level=" + aLevel + "]"
        }
        if (log.isDebugEnabled) {
            log.debug(
                "{}parseWithResult(level={}, offset={}, len={}, buf={})",
                levelPadding,
                aLevel,
                aOffset,
                aLen,
                HexUtil.toFormattedHexString(aBuf, aOffset, aLen)
            )
        }
        val tagBytesCount = getTagBytesCount(aBuf, aOffset)
        val tag = createTag(levelPadding, aBuf, aOffset, tagBytesCount)
        if (log.isDebugEnabled) {
            log.debug(
                "{}tag = {}, tagBytesCount={}, tagBuf={}",
                levelPadding,
                tag,
                tagBytesCount,
                HexUtil.toFormattedHexString(aBuf, aOffset, tagBytesCount)
            )
        }
        val lengthBytesCount = getLengthBytesCount(aBuf, aOffset + tagBytesCount)
        val valueLength = getDataLength(aBuf, aOffset + tagBytesCount)
        if (log.isDebugEnabled) {
            log.debug(
                "{}lenBytesCount = {}, len = {}, lenBuf = {}",
                levelPadding, lengthBytesCount, valueLength,
                HexUtil.toFormattedHexString(aBuf, aOffset + tagBytesCount, lengthBytesCount)
            )
        }
        return if (tag.isConstructed) {
            val list = ArrayList<BerTlv>()
            addChildren(
                aLevel,
                aBuf,
                aOffset,
                levelPadding,
                tagBytesCount,
                lengthBytesCount,
                valueLength,
                list
            )
            val resultOffset = aOffset + tagBytesCount + lengthBytesCount + valueLength
            if (log.isDebugEnabled) {
                log.debug("{}returning constructed offset = {}", levelPadding, resultOffset)
            }
            ParseResult(BerTlv(tag, list), resultOffset)
        } else {
            val value = ByteArray(valueLength)
            System.arraycopy(
                aBuf,
                aOffset + tagBytesCount + lengthBytesCount,
                value,
                0,
                valueLength
            )
            val resultOffset = aOffset + tagBytesCount + lengthBytesCount + valueLength
            if (log.isDebugEnabled) {
                log.debug("{}value = {}", levelPadding, HexUtil.toFormattedHexString(value))
                log.debug("{}returning primitive offset = {}", levelPadding, resultOffset)
            }
            ParseResult(BerTlv(tag, value), resultOffset)
        }
    }

    private fun addChildren(
        aLevel: Int, aBuf: ByteArray, aOffset: Int, levelPadding: String,
        aTagBytesCount: Int, aDataBytesCount: Int, valueLength: Int,
        list: ArrayList<BerTlv>
    ) {
        var startPosition = aOffset + aTagBytesCount + aDataBytesCount
        var len = valueLength
        while (startPosition < aOffset + valueLength) {
            val result = parseWithResult(aLevel + 1, aBuf, startPosition, len)
            list.add(result.tlv)
            startPosition = result.offset
            len = valueLength - startPosition
            if (log.isDebugEnabled) {
                log.debug(
                    "{}level {}: adding {} with offset {}, " + "startPosition={}," +
                            " " + "aDataBytesCount={}, " + "valueLength={}",
                    levelPadding, aLevel, result.tlv.tag, result.offset,
                    startPosition, aDataBytesCount, valueLength
                )
            }
        }
    }

    private fun createLevelPadding(aLevel: Int): String {
        if (!log.isDebugEnabled) {
            return ""
        }
        val sb = StringBuilder()
        for (i in 0 until aLevel * 4) {
            sb.append(' ')
        }
        return sb.toString()
    }

    private class ParseResult(val tlv: BerTlv, val offset: Int) {
        override fun toString(): String {
            return "ParseResult{tlv=$tlv, offset=$offset}"
        }
    }

    private fun createTag(
        aLevelPadding: String, aBuf: ByteArray, aOffset: Int,
        aLength: Int
    ): BerTag {
        if (log.isDebugEnabled) {
            log.debug(
                "{}Creating tag {}...", aLevelPadding,
                HexUtil.toFormattedHexString(aBuf, aOffset, aLength)
            )
        }
        return BerTag(aBuf, aOffset, aLength)
    }

    private fun getTagBytesCount(aBuf: ByteArray, aOffset: Int): Int {
        return if (aBuf[aOffset].toInt() and 0x1F == 0x1F) {
            var len = 2
            for (i in aOffset + 1 until aOffset + 10) {
                if (aBuf[i].toInt() and 0x80 != 0x80) {
                    break
                }
                len++
            }
            len
        } else {
            1
        }
    }

    private fun getDataLength(aBuf: ByteArray, aOffset: Int): Int {
        var length = aBuf[aOffset].toInt() and 0xff
        if (length and 0x80 == 0x80) {
            val numberOfBytes = length and 0x7f
            check(numberOfBytes <= 3) {
                String.format(
                    "At position %d the len is more then 3 [%d]",
                    aOffset, numberOfBytes
                )
            }
            length = 0
            for (i in aOffset + 1 until aOffset + 1 + numberOfBytes) {
                length = length * 0x100 + (aBuf[i].toInt() and 0xff)
            }
        }
        return length
    }

    private fun getLengthBytesCount(aBuf: ByteArray, aOffset: Int): Int {
        val len = aBuf[aOffset].toInt() and 0xff
        return if (len and 0x80 == 0x80) {
            1 + (len and 0x7f)
        } else {
            1
        }
    }

    companion object {
        private val EMPTY_LOGGER: IBerTlvLogger = object : IBerTlvLogger {

            override val isDebugEnabled: Boolean = false

            override fun debug(aFormat: String?, vararg args: Any?) {
                //do nothing
            }
        }
    }
}