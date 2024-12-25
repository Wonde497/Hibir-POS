package net.geidea.payment.tlv

import android.annotation.SuppressLint
import java.nio.charset.Charset
import java.util.*

class BerTlv {
    val tag: BerTag
    private val theValue: ByteArray?
    @JvmField
    val theList: List<BerTlv>?

    constructor(aTag: BerTag, aList: List<BerTlv>?) {
        tag = aTag
        theList = aList
        theValue = null
    }

    constructor(aTag: BerTag, aValue: ByteArray?) {
        tag = aTag
        theValue = aValue
        theList = null
    }

    private val isPrimitive: Boolean
        get() = !tag.isConstructed
    val isConstructed: Boolean
        get() = tag.isConstructed

    fun isTag(aTag: BerTag): Boolean {
        return tag == aTag
    }

    fun find(aTag: BerTag): BerTlv? {
        if (aTag == tag) {
            return this
        }
        if (isConstructed) {
            for (tlv in theList!!) {
                val ret = tlv.find(aTag)
                if (ret != null) {
                    return ret
                }
            }
            return null
        }
        return null
    }

    fun findAll(aTag: BerTag): List<BerTlv> {
        val list: MutableList<BerTlv> = ArrayList()
        if (aTag == tag) {
            list.add(this)
            return list
        } else if (isConstructed) {
            for (tlv in theList!!) {
                list.addAll(tlv.findAll(aTag))
            }
        }
        return list
    }

    val hexValue: String
        get() {
            check(!isConstructed) { "Tag is CONSTRUCTED " + HexUtil.toHexString(tag.bytes) }
            return HexUtil.toHexString(theValue!!)
        }
    val textValue: String
        get() = getTextValue(ASCII)

    @SuppressLint("NewApi")
    fun getTextValue(aCharset: Charset?): String {
        check(!isConstructed) { "TLV is constructed" }
        return String(theValue!!, aCharset!!)
    }

    val bytesValue: ByteArray?
        get() {
            check(!isConstructed) { "TLV [$tag]is constructed" }
            return theValue
        }
    val intValue: Int
        get() {
            var j: Int
            var number = 0
            var i: Int = 0
            while (i < theValue!!.size) {
                j = theValue[i].toInt()
                number = number * 256 + if (j < 0) 256.let { j += it; j } else j
                i++
            }
            return number
        }
    val values: List<BerTlv>?
        get() {
            check(!isPrimitive) { "Tag is PRIMITIVE" }
            return theList
        }

    override fun toString(): String {
        return "BerTlv{" + "theTag=" + tag + ", theValue=" + Arrays.toString(theValue) + ", theList=" + theList + '}'
    }

    companion object {
        private val ASCII = Charset.forName("US-ASCII")
    }
}