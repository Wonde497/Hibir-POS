package net.geidea.payment.transaction.model

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.annotation.Keep
import net.geidea.payment.DBHandler
import net.geidea.payment.TxnType
import net.geidea.payment.tlv.HexUtil
import java.nio.ByteBuffer

@Keep
class TransData(private val context:Context) {
    val sharedPreferences=context.getSharedPreferences("SHARED_DATA",Context.MODE_PRIVATE)
    private lateinit var editor: SharedPreferences.Editor


    private var txnType=sharedPreferences.getString("TXN_TYPE","")
    var txnMenuType=sharedPreferences.getString("TXN_MENU_TYPE","")

    var entryMode:String = ""
    var cardLabelNameEng = ""
    var isOnlinePin=false
    var amount:Long = 0L
    var pinBlock = ""
    var transactionReqDateTime = ""
    var aid = ""
    var tvr = ""
    var rrn = ""
    var stan = ""
    var transactionStatus = false
    var transactionType = ""
    var genACResult = ""
    var pan = ""
    var applicationCryptogram = ""
    var cardExpiryDate = ""
    var verificationMethod = 0
    val TAG="TransData"
    var track2=""

    override fun toString(): String {
        return "TransData(" +
                "entryMode='$entryMode', " +
                "cardLabelNameEng='$cardLabelNameEng', " +
                "isOnlinePin=$isOnlinePin, " +
                "amount=$amount, " +
                "pinBlock='$pinBlock', " +
                "transactionReqDateTime='$transactionReqDateTime', " +
                "aid='$aid', " +
                "tvr='$tvr', " +
                "rrn='$rrn', " +
                "stan='$stan', " +
                "transactionStatus=$transactionStatus, " +
                "transactionType='$transactionType', " +
                "genACResult='$genACResult', " +
                "pan='$pan', " +
                "applicationCryptogram='$applicationCryptogram', " +
                "cardExpiryDate='$cardExpiryDate', " +
                "verificationMethod=$verificationMethod" +
                ")"
    }
    class RequestFields{
        companion object{
            var Header=""
            var MTI=""
            var primaryBitmap=""
            var Field02=""
            var Field03=""
            var Field04=""
            var Field07=""
            var Field11=""
            var Field12=""
            var Field13=""
            var Field14=""
            var Field22="0051"
            var Field24="0001"
            var Field25="00"
            var Field35=""
            var Field37=""
            var Field38=""
            var Field39= ""
            var Field41=""
            var Field42=""
            var Field49="230"
            var Field52=""
            var Field55=""
            var Field60="0012"
            var Field62="0006"
            var Field63=""
            var endValue4F63=""

        }



    }
    class ResponseFields{
        companion object{
            var Header=""
            var MTI=""
            var primaryBitmap=""
            var Field02=""
            var Field03=""
            var Field04=""
            var Field07=""
            var Field11=""
            var Field12=""
            var Field13=""
            var Field22=""
            var Field24="200"
            var Field25="00"
            var Field35=""
            var Field37=""
            var Field38=""
            var Field39=""
            var Field41=""
            var Field42=""
            var Field49=""
            var Field52=""
            var Field53=""
            var Field54=""
            var Field55=""
            var Field62=""

        }



    }
    fun assignValue2Fields(){

        txnType=sharedPreferences.getString("TXN_TYPE","")
         //txnMenuType=sharedPreferences.getString("TXN_MENU_TYPE","")
        //Log.d(TAG,"txnMenuType${txnMenuType}")

        if(!txnType.equals(TxnType.REFUND)&&!txnType.equals(TxnType.KEY_DOWNLOAD) && !txnType.equals(TxnType.SETTLEMENT) && !txnType.equals(TxnType.REVERSAL)) {
            Log.d(TAG, "txntype2:$txnType")

            RequestFields.Field11 = stan
            Log.d(TAG, "stan...:" + RequestFields.Field11)
        }

        Log.d(TAG, "txn type$txnType")
        Log.d(TAG, "entry mode$entryMode")
        if(txnType.equals(TxnType.PURCHASE)){
            if(entryMode == EntryMode.CONTACTLESS.toString()){
                Log.d(TAG, "txn type$txnType")
                RequestFields.Field22="0070"
            }else {
                RequestFields.Field22="0051"
            }


            RequestFields. Header="30606020153535"
            RequestFields.MTI="0200"
            RequestFields.Field03="000000"
            RequestFields.Field25="00"
            RequestFields.Field62="0006"

            if(isOnlinePin){
                RequestFields.primaryBitmap="7024058020C01204"

            }else{
                RequestFields.primaryBitmap="7024058020C00204"
            }


        } else if(txnType.equals(TxnType.REVERSAL)){
            RequestFields. Header="30606020153535"
            RequestFields.MTI="0200"
            RequestFields.primaryBitmap="703C05802EC00014"
            RequestFields.Field03="020000"
            if(entryMode == EntryMode.CONTACTLESS.toString()){
                RequestFields.Field22="0070"
            }else {
                RequestFields.Field22="0051"
            }
        }else if(txnType.equals(TxnType.M_REVERSAL)){
            RequestFields. Header="30606020153535"
            RequestFields.MTI="0200"
            RequestFields.Field03="000000"
            RequestFields.primaryBitmap="703C05800EC00014"
            RequestFields.Field22="0010"
            RequestFields.Field24="0001"
            RequestFields.Field25="00"



        }


if(!txnType.equals(TxnType.M_REVERSAL)) {
    RequestFields.Field02 = pan
    }
        if(txnType.equals(TxnType.REFUND)){
           RequestFields.Header="30606020153535"
          RequestFields.MTI="0200"
            RequestFields.Field03= "200000"
            if(isOnlinePin){
                RequestFields.primaryBitmap="703C05802EC01204"
            } else{
                RequestFields.primaryBitmap="703C05802EC00204"
            }
            if(entryMode == EntryMode.CONTACTLESS.toString()){
                Log.d(TAG, "txn type$txnType")
                RequestFields.Field22="0070"
            }else {
                RequestFields.Field22="0051"
            }
        }
        if(txnType.equals(TxnType.BALANCE_INQUIRY)){
            RequestFields.Header="30606020153535"
            RequestFields.MTI="0100"

            if(isOnlinePin){
                RequestFields.primaryBitmap="6024058020C01204"

            }else{
                RequestFields.primaryBitmap="6024058020C00204"
            }
            RequestFields.Field03="310000"
            if(entryMode==EntryMode.CONTACTLESS.toString()){
                Log.d(TAG, "txn type$txnType")
                RequestFields.Field22="0070"
            }else {
                RequestFields.Field22="0051"
             }
            RequestFields.Field24="0001"
            RequestFields.Field25="00"
        }
        if(txnType.equals(TxnType.PRE_AUTH)){
            RequestFields.Header="30606020153535"
            RequestFields.MTI="0100"
            if(isOnlinePin){
                RequestFields.primaryBitmap="7024058020C01204"

            }else{
                RequestFields.primaryBitmap="7024058020C00204"
               }
            RequestFields.Field03="300000"
            if(entryMode == EntryMode.CONTACTLESS.toString()){
                Log.d(TAG, "txn type$txnType")
                RequestFields.Field22="0070"
            }else {
                RequestFields.Field22="0051"
            }
            RequestFields.Field24="0001"
            RequestFields.Field25="00"
        }
        if(txnType.equals(TxnType.PRE_AUTH_COMPLETION)){
            RequestFields.Header="30606020153535"
            RequestFields.MTI="0200"
            RequestFields.Field03= "000005"
            if(isOnlinePin){
                RequestFields.primaryBitmap="703C05802EC01204"
            } else{
                RequestFields.primaryBitmap="703C05802EC00204"
            }
            if(entryMode == EntryMode.CONTACTLESS.toString()){
                Log.d(TAG, "txn type$txnType")
                RequestFields.Field22="0070"
            }else {
                RequestFields.Field22="0051"
            }
        }
        // RequestFields.FielFd07= SimpleDateFormat("MMddhhmmss", Locale.getDefault()).format(Date())
        Log.d(TAG, "trantype:$txnType")

        // RequestFields.Field12=SimpleDateFormat("yyMMddhhmmss", Locale.getDefault()).format( Date())
        if(!txnType.equals(TxnType.KEY_DOWNLOAD ) && !txnType.equals(TxnType.M_PURCHASE )&& !txnType.equals(TxnType.M_REVERSAL )  && !txnType.equals(TxnType.SETTLEMENT) ){
                RequestFields.Field14 = cardExpiryDate.substring(0, 4)//+"01"
            }
        Log.d(TAG,"Field14:"+RequestFields.Field14)

         if(RequestFields.Field35.endsWith("F")){
            RequestFields.Field35=RequestFields.Field35.substringBefore("F")

          }

        Log.d(TAG,"Field35:"+RequestFields.Field35)
        val dbHandler=DBHandler(context)
        RequestFields.Field41=dbHandler.getTID()//"TID12345"
        Log.d(TAG,"terminalID:"+RequestFields.Field41)
        RequestFields.Field42=dbHandler.getMID()//"MID123456789012"
        Log.d(TAG,"merchantID:"+RequestFields.Field42)
        //RequestFields.Field52
        Log.d(TAG,"Field52:"+RequestFields.Field52)

    }


    fun packFields4TimeoutReversal():ByteArray {

        var j = 0
        var i = 0
        var fieldLength=0
        //   listOfByteArrays="".toByteArray()
        val listOfByteArraysHeader = mutableListOf<ByteArray>()
        val listOfByteArraysMTI = mutableListOf<ByteArray>()
        val listOfByteArraysBMP = mutableListOf<ByteArray>()
        var lengthOfHeader=0
        var lengthOfMTI=0

        var lengthOfBitmap = 0

        var charArrayHeader="".toCharArray()
        var charArrayMTI="".toCharArray()
        var charArrayBmp="".toCharArray()
        val listOfByteArrayslenF02 = mutableListOf<ByteArray>()
        var lengthOfFlen02=0
        var charArraylenF02="".toCharArray()

        var listOfByteArraysF02 = mutableListOf<ByteArray>()
        var lengthOfF02=0
        var charArrayF02="".toCharArray()
        //---------------------------------------------------------------
        var listOfByteArraysF03 = mutableListOf<ByteArray>()
        var lengthOfF03=0
        var charArrayF03="".toCharArray()
        //---------------------------------------------------------------
        var listOfByteArraysF04 = mutableListOf<ByteArray>()
        var lengthOfF04=0
        var charArrayF04="".toCharArray()
        //---------------------------------------------------------------
        var listOfByteArraysF11 = mutableListOf<ByteArray>()
        var lengthOfF11=0
        var charArrayF11="".toCharArray()
        //---------------------------------------------------------------
        var listOfByteArraysF14 = mutableListOf<ByteArray>()
        var lengthOfF14=0
        var charArrayF14="".toCharArray()
        //---------------------------------------------------------------
        var listOfByteArraysF22 = mutableListOf<ByteArray>()
        var lengthOfF22=0
        var charArrayF22="".toCharArray()
        //---------------------------------------------------------------
        var listOfByteArraysF24 = mutableListOf<ByteArray>()
        var lengthOfF24=0
        var charArrayF24="".toCharArray()
        //---------------------------------------------------------------
        var listOfByteArraysF25 = mutableListOf<ByteArray>()
        var lengthOfF25=0
        var charArrayF25="".toCharArray()
        //---------------------------------------------------------------

        var listOfByteArraysF62 = mutableListOf<ByteArray>()
        var lengthOfF62=0
        var charArrayF62="".toCharArray()

        //---------------------------------------------------------------

        RequestFields.Header="30606020153535"

        fieldLength = RequestFields.Header.length / 2
        Log.d("TransData", "headertry.........: ${RequestFields.Header}")

        val  header= Array(fieldLength) { "" }
        charArrayHeader = RequestFields.Header.toCharArray()

        j=0
        for (i in 0 until fieldLength * 2 step 2) {
            header[j] = "${charArrayHeader[i]}${charArrayHeader[i + 1]}"
            j++
        }

        //--------------------------------------------------------------- end header/

        RequestFields.MTI="0400"
        fieldLength = RequestFields.MTI.length / 2
        Log.d("TransData", "mti.........: ${RequestFields.MTI}")

        val  mti= Array(fieldLength) { "" }

        charArrayMTI = RequestFields.MTI.toCharArray()

        j=0
        for (i in 0 until fieldLength * 2 step 2) {
            mti[j] = "${charArrayMTI[i]}${charArrayMTI[i + 1]}"
            j++
        }


        //--------------------------------------------------------------- end mti/
        RequestFields.primaryBitmap="7024058000C00004"
        val bitmaplength = RequestFields.primaryBitmap.length / 2
        Log.d("TransData", "bitmaptry.........: ${RequestFields.primaryBitmap}")

        val primarybitmap = Array(bitmaplength) { "" }
        charArrayBmp = RequestFields.primaryBitmap.toCharArray()

        j=0
        for (i in 0 until bitmaplength * 2 step 2) {
            primarybitmap[j] = "${charArrayBmp[i]}${charArrayBmp[i + 1]}"
            j++
        }
        //--------------------------------------------------------------- end of bmp/




        //---------------------------------------------------------------
        val Fld02lenint=RequestFields.Field02.length
        if(RequestFields.Field02.length % 2 !=0) {
            fieldLength = (Fld02lenint+1).toString().length / 2
        }
        else {
            fieldLength = Fld02lenint.toString().length / 2
        }
        Log.d("TransData", "mti.........: " + Fld02lenint)

        val  Fld02lenstr= Array(fieldLength) { "" }

        charArraylenF02 = Fld02lenint.toString().toCharArray()

        j=0
        for (i in 0 until fieldLength * 2 step 2) {
            Fld02lenstr[j] = "${charArraylenF02[i]}${charArraylenF02[i + 1]}"
            j++
        }





        fieldLength = RequestFields.Field02.length / 2
        Log.d("TransData", "headertry.........: ${RequestFields.Field02}")

        val  fld02= Array(fieldLength) { "" }
        charArrayF02 = RequestFields.Field02.toCharArray()

        j=0
        for (i in 0 until fieldLength * 2 step 2) {
            fld02[j] = "${charArrayF02[i]}${charArrayF02[i + 1]}"
            j++
        }

        //--------------------------------------------------------------- end field02/

        fieldLength = RequestFields.Field03.length / 2
        Log.d("TransData", "headertry.........: ${RequestFields.Field03}")

        val  fld03= Array(fieldLength) { "" }
        charArrayF03 = RequestFields.Field03.toCharArray()

        j=0
        for (i in 0 until fieldLength * 2 step 2) {
            fld03[j] = "${charArrayF03[i]}${charArrayF03[i + 1]}"
            j++
        }

        //--------------------------------------------------------------- end field03/

        fieldLength = RequestFields.Field04.length / 2
        Log.d("TransData", "headertry.........: ${RequestFields.Field04}")

        val  fld04= Array(fieldLength) { "" }
        charArrayF04 = RequestFields.Field04.toCharArray()

        j=0
        for (i in 0 until fieldLength * 2 step 2) {
            fld04[j] = "${charArrayF04[i]}${charArrayF04[i + 1]}"
            j++
        }

        //--------------------------------------------------------------- end field04/



        fieldLength = RequestFields.Field11.length / 2
        Log.d("TransData", "headertryF11.........: ${RequestFields.Field11}")

        val  fld11= Array(fieldLength) { "" }
        charArrayF11 = RequestFields.Field11.toCharArray()

        j=0
        for (i in 0 until fieldLength * 2 step 2) {
            fld11[j] = "${charArrayF11[i]}${charArrayF11[i + 1]}"
            j++
        }

        //--------------------------------------------------------------- end of field11/


        fieldLength = RequestFields.Field14.length / 2
        Log.d("TransData", "headertry.........: ${RequestFields.Field14}")

        val  fld14= Array(fieldLength) { "" }
        charArrayF14 = RequestFields.Field14.toCharArray()

        j=0
        for (i in 0 until fieldLength * 2 step 2) {
            fld14[j] = "${charArrayF14[i]}${charArrayF14[i + 1]}"
            j++
        }

        //--------------------------------------------------------------- end field14/
        RequestFields.Field22="0010"
        fieldLength = RequestFields.Field22.length / 2
        Log.d("TransData", "headertry.........: ${RequestFields.Field22}")

        val  fld22= Array(fieldLength) { "" }
        charArrayF22 = RequestFields.Field22.toCharArray()

        j=0
        for (i in 0 until fieldLength * 2 step 2) {
            fld22[j] = "${charArrayF22[i]}${charArrayF22[i + 1]}"
            j++
        }

        //--------------------------------------------------------------- end field22/

        RequestFields.Field24="0001"
        fieldLength = RequestFields.Field24.length / 2
        Log.d("TransData", "headertry.........: ${RequestFields.Field24}")

        val  fld24= Array(fieldLength) { "" }
        charArrayF24 = RequestFields.Field24.toCharArray()

        j=0
        for (i in 0 until fieldLength * 2 step 2) {
            fld24[j] = "${charArrayF24[i]}${charArrayF24[i + 1]}"
            j++
        }

        //--------------------------------------------------------------- end field24/
        RequestFields.Field25="00"
        fieldLength = RequestFields.Field25.length / 2
        Log.d("TransData", "headertry.........: ${RequestFields.Field25}")

        val  fld25= Array(fieldLength) { "" }
        charArrayF25 = RequestFields.Field25.toCharArray()

        j=0
        for (i in 0 until fieldLength * 2 step 2) {
            fld25[j] = "${charArrayF25[i]}${charArrayF25[i + 1]}"
            j++
        }
        //---------------------------------------------------------------end field25/


        fieldLength = RequestFields.Field62.length / 2
        Log.d("TransData", "headertry.........: ${RequestFields.Field62}")

        val  fld62= Array(fieldLength) { "" }
        charArrayF62 = RequestFields.Field62.toCharArray()

        j=0
        for (i in 0 until fieldLength * 2 step 2) {
            fld62[j] = "${charArrayF62[i]}${charArrayF62[i + 1]}"
            j++
        }


        //--------------------------------------------------------------- end of field 62/





        /// ----------------------length of fields-------------------------------//
        for (r in header.indices) {
            listOfByteArraysHeader.add(HexUtil.hexStr2Byte(header[r]))
        }
        Log.d(TAG, "list of byte arrays: $listOfByteArraysHeader")


        for (byteArray1 in listOfByteArraysHeader) {
            lengthOfHeader += byteArray1.size
        }
        //---------------------------------------------------------------
        for (r in mti.indices) {
            listOfByteArraysMTI.add(HexUtil.hexStr2Byte(mti[r]))
        }
        Log.d(TAG, "list of byte arrays: $listOfByteArraysMTI")


        for (byteArray1 in listOfByteArraysMTI) {
            lengthOfMTI += byteArray1.size
        }
        //val listOfByteArrays1 = mutableListOf<ByteArray>()
        for (r in primarybitmap.indices) {
            listOfByteArraysBMP.add(HexUtil.hexStr2Byte(primarybitmap[r]))
        }
        Log.d(TAG, "list of byte arrays1: $listOfByteArraysBMP")
        //---------------------------------------------------------------
        lengthOfBitmap = 0
        for (byteArray1 in listOfByteArraysBMP) {
            lengthOfBitmap += byteArray1.size
        }
        //---------------------------------------------------------------
        for (r in Fld02lenstr.indices) {
            listOfByteArrayslenF02.add(HexUtil.hexStr2Byte(Fld02lenstr[r]))
        }
        Log.d(TAG, "list of byte arrayslengf02: $listOfByteArrayslenF02")


        for (byteArray1 in listOfByteArrayslenF02) {
            lengthOfFlen02 += byteArray1.size
        }
        Log.d(TAG,"lenf02"+lengthOfFlen02)
        for (r in fld02.indices) {
            listOfByteArraysF02.add(HexUtil.hexStr2Byte(fld02[r]))
        }
        Log.d(TAG, "list of byte arraysF02: $listOfByteArraysF02")


        for (byteArray1 in listOfByteArraysF02) {
            lengthOfF02 += byteArray1.size
        }
        Log.d(TAG,"lenf02"+lengthOfF02)


        //---------------------------------------------------------------
        for (r in fld03.indices) {
            listOfByteArraysF03.add(HexUtil.hexStr2Byte(fld03[r]))
        }
        Log.d(TAG, "list of byte arrays: $listOfByteArraysF03")


        for (byteArray1 in listOfByteArraysF03) {
            lengthOfF03 += byteArray1.size
        }

        //---------------------------------------------------------------
        for (r in fld04.indices) {
            listOfByteArraysF04.add(HexUtil.hexStr2Byte(fld04[r]))
        }
        Log.d(TAG, "list of byte arrays: $listOfByteArraysF04")


        for (byteArray1 in listOfByteArraysF04) {
            lengthOfF04 += byteArray1.size
        }

        //---------------------------------------------------------------
        for (r in fld11.indices) {
            listOfByteArraysF11.add(HexUtil.hexStr2Byte(fld11[r]))
        }
        Log.d(TAG, "list of byte arraysf1111: $listOfByteArraysF11")


        for (byteArray1 in listOfByteArraysF11) {
            lengthOfF11 += byteArray1.size
        }
        //---------------------------------------------------------------

        for (r in fld14.indices) {
            listOfByteArraysF14.add(HexUtil.hexStr2Byte(fld14[r]))
        }
        Log.d(TAG, "list of byte arrays: $listOfByteArraysF14")


        for (byteArray1 in listOfByteArraysF14) {
            lengthOfF14 += byteArray1.size
        }
        //---------------------------------------------------------------
        for (r in fld22.indices) {
            listOfByteArraysF22.add(HexUtil.hexStr2Byte(fld22[r]))
        }
        Log.d(TAG, "list of byte arrays: $listOfByteArraysF22")


        for (byteArray1 in listOfByteArraysF22) {
            lengthOfF22 += byteArray1.size
        }
        //---------------------------------------------------------------
        for (r in fld24.indices) {
            listOfByteArraysF24.add(HexUtil.hexStr2Byte(fld24[r]))
        }
        Log.d(TAG, "list of byte arrays: $listOfByteArraysF24")


        for (byteArray1 in listOfByteArraysF24) {
            lengthOfF24 += byteArray1.size
        }
        //---------------------------------------------------------------
        for (r in fld25.indices) {
            listOfByteArraysF25.add(HexUtil.hexStr2Byte(fld25[r]))
        }
        Log.d(TAG, "list of byte arrays: $listOfByteArraysF25")


        for (byteArray1 in listOfByteArraysF25) {
            lengthOfF25 += byteArray1.size
        }
        //---------------------------------------------------------------
        for (r in fld62.indices) {
            listOfByteArraysF62.add(HexUtil.hexStr2Byte(fld62[r]))
        }
        Log.d(TAG, "list of byte arrays: $listOfByteArraysF62")


        for (byteArray1 in listOfByteArraysF62) {
            lengthOfF62 += byteArray1.size
        }
        //-------------------------------------------------------------------
        val field11 = HexUtil.hexStr2Byte(HexUtil.asc2Hex(RequestFields.Field11))
        val field41 = HexUtil.hexStr2Byte(HexUtil.asc2Hex(RequestFields.Field41))
        val field42 = HexUtil.hexStr2Byte(HexUtil.asc2Hex(RequestFields.Field42))
         var buffer:ByteBuffer

          buffer=ByteBuffer.allocate(
            lengthOfHeader+
                    lengthOfMTI+
                    lengthOfBitmap+
                    lengthOfFlen02+
                    lengthOfF02+
                    lengthOfF03 +
                    lengthOfF04 +
                    lengthOfF11 +

                    lengthOfF14 +
                    lengthOfF22 +
                    lengthOfF24 +
                    lengthOfF25+
                    field41.size +
                    field42.size+
                    lengthOfF62+field11.size)
        buffer.apply {
            for (byteArray1 in listOfByteArraysHeader) {
                put(byteArray1)
            }
            for (byteArray1 in listOfByteArraysMTI) {
                put(byteArray1)
            }
            for (byteArray1 in listOfByteArraysBMP) {
                put(byteArray1)
            }

            //for loop to put the bit map in buffer


            for (byteArray1 in listOfByteArrayslenF02) {
                put(byteArray1)
            }
            for (byteArray1 in listOfByteArraysF02) {
                put(byteArray1)
            }

            for (byteArray1 in listOfByteArraysF03) {
                put(byteArray1)
            }
            for (byteArray1 in listOfByteArraysF04) {
                put(byteArray1)
            }
            for (byteArray1 in listOfByteArraysF11) {
                put(byteArray1)
            }

            for (byteArray1 in listOfByteArraysF14) {
                put(byteArray1)
            }
            for (byteArray1 in listOfByteArraysF22) {
                put(byteArray1)
            }
            for (byteArray1 in listOfByteArraysF24) {
                put(byteArray1)
            }
            for (byteArray1 in listOfByteArraysF25) {
                put(byteArray1)
            }
            put(field41)
            put(field42)
            for (byteArray in listOfByteArraysF62) {
                put(byteArray)
            }
            put(field11)
        }


        val result:ByteArray=buffer.array()


        return result

    }
    fun packRequestFields():ByteArray {
        //ClearVariables()
        var j = 0
        var i = 0
        var fieldLength=0
        //   listOfByteArrays="".toByteArray()
        var listOfByteArraysHeader = mutableListOf<ByteArray>()
        var listOfByteArraysMTI = mutableListOf<ByteArray>()
        var listOfByteArraysBMP = mutableListOf<ByteArray>()
        var lengthOfHeader=0
        var lengthOfMTI=0

        var lengthOfBitmap = 0

        var charArrayHeader="".toCharArray()
        var charArrayMTI="".toCharArray()
        var charArrayBmp="".toCharArray()
        val listOfByteArrayslenF02 = mutableListOf<ByteArray>()
        var lengthOfFlen02=0
        var charArraylenF02="".toCharArray()

        var listOfByteArraysF02 = mutableListOf<ByteArray>()
        var lengthOfF02=0
        var charArrayF02="".toCharArray()
        //   ***************************************************************
        var listOfByteArraysF03 = mutableListOf<ByteArray>()
        var lengthOfF03=0
        var charArrayF03="".toCharArray()
        //   ***************************************************************
        //   ***************************************************************
        var listOfByteArraysF04 = mutableListOf<ByteArray>()
        var lengthOfF04=0
        var charArrayF04="".toCharArray()
        //   ***************************************************************
//   ***************************************************************
        var listOfByteArraysF11 = mutableListOf<ByteArray>()
        var lengthOfF11=0
        var charArrayF11="".toCharArray()
        //   ***************************************************************
        //   ***************************************************************
        var listOfByteArraysF12 = mutableListOf<ByteArray>()
        var lengthOfF12=0
        var charArrayF12="".toCharArray()
        //   ***************************************************************
        //   ***************************************************************
        var listOfByteArraysF13 = mutableListOf<ByteArray>()
        var lengthOfF13=0
        var charArrayF13="".toCharArray()
        //   ***************************************************************

        //   ***************************************************************
        var listOfByteArraysF14 = mutableListOf<ByteArray>()
        var lengthOfF14=0
        var charArrayF14="".toCharArray()
        //   ***************************************************************
        //   ***************************************************************
        var listOfByteArraysF22 = mutableListOf<ByteArray>()
        var lengthOfF22=0
        var charArrayF22="".toCharArray()
        //   ***************************************************************
        //   ***************************************************************
        var listOfByteArraysF24 = mutableListOf<ByteArray>()
        var lengthOfF24=0
        var charArrayF24="".toCharArray()
        //   ***************************************************************
        //   ***************************************************************
        var listOfByteArraysF25 = mutableListOf<ByteArray>()
        var lengthOfF25=0
        var charArrayF25="".toCharArray()
        //   ***************************************************************
        //   ***************************************************************
        var listOfByteArraysF35 = mutableListOf<ByteArray>()
        var lengthOfF35=0
        var charArrayF35="".toCharArray()
        //   ***************************************************************
        //   ***************************************************************
        var listOfByteArraysF37 = mutableListOf<ByteArray>()
        var lengthOfF37=0
        var charArrayF37="".toCharArray()
        //   ***************************************************************
        //   ***************************************************************
        var listOfByteArraysF38 = mutableListOf<ByteArray>()
        var lengthOfF38=0
        var charArrayF38="".toCharArray()
        //   ***************************************************************
        //   ***************************************************************
        var listOfByteArraysF39 = mutableListOf<ByteArray>()
        var lengthOfF39=0
        var charArrayF39="".toCharArray()
        //   ***************************************************************
        //   ***************************************************************
        var listOfByteArrayslenF35 = mutableListOf<ByteArray>()
        var lengthOflenF35=0
        var charArraylenF35="".toCharArray()
        //   ***************************************************************
        //   ***************************************************************
        var listOfByteArrayslenF52 = mutableListOf<ByteArray>()
        var lengthOflenF52=0
        var charArraylenF52="".toCharArray()
        //   ***************************************************************
        //   ***************************************************************
        var listOfByteArraysF52 = mutableListOf<ByteArray>()
        var lengthOfF52=0
        var charArrayF52="".toCharArray()
        //   ***************************************************************
        //   ***************************************************************
        var listOfByteArrayslenF55 = mutableListOf<ByteArray>()
        var lengthOflenF55=0
        var charArraylenF55="".toCharArray()
        //   ***************************************************************
        //   ***************************************************************
        var listOfByteArraysF55 = mutableListOf<ByteArray>()
        var lengthOfF55=0
        var charArrayF55="".toCharArray()
        //   ***************************************************************
        //   ***************************************************************
        var listOfByteArraysF60 = mutableListOf<ByteArray>()
        var lengthOfF60=0
        var charArrayF60="".toCharArray()
        //   ***************************************************************
        //   ***************************************************************
        var listOfByteArraysF62 = mutableListOf<ByteArray>()
        var lengthOfF62=0
        var charArrayF62="".toCharArray()
        //   ***************************************************************
        //   ***************************************************************
        var listOfByteArraysF63 = mutableListOf<ByteArray>()
        var lengthOfF63=0
        var charArrayF63="".toCharArray()
        //   ***************************************************************


        Log.d("tag","packet12 "+RequestFields.Field11)
        //**************************************************************
        fieldLength = RequestFields.Header.length / 2
        Log.d("TransData", "headertry.........: ${RequestFields.Header}")

        val  header= Array(fieldLength) { "" }
        charArrayHeader = RequestFields.Header.toCharArray()

        j=0
        for (i in 0 until fieldLength * 2 step 2) {
            header[j] = "${charArrayHeader[i]}${charArrayHeader[i + 1]}"
            j++
        }

        //************************************************ end header/
        //**************************************************************

        fieldLength = RequestFields.MTI.length / 2
        Log.d("TransData", "mti.........: ${RequestFields.MTI}")

        val  mti= Array(fieldLength) { "" }

        charArrayMTI = RequestFields.MTI.toCharArray()

        j=0
        for (i in 0 until fieldLength * 2 step 2) {
            mti[j] = "${charArrayMTI[i]}${charArrayMTI[i + 1]}"
            j++
        }
        //************************************************ end mti/
        val bitmaplength = RequestFields.primaryBitmap.length / 2
        Log.d("TransData", "bitmaptry.........: ${RequestFields.primaryBitmap}")

        val primarybitmap = Array(bitmaplength) { "" }
        charArrayBmp = RequestFields.primaryBitmap.toCharArray()

        j=0
        for (i in 0 until bitmaplength * 2 step 2) {
            primarybitmap[j] = "${charArrayBmp[i]}${charArrayBmp[i + 1]}"
            j++
        }
        //************************************************ end bmp/

//**************************************************************



        //**************************************************************
        val Fld02lenint=RequestFields.Field02.length
        if(RequestFields.Field02.length % 2 !=0)
        {
            fieldLength = (Fld02lenint+1).toString().length / 2
        }
        else {
            fieldLength = Fld02lenint.toString().length / 2
        }
        Log.d("TransData", "mti.........: " + Fld02lenint)

        val  Fld02lenstr= Array(fieldLength) { "" }

        charArraylenF02 = Fld02lenint.toString().toCharArray()

        j=0
        for (i in 0 until fieldLength * 2 step 2) {
            Fld02lenstr[j] = "${charArraylenF02[i]}${charArraylenF02[i + 1]}"
            j++
        }





        fieldLength = RequestFields.Field02.length / 2
        Log.d("TransData", "headertry.........: ${RequestFields.Field02}")

        val  fld02= Array(fieldLength) { "" }
        charArrayF02 = RequestFields.Field02.toCharArray()

        j=0
        for (i in 0 until fieldLength * 2 step 2) {
            fld02[j] = "${charArrayF02[i]}${charArrayF02[i + 1]}"
            j++
        }

        //************************************************ end field02/
        //**************************************************************
        fieldLength = RequestFields.Field03.length / 2
        Log.d("TransData", "headertry.........: ${RequestFields.Field03}")

        val  fld03= Array(fieldLength) { "" }
        charArrayF03 = RequestFields.Field03.toCharArray()

        j=0
        for (i in 0 until fieldLength * 2 step 2) {
            fld03[j] = "${charArrayF03[i]}${charArrayF03[i + 1]}"
            j++
        }

        //************************************************ end field03/
        //**************************************************************
        if(txnType==TxnType.M_BALANCE_INQUIRY){
            RequestFields.Field04="0000000000000"
        }
        fieldLength = RequestFields.Field04.length / 2
        Log.d("TransData", "headertry.........: ${RequestFields.Field04}")

        val  fld04= Array(fieldLength) { "" }
        charArrayF04 = RequestFields.Field04.toCharArray()

        j=0
        for (i in 0 until fieldLength * 2 step 2) {
            fld04[j] = "${charArrayF04[i]}${charArrayF04[i + 1]}"
            j++
        }

        //************************************************ end field04/


        //**************************************************************
        fieldLength = RequestFields.Field11.length / 2
        Log.d("TransData", "headertryF11.........: ${RequestFields.Field11}")

        val  fld11= Array(fieldLength) { "" }
        charArrayF11 = RequestFields.Field11.toCharArray()

        j=0
        for (i in 0 until fieldLength * 2 step 2) {
            fld11[j] = "${charArrayF11[i]}${charArrayF11[i + 1]}"
            j++
        }

        //************************************************ end field11/
        //**************************************************************
        fieldLength = RequestFields.Field12.length / 2
        Log.d("TransData", "headertry.........: ${RequestFields.Field12}")

        val  fld12= Array(fieldLength) { "" }
        charArrayF12 = RequestFields.Field12.toCharArray()

        j=0
        for (i in 0 until fieldLength * 2 step 2) {
            fld12[j] = "${charArrayF12[i]}${charArrayF12[i + 1]}"
            j++
        }

        //************************************************ end field12/
        //**************************************************************
        fieldLength = RequestFields.Field13.length / 2
        Log.d("TransData", "headertry.........: ${RequestFields.Field13}")

        val  fld13= Array(fieldLength) { "" }
        charArrayF13 = RequestFields.Field13.toCharArray()

        j=0
        for (i in 0 until fieldLength * 2 step 2) {
            fld13[j] = "${charArrayF13[i]}${charArrayF13[i + 1]}"
            j++
        }

        //************************************************ end field13/

        //**************************************************************
        fieldLength = RequestFields.Field14.length / 2
        Log.d("TransData", "headertry.........: ${RequestFields.Field14}")

        val  fld14= Array(fieldLength) { "" }
        charArrayF14 = RequestFields.Field14.toCharArray()

        j=0
        for (i in 0 until fieldLength * 2 step 2) {
            fld14[j] = "${charArrayF14[i]}${charArrayF14[i + 1]}"
            j++
        }

        //************************************************ end field14/
        //**************************************************************
        fieldLength = RequestFields.Field22.length / 2
        Log.d("TransData", "headertry.........: ${RequestFields.Field22}")

        val  fld22= Array(fieldLength) { "" }
        charArrayF22 = RequestFields.Field22.toCharArray()

        j=0
        for (i in 0 until fieldLength * 2 step 2) {
            fld22[j] = "${charArrayF22[i]}${charArrayF22[i + 1]}"
            j++
        }

        //************************************************ end field22/

        //**************************************************************
        fieldLength = RequestFields.Field24.length / 2
        Log.d("TransData", "headertry.........: ${RequestFields.Field24}")

        val  fld24= Array(fieldLength) { "" }
        charArrayF24 = RequestFields.Field24.toCharArray()

        j=0
        for (i in 0 until fieldLength * 2 step 2) {
            fld24[j] = "${charArrayF24[i]}${charArrayF24[i + 1]}"
            j++
        }

        //************************************************ end field24/
        //**************************************************************
        fieldLength = RequestFields.Field25.length / 2
        Log.d("TransData", "headertry.........: ${RequestFields.Field25}")

        val  fld25= Array(fieldLength) { "" }
        charArrayF25 = RequestFields.Field25.toCharArray()

        j=0
        for (i in 0 until fieldLength * 2 step 2) {
            fld25[j] = "${charArrayF25[i]}${charArrayF25[i + 1]}"
            j++
        }

        //************************************************ end field25/
        //**************************************************************
        val Fld35lenint=RequestFields.Field35.length
        if(RequestFields.Field35.length % 2 !=0)
        {
            fieldLength = (Fld35lenint+1).toString().length / 2
            RequestFields.Field35="0"+RequestFields.Field35

        }
        else {
            fieldLength = Fld35lenint.toString().length / 2

        }
        Log.d("TransData", "mti.........: " + Fld35lenint)

        val fld35len  = Array(fieldLength) { "" }

        charArraylenF35 = Fld35lenint.toString().toCharArray()

        j=0
        for (i in 0 until fieldLength * 2 step 2) {
            fld35len[j] = "${charArraylenF35[i]}${charArraylenF35[i + 1]}"
            j++
        }

        //**************************************************************
        fieldLength = RequestFields.Field35.length / 2
        Log.d("TransData", "headertry.........: ${RequestFields.Field35}")

        val  fld35= Array(fieldLength) { "" }
        charArrayF35 = RequestFields.Field35.toCharArray()

        j=0
        for (i in 0 until fieldLength * 2 step 2) {
            fld35[j] = "${charArrayF35[i]}${charArrayF35[i + 1]}"
            j++
        }

        //************************************************ end field35/

        //**************************************************************
        fieldLength = RequestFields.Field37.length / 2
        Log.d("TransData", "headertry.........: ${RequestFields.Field37}")

        val  fld37= Array(fieldLength) { "" }
        charArrayF37 = RequestFields.Field37.toCharArray()

        j=0
        for (i in 0 until fieldLength * 2 step 2) {
            fld37[j] = "${charArrayF37[i]}${charArrayF37[i + 1]}"
            j++
        }

        //************************************************ end field37/
        //**************************************************************
        fieldLength = RequestFields.Field38.length / 2
        Log.d("TransData", "headertry.........: ${RequestFields.Field38}")

        val  fld38= Array(fieldLength) { "" }
        charArrayF38 = RequestFields.Field38.toCharArray()

        j=0
        for (i in 0 until fieldLength * 2 step 2) {
            fld38[j] = "${charArrayF38[i]}${charArrayF38[i + 1]}"
            j++
        }

        //************************************************ end field38/
        //**************************************************************
        fieldLength = RequestFields.Field39.length / 2
        Log.d("TransData", "headertry.........: ${RequestFields.Field39}")

        val  fld39= Array(fieldLength) { "" }
        charArrayF39 = RequestFields.Field39.toCharArray()

        j=0
        for (i in 0 until fieldLength * 2 step 2) {
            fld39[j] = "${charArrayF39[i]}${charArrayF39[i + 1]}"
            j++
        }

        //************************************************ end field39/

        //listOfByteArrays = mutableListOf<ByteArray>()
        for (r in header.indices) {
            listOfByteArraysHeader.add(HexUtil.hexStr2Byte(header[r]))
        }
        Log.d(TAG, "list of byte arrays: $listOfByteArraysHeader")


        for (byteArray1 in listOfByteArraysHeader) {
            lengthOfHeader += byteArray1.size
        }
//*************************************************
        for (r in mti.indices) {
            listOfByteArraysMTI.add(HexUtil.hexStr2Byte(mti[r]))
        }
        Log.d(TAG, "list of byte arrays: $listOfByteArraysMTI")


        for (byteArray1 in listOfByteArraysMTI) {
            lengthOfMTI += byteArray1.size
        }
        //val listOfByteArrays1 = mutableListOf<ByteArray>()
        for (r in primarybitmap.indices) {
            listOfByteArraysBMP.add(HexUtil.hexStr2Byte(primarybitmap[r]))
        }
        Log.d(TAG, "list of byte arrays1: $listOfByteArraysBMP")
//*************************************************
        lengthOfBitmap = 0
        for (byteArray1 in listOfByteArraysBMP) {
            lengthOfBitmap += byteArray1.size
        }
        //********************************************************
        //********************************************************
        for (r in Fld02lenstr.indices) {
            listOfByteArrayslenF02.add(HexUtil.hexStr2Byte(Fld02lenstr[r]))
        }
        Log.d(TAG, "list of byte arrayslengf02: $listOfByteArrayslenF02")


        for (byteArray1 in listOfByteArrayslenF02) {
            lengthOfFlen02 += byteArray1.size
        }
        Log.d(TAG,"lenf02"+lengthOfFlen02)
        for (r in fld02.indices) {
            listOfByteArraysF02.add(HexUtil.hexStr2Byte(fld02[r]))
        }
        Log.d(TAG, "list of byte arraysF02: $listOfByteArraysF02")


        for (byteArray1 in listOfByteArraysF02) {
            lengthOfF02 += byteArray1.size
        }
        Log.d(TAG,"lenf02"+lengthOfF02)


        //********************************************************
        for (r in fld03.indices) {
            listOfByteArraysF03.add(HexUtil.hexStr2Byte(fld03[r]))
        }
        Log.d(TAG, "list of byte arrays: $listOfByteArraysF03")


        for (byteArray1 in listOfByteArraysF03) {
            lengthOfF03 += byteArray1.size
        }

        //********************************************************
        for (r in fld04.indices) {
            listOfByteArraysF04.add(HexUtil.hexStr2Byte(fld04[r]))
        }
        Log.d(TAG, "list of byte arrays: $listOfByteArraysF04")


        for (byteArray1 in listOfByteArraysF04) {
            lengthOfF04 += byteArray1.size
        }

        //********************************************************
        for (r in fld11.indices) {
            listOfByteArraysF11.add(HexUtil.hexStr2Byte(fld11[r]))
        }
        Log.d(TAG, "list of byte arraysf1111: $listOfByteArraysF11")


        for (byteArray1 in listOfByteArraysF11) {
            lengthOfF11 += byteArray1.size
        }
        //********************************************************
        //********************************************************
        for (r in fld12.indices) {
            listOfByteArraysF12.add(HexUtil.hexStr2Byte(fld12[r]))
        }
        Log.d(TAG, "list of byte arrays: $listOfByteArraysF12")


        for (byteArray1 in listOfByteArraysF12) {
            lengthOfF12 += byteArray1.size
        }
        //********************************************************
        //********************************************************
        for (r in fld13.indices) {
            listOfByteArraysF13.add(HexUtil.hexStr2Byte(fld13[r]))
        }
        Log.d(TAG, "list of byte arrays: $listOfByteArraysF13")


        for (byteArray1 in listOfByteArraysF13) {
            lengthOfF13 += byteArray1.size
        }
        //********************************************************
        for (r in fld14.indices) {
            listOfByteArraysF14.add(HexUtil.hexStr2Byte(fld14[r]))
        }
        Log.d(TAG, "list of byte arrays: $listOfByteArraysF14")


        for (byteArray1 in listOfByteArraysF14) {
            lengthOfF14 += byteArray1.size
        }
        //********************************************************
        for (r in fld22.indices) {
            listOfByteArraysF22.add(HexUtil.hexStr2Byte(fld22[r]))
        }
        Log.d(TAG, "list of byte arrays: $listOfByteArraysF22")


        for (byteArray1 in listOfByteArraysF22) {
            lengthOfF22 += byteArray1.size
        }
        //********************************************************
        for (r in fld24.indices) {
            listOfByteArraysF24.add(HexUtil.hexStr2Byte(fld24[r]))
        }
        Log.d(TAG, "list of byte arrays: $listOfByteArraysF24")


        for (byteArray1 in listOfByteArraysF24) {
            lengthOfF24 += byteArray1.size
        }
        //********************************************************
        for (r in fld25.indices) {
            listOfByteArraysF25.add(HexUtil.hexStr2Byte(fld25[r]))
        }
        Log.d(TAG, "list of byte arrays: $listOfByteArraysF25")


        for (byteArray1 in listOfByteArraysF25) {
            lengthOfF25 += byteArray1.size
        }
        //********************************************************
        for (r in fld35len.indices) {
            listOfByteArrayslenF35.add(HexUtil.hexStr2Byte(fld35len[r]))
        }
        Log.d(TAG, "list of byte arrays: $listOfByteArrayslenF35")


        for (byteArray1 in listOfByteArrayslenF35) {
            lengthOflenF35 += byteArray1.size
        }
        //********************************************************
        for (r in fld35.indices) {
            listOfByteArraysF35.add(HexUtil.hexStr2Byte(fld35[r]))
        }
        Log.d(TAG, "list of byte arrays: $listOfByteArraysF35")


        for (byteArray1 in listOfByteArraysF35) {
            lengthOfF35 += byteArray1.size

        }

        //********************************************************
        for (r in fld37.indices) {
            listOfByteArraysF37.add(HexUtil.hexStr2Byte(fld37[r]))
        }
        Log.d(TAG, "list of byte arrays: $listOfByteArraysF37")


        for (byteArray1 in listOfByteArraysF37) {
            lengthOfF37 += byteArray1.size
        }
        //********************************************************
        //********************************************************
        for (r in fld38.indices) {
            listOfByteArraysF38.add(HexUtil.hexStr2Byte(fld38[r]))
        }
        Log.d(TAG, "list of byte arrays: $listOfByteArraysF38")


        for (byteArray1 in listOfByteArraysF38) {
            lengthOfF38 += byteArray1.size
        }
        //********************************************************
        //********************************************************
        for (r in fld39.indices) {
            listOfByteArraysF39.add(HexUtil.hexStr2Byte(fld39[r]))
        }
        Log.d(TAG, "list of byte arrays: $listOfByteArraysF39")


        for (byteArray1 in listOfByteArraysF39) {
            lengthOfF39 += byteArray1.size
        }
        //********************************************************
        //*****************************************************************start of Field 60/
        //***********************************************************************
        fieldLength = RequestFields.Field60.length / 2
        Log.d("TransData", "headertry 60.........: ${RequestFields.Field60}")

        val  fld60= Array(fieldLength) { "" }
        charArrayF60 = RequestFields.Field60.toCharArray()

        j=0
        for (i in 0 until fieldLength * 2 step 2) {
            fld60[j] = "${charArrayF60[i]}${charArrayF60[i + 1]}"
            j++
        }


        //********************************************************
        for (r in fld60.indices) {
            listOfByteArraysF60.add(HexUtil.hexStr2Byte(fld60[r]))
        }
        Log.d(TAG, "list of byte arraysF60: $listOfByteArraysF60")


        for (byteArray1 in listOfByteArraysF60) {
            lengthOfF60 += byteArray1.size
        }

        //***********************************************************************
        fieldLength = RequestFields.Field62.length / 2
        Log.d("TransData", "headertry.........: ${RequestFields.Field62}")

        val  fld62= Array(fieldLength) { "" }
        charArrayF62 = RequestFields.Field62.toCharArray()

        j=0
        for (i in 0 until fieldLength * 2 step 2) {
            fld62[j] = "${charArrayF62[i]}${charArrayF62[i + 1]}"
            j++
        }


        //********************************************************
        for (r in fld62.indices) {
            listOfByteArraysF62.add(HexUtil.hexStr2Byte(fld62[r]))
        }
        Log.d(TAG, "list of byte arrays: $listOfByteArraysF62")


        for (byteArray1 in listOfByteArraysF62) {
            lengthOfF62 += byteArray1.size
        }
        //***********************************************************************
        fieldLength = RequestFields.Field63.length / 2
        Log.d("TransData", "headertry.........: ${RequestFields.Field63}")

        val  fld63= Array(fieldLength) { "" }
        charArrayF63 = RequestFields.Field63.toCharArray()

        j=0
        for (i in 0 until fieldLength * 2 step 2) {
            fld63[j] = "${charArrayF63[i]}${charArrayF63[i + 1]}"
            j++
        }


        //********************************************************
        for (r in fld63.indices) {
            listOfByteArraysF63.add(HexUtil.hexStr2Byte(fld63[r]))
        }
        Log.d(TAG, "list of byte arrays: $listOfByteArraysF63")


        for (byteArray1 in listOfByteArraysF63) {
            lengthOfF63 += byteArray1.size
        }
        val f60EndValue4Settlement=HexUtil.hexStr2Byte(HexUtil.asc2Hex("000020"))
        val f63EndValue4Settlement=HexUtil.hexStr2Byte(HexUtil.asc2Hex(TransData.RequestFields.endValue4F63))
        val field04 = HexUtil.hexStr2Byte(HexUtil.asc2Hex(RequestFields.Field04))
        val field11 = HexUtil.hexStr2Byte(HexUtil.asc2Hex(RequestFields.Field11))
        Log.d(TAG, "f11 $field11")

        //field 02 and its length


        val field41 = HexUtil.hexStr2Byte(HexUtil.asc2Hex(RequestFields.Field41))
        val field42 = HexUtil.hexStr2Byte(HexUtil.asc2Hex(RequestFields.Field42))
        val field37 = HexUtil.hexStr2Byte(HexUtil.asc2Hex(RequestFields.Field37))
        val field38 = HexUtil.hexStr2Byte(HexUtil.asc2Hex(RequestFields.Field38))
        val field39 = HexUtil.hexStr2Byte(HexUtil.asc2Hex(RequestFields.Field39))
        var  buffer: ByteBuffer? =null
        var  bufferForManual: ByteBuffer? =null

        if(txnType.equals(TxnType.M_PURCHASE)){
            Log.d(TAG,"txnmenutype:${txnType}")
            buffer=ByteBuffer.allocate(
                lengthOfHeader+
                        lengthOfMTI+
                        lengthOfBitmap+
                        lengthOfFlen02+
                        lengthOfF02+
                        lengthOfF03+
                        lengthOfF04+
                        lengthOfF11+
                        lengthOfF14+
                        lengthOfF22+
                        lengthOfF24+
                        lengthOfF25+
                        field41.size+
                        field42.size+
                        lengthOfF62+field11.size)
            buffer.apply {
                for (byteArray1 in listOfByteArraysHeader) {
                    put(byteArray1)
                }
                for (byteArray1 in listOfByteArraysMTI) {
                    put(byteArray1)
                }
                for (byteArray1 in listOfByteArraysBMP) {
                    put(byteArray1)
                  }
                for (byteArray1 in listOfByteArrayslenF02) {
                    put(byteArray1)
                }
                for (byteArray1 in listOfByteArraysF02) {
                    put(byteArray1)
                }

                for (byteArray1 in listOfByteArraysF03) {
                    put(byteArray1)
                      }
                for (byteArray1 in listOfByteArraysF04) {
                    put(byteArray1)
                      }
                for (byteArray1 in listOfByteArraysF11) {
                    put(byteArray1)
                   }
                for (byteArray1 in listOfByteArraysF14) {
                    put(byteArray1)
                   }
                for(byteArray1 in listOfByteArraysF22) {
                    put(byteArray1)
                   }
                for(byteArray1 in listOfByteArraysF24) {
                    put(byteArray1)
                   }
                for(byteArray1 in listOfByteArraysF25) {
                    put(byteArray1)
                     }
                put(field41)
                put(field42)
                for (byteArray1 in listOfByteArraysF62) {
                    put(byteArray1)
                  }
                put(field11)


            }

        }else if(txnType.equals(TxnType.M_BALANCE_INQUIRY)){
            Log.d(TAG,"txnmenutype:${txnType}")
            buffer=ByteBuffer.allocate(
                lengthOfHeader+
                        lengthOfMTI+
                        lengthOfBitmap+
                        lengthOfFlen02+
                        lengthOfF02+
                        lengthOfF03+
                        lengthOfF11+
                        lengthOfF14+
                        lengthOfF22+
                        lengthOfF24+
                        lengthOfF25+
                        field41.size+
                        field42.size+
                        lengthOfF62+field11.size)
            buffer.apply {
                for (byteArray1 in listOfByteArraysHeader) {
                    put(byteArray1)
                }
                for (byteArray1 in listOfByteArraysMTI) {
                    put(byteArray1)
                }
                for (byteArray1 in listOfByteArraysBMP) {
                    put(byteArray1)
                }
                for (byteArray1 in listOfByteArrayslenF02) {
                    put(byteArray1)
                }
                for (byteArray1 in listOfByteArraysF02) {
                    put(byteArray1)
                }

                for (byteArray1 in listOfByteArraysF03) {
                    put(byteArray1)
                }
                for (byteArray1 in listOfByteArraysF11) {
                    put(byteArray1)
                }
                for (byteArray1 in listOfByteArraysF14) {
                    put(byteArray1)
                }
                for(byteArray1 in listOfByteArraysF22) {
                    put(byteArray1)
                }
                for(byteArray1 in listOfByteArraysF24) {
                    put(byteArray1)
                }
                for(byteArray1 in listOfByteArraysF25) {
                    put(byteArray1)
                }
                put(field41)
                put(field42)
                for (byteArray1 in listOfByteArraysF62) {
                    put(byteArray1)
                }
                put(field11)


            }

        }
        else if(txnType.equals(TxnType.KEY_DOWNLOAD)){

           buffer=ByteBuffer.allocate(lengthOfHeader+
                   lengthOfMTI+
                   lengthOfBitmap+
                   lengthOfF03+
                   lengthOfF11+
                   lengthOfF24+
                   field41.size +
                   field42.size+
                   lengthOfF62+field11.size
           )
            buffer.apply {
                for (byteArray1 in listOfByteArraysHeader) {
                    put(byteArray1)
                }
                for (byteArray1 in listOfByteArraysMTI) {
                    put(byteArray1)
                }
                for (byteArray1 in listOfByteArraysBMP) {
                    put(byteArray1)
                }
                for (byteArray1 in listOfByteArraysF03) {
                    put(byteArray1)
                }
                for (byteArray1 in listOfByteArraysF11) {
                    put(byteArray1)
                }
                for (byteArray1 in listOfByteArraysF24) {
                    put(byteArray1)
                }
                put(field41)
                put(field42)
                for (byteArray in listOfByteArraysF62) {
                    put(byteArray)
                }
                Log.d(TAG, "f11P $field11")

                put(field11)


            }
        }else if (txnType.equals(TxnType.SETTLEMENT)){
            buffer=ByteBuffer.allocate(
                lengthOfHeader+
                    lengthOfMTI+
                    lengthOfBitmap+
                    lengthOfF03+
                    lengthOfF11+
                    lengthOfF24+
                    field41.size +
                    field42.size+
                    lengthOfF60+
                    f60EndValue4Settlement.size+
                    lengthOfF62+
                    lengthOfF63+
                        f63EndValue4Settlement.size
            )
            buffer.apply {
                for (byteArray1 in listOfByteArraysHeader) {
                    put(byteArray1)
                }
                for (byteArray1 in listOfByteArraysMTI) {
                    put(byteArray1)
                }
                for (byteArray1 in listOfByteArraysBMP) {
                    put(byteArray1)
                }
                for (byteArray1 in listOfByteArraysF03) {
                    put(byteArray1)
                }
                for (byteArray1 in listOfByteArraysF11) {
                    put(byteArray1)
                }
                for (byteArray1 in listOfByteArraysF24) {
                    put(byteArray1)
                }
                put(field41)
                put(field42)
                for (byteArray in listOfByteArraysF60) {
                    put(byteArray)
                }
                put(f60EndValue4Settlement)


                for (byteArray in listOfByteArraysF62) {
                    put(byteArray)
                }
                Log.d(TAG, "f11P $field11")
                for (byteArray in listOfByteArraysF63) {
                    put(byteArray)
                }
                put(f63EndValue4Settlement)




        }
        } else if(txnType.equals(TxnType.REVERSAL)) {

            buffer = ByteBuffer.allocate(
                lengthOfHeader +
                        lengthOfMTI +
                        lengthOfBitmap +
                        lengthOfFlen02 +
                        lengthOfF02 +
                        lengthOfF03 +
                        lengthOfF04 +
                        lengthOfF11 +
                        lengthOfF12 +
                        lengthOfF13 +
                        lengthOfF14 +
                        lengthOfF22 +
                        lengthOfF24 +
                        lengthOfF25 +
                        lengthOflenF35 +
                        lengthOfF35 +
                        field37.size +
                        field38.size +
                        field39.size +
                        field41.size +
                        field42.size +
                        lengthOfF60 + field04.size +
                        lengthOfF62 + field11.size
            )
            buffer.apply {
                for (byteArray1 in listOfByteArraysHeader) {
                    put(byteArray1)
                }
                for (byteArray1 in listOfByteArraysMTI) {
                    put(byteArray1)
                }
                for (byteArray1 in listOfByteArraysBMP) {
                    put(byteArray1)
                }

                //for loop to put the bit map in buffer


                for (byteArray1 in listOfByteArrayslenF02) {
                    put(byteArray1)
                }
                for (byteArray1 in listOfByteArraysF02) {
                    put(byteArray1)
                }

                for (byteArray1 in listOfByteArraysF03) {
                    put(byteArray1)
                }
                for (byteArray1 in listOfByteArraysF04) {
                    put(byteArray1)
                }
                for (byteArray1 in listOfByteArraysF11) {
                    put(byteArray1)
                }
                for (byteArray1 in listOfByteArraysF12) {
                    put(byteArray1)
                }
                for (byteArray1 in listOfByteArraysF13) {
                    put(byteArray1)
                }
                for (byteArray1 in listOfByteArraysF14) {
                    put(byteArray1)
                }
                for (byteArray1 in listOfByteArraysF22) {
                    put(byteArray1)
                }
                for (byteArray1 in listOfByteArraysF24) {
                    put(byteArray1)
                }
                for (byteArray1 in listOfByteArraysF25) {
                    put(byteArray1)
                }

                for (byteArray1 in listOfByteArrayslenF35) {
                    put(byteArray1)
                }
                for (byteArray1 in listOfByteArraysF35) {
                    put(byteArray1)
                }
                put(field37)
                put(field38)
                put(field39)


                put(field41)
                put(field42)

                for (byteArray in listOfByteArraysF60) {
                    put(byteArray)
                }
                put(field04)


                for (byteArray in listOfByteArraysF62) {
                    put(byteArray)
                }
                put(field11)
            }

        }else if(txnType.equals(TxnType.M_REVERSAL)){

            buffer = ByteBuffer.allocate(
                lengthOfHeader+
                        lengthOfMTI+
                        lengthOfBitmap+
                        lengthOfFlen02+
                        lengthOfF02+
                        lengthOfF03 +
                        lengthOfF04 +
                        lengthOfF11 +
                        lengthOfF12 +
                        lengthOfF13 +
                        lengthOfF14 +
                        lengthOfF22 +
                        lengthOfF24 +
                        lengthOfF25+
                        field37.size +
                        field38.size +
                        field39.size +
                        field41.size +
                        field42.size+
                        lengthOfF60+field04.size+
                        lengthOfF62+field11.size)
            buffer.apply {
                for (byteArray1 in listOfByteArraysHeader) {
                    put(byteArray1)
                }
                for (byteArray1 in listOfByteArraysMTI) {
                    put(byteArray1)
                }
                for (byteArray1 in listOfByteArraysBMP) {
                    put(byteArray1)
                }

                //for loop to put the bit map in buffer


                for (byteArray1 in listOfByteArrayslenF02) {
                    put(byteArray1)
                }
                for (byteArray1 in listOfByteArraysF02) {
                    put(byteArray1)
                }

                for (byteArray1 in listOfByteArraysF03) {
                    put(byteArray1)
                }
                for (byteArray1 in listOfByteArraysF04) {
                    put(byteArray1)
                }
                for (byteArray1 in listOfByteArraysF11) {
                    put(byteArray1)
                }
                for (byteArray1 in listOfByteArraysF12) {
                    put(byteArray1)
                }
                for (byteArray1 in listOfByteArraysF13) {
                    put(byteArray1)
                }
                for (byteArray1 in listOfByteArraysF14) {
                    put(byteArray1)
                }
                for (byteArray1 in listOfByteArraysF22) {
                    put(byteArray1)
                }
                for (byteArray1 in listOfByteArraysF24) {
                    put(byteArray1)
                }
                for (byteArray1 in listOfByteArraysF25) {
                    put(byteArray1)
                }


                put(field37)
                put(field38)
                put(field39)


                put(field41)
                put(field42)

                for (byteArray in listOfByteArraysF60) {
                    put(byteArray)
                }
                put(field04)


                for (byteArray in listOfByteArraysF62) {
                    put(byteArray)
                }
                put(field11)
            }
        } else if(txnType.equals(TxnType.PURCHASE)||txnType.equals(TxnType.PRE_AUTH)){
             buffer = ByteBuffer.allocate(
                 lengthOfHeader+
                         lengthOfMTI+
                         lengthOfBitmap +
                         lengthOfFlen02+
                         lengthOfF02+
                         lengthOfF03 +
                         lengthOfF04 +
                         lengthOfF11 +
                         lengthOfF14 +
                         lengthOfF22 +
                         lengthOfF24 +
                         lengthOfF25+
                         lengthOflenF35+
                         lengthOfF35 +
                         field41.size +
                         field42.size

             )


             buffer?.apply {
                 for (byteArray1 in listOfByteArraysHeader) {
                     put(byteArray1)
                 }
                 for (byteArray1 in listOfByteArraysMTI) {
                     put(byteArray1)
                 }
                 for (byteArray1 in listOfByteArraysBMP) {
                     put(byteArray1)
                 }

                 //for loop to put the bit map in buffer


                 for (byteArray1 in listOfByteArrayslenF02) {
                     put(byteArray1)
                 }
                 for (byteArray1 in listOfByteArraysF02) {
                     put(byteArray1)
                 }

                 for (byteArray1 in listOfByteArraysF03) {
                     put(byteArray1)
                 }
                 for (byteArray1 in listOfByteArraysF04) {
                     put(byteArray1)
                 }
                 for (byteArray1 in listOfByteArraysF11) {
                     put(byteArray1)
                 }
                 for (byteArray1 in listOfByteArraysF14) {
                     put(byteArray1)
                 }
                 for (byteArray1 in listOfByteArraysF22) {
                     put(byteArray1)
                 }
                 for (byteArray1 in listOfByteArraysF24) {
                     put(byteArray1)
                 }
                 for (byteArray1 in listOfByteArraysF25) {
                     put(byteArray1)
                 }

                 for (byteArray1 in listOfByteArrayslenF35) {
                     put(byteArray1)
                 }
                 for (byteArray1 in listOfByteArraysF35) {
                     put(byteArray1)
                 }


                 put(field41)
                 put(field42)

             }
         }else if(txnType.equals(TxnType.BALANCE_INQUIRY)){
            buffer = ByteBuffer.allocate(
                lengthOfHeader+
                        lengthOfMTI+
                        lengthOfBitmap +
                        lengthOfFlen02+
                        lengthOfF02+
                        lengthOfF03 +
                        lengthOfF11 +
                        lengthOfF14 +
                        lengthOfF22 +
                        lengthOfF24 +
                        lengthOfF25+
                        lengthOflenF35+
                        lengthOfF35 +
                        field41.size +
                        field42.size

            )


            buffer?.apply {
                for (byteArray1 in listOfByteArraysHeader) {
                    put(byteArray1)
                }
                for (byteArray1 in listOfByteArraysMTI) {
                    put(byteArray1)
                }
                for (byteArray1 in listOfByteArraysBMP) {
                    put(byteArray1)
                }

                //for loop to put the bit map in buffer


                for (byteArray1 in listOfByteArrayslenF02) {
                    put(byteArray1)
                }
                for (byteArray1 in listOfByteArraysF02) {
                    put(byteArray1)
                }

                for (byteArray1 in listOfByteArraysF03) {
                    put(byteArray1)
                }
                for (byteArray1 in listOfByteArraysF11) {
                    put(byteArray1)
                }
                for (byteArray1 in listOfByteArraysF14) {
                    put(byteArray1)
                }
                for (byteArray1 in listOfByteArraysF22) {
                    put(byteArray1)
                }
                for (byteArray1 in listOfByteArraysF24) {
                    put(byteArray1)
                }
                for (byteArray1 in listOfByteArraysF25) {
                    put(byteArray1)
                }

                for (byteArray1 in listOfByteArrayslenF35) {
                    put(byteArray1)
                }
                for (byteArray1 in listOfByteArraysF35) {
                    put(byteArray1)
                }


                put(field41)
                put(field42)

            }
        }else if(txnType.equals(TxnType.REFUND)||txnType.equals(TxnType.PRE_AUTH_COMPLETION)) {
                buffer = ByteBuffer.allocate(
                    lengthOfHeader+
                            lengthOfMTI+
                            lengthOfBitmap +
                            lengthOfFlen02+
                            lengthOfF02+
                            lengthOfF03 +
                            lengthOfF04 +
                            lengthOfF11 +
                            lengthOfF12+
                            lengthOfF13+
                            lengthOfF14 +
                            lengthOfF22 +
                            lengthOfF24 +
                            lengthOfF25+
                            lengthOflenF35+
                            lengthOfF35 +
                            field37.size +
                            field38.size +
                            field39.size +
                            field41.size +
                            field42.size

                )

            buffer?.apply {
                    for (byteArray1 in listOfByteArraysHeader) {
                        put(byteArray1)
                    }
                    for (byteArray1 in listOfByteArraysMTI) {
                        put(byteArray1)
                    }
                    for (byteArray1 in listOfByteArraysBMP) {
                        put(byteArray1)
                    }

                    //for loop to put the bit map in buffer


                    for (byteArray1 in listOfByteArrayslenF02) {
                        put(byteArray1)
                    }
                    for (byteArray1 in listOfByteArraysF02) {
                        put(byteArray1)
                    }

                    for (byteArray1 in listOfByteArraysF03) {
                        put(byteArray1)
                    }
                    for (byteArray1 in listOfByteArraysF04) {
                        put(byteArray1)
                    }
                    for (byteArray1 in listOfByteArraysF11) {
                        put(byteArray1)
                    }
                    for (byteArray1 in listOfByteArraysF12) {
                        put(byteArray1)
                    }
                    for (byteArray1 in listOfByteArraysF13) {
                        put(byteArray1)
                    }
                    for (byteArray1 in listOfByteArraysF14) {
                        put(byteArray1)
                    }
                    for (byteArray1 in listOfByteArraysF22) {
                        put(byteArray1)
                    }
                    for (byteArray1 in listOfByteArraysF24) {
                        put(byteArray1)
                    }
                    for (byteArray1 in listOfByteArraysF25) {
                        put(byteArray1)
                    }

                    for (byteArray1 in listOfByteArrayslenF35) {
                        put(byteArray1)
                    }
                    for (byteArray1 in listOfByteArraysF35) {
                        put(byteArray1)
                    }
                put(field37)
                put(field38)
                put(field39)


                    put(field41)
                    put(field42)

                }
            }

        //**************************************************************

         //********************************************************

        //**************************************************************
        fieldLength = RequestFields.Field52.length / 2
        Log.d("TransData", "headertry52.........: ${RequestFields.Field52}")

        val  fld52= Array(fieldLength) { "" }
        charArrayF52 = RequestFields.Field52.toCharArray()

        j=0
        for (i in 0 until fieldLength * 2 step 2) {
            fld52[j] = "${charArrayF52[i]}${charArrayF52[i + 1]}"
            j++
        }

        //************************************************ end field52/
        //********************************************************
        for (r in fld52.indices) {
            listOfByteArraysF52.add(HexUtil.hexStr2Byte(fld52[r]))
        }
        Log.d(TAG, "list of byte arrays 52: $listOfByteArraysF52")


        for (byteArray1 in listOfByteArraysF52) {
            lengthOfF52 += byteArray1.size
        }
        //pinblock for online pin

        val bufferForF52 = ByteBuffer.allocate(lengthOfF52 )
        bufferForF52.apply {
           // put(lengthOfF52.toString().toByteArray())
            for (byteArray1 in listOfByteArraysF52) {
                put(byteArray1)
            }
        }

        //**************************************************************
        val Fld55lenint=RequestFields.Field55.length/2
        var Fld55lenstr=Fld55lenint.toString()
        Fld55lenstr="0"+Fld55lenstr


        fieldLength = Fld55lenstr.length/2

        Log.d("TransData", "Fld55lenstr.........: " + Fld55lenstr)

        val fld55len  = Array(fieldLength) { "" }

        charArraylenF55 = Fld55lenstr.toCharArray()

        j=0
        for (i in 0 until fieldLength * 2 step 2) {
            fld55len[j] = "${charArraylenF55[i]}${charArraylenF55[i + 1]}"
            j++
        }
        for (r in fld55len.indices) {
            listOfByteArrayslenF55.add(HexUtil.hexStr2Byte(fld55len[r]))
        }
        Log.d(TAG, "list of byte arrays: $listOfByteArrayslenF55")


        for (byteArray1 in listOfByteArrayslenF55) {
            lengthOflenF55 += byteArray1.size
        }

        //Field 55

        fieldLength = RequestFields.Field55.length / 2
        Log.d("TransData", "headertry.........: ${RequestFields.Field55}")

        val  fld55= Array(fieldLength) { "" }
        charArrayF55 = RequestFields.Field55.toCharArray()

        j=0
        for (i in 0 until fieldLength * 2 step 2) {
            fld55[j] = "${charArrayF55[i]}${charArrayF55[i + 1]}"
            j++
        }


        //************************************************ end field52/
        //********************************************************
        for (r in fld55.indices) {
            listOfByteArraysF55.add(HexUtil.hexStr2Byte(fld55[r]))
        }
        Log.d(TAG, "list of byte arrays: $listOfByteArraysF55")


        for (byteArray1 in listOfByteArraysF55) {
            lengthOfF55 += byteArray1.size
        }



        //Log.d(TAG, "len55:" + Utility.byte2HexStr(len_byte))



        val byteBufferF55nF62 = ByteBuffer.allocate(lengthOflenF55 +
                lengthOfF55+
                lengthOfF62+field11.size)

        byteBufferF55nF62.apply {
            for (byteArray in listOfByteArrayslenF55) {
                put(byteArray)
            }
            for (byteArray in listOfByteArraysF55) {
                put(byteArray)
            }
            for (byteArray in listOfByteArraysF62) {
                put(byteArray)
            }
            put(field11)
        }


        val bufferF55nF62 = byteBufferF55nF62.array()
        lateinit var combinedBuffer: ByteBuffer
        val firstBuffer: ByteArray = buffer?.array() ?: "".toByteArray()
        val bufferManual: ByteArray = bufferForManual?.array() ?: "".toByteArray()

        Log.d(TAG, "packet for manual....:" + HexUtil.toHexString(bufferManual))
        val field52: ByteArray = bufferForF52.array()
        Log.d(TAG, "Sent field52....:" + HexUtil.toHexString(field52))
        /*if(txnMenuType.equals(Txntype.manualCardEntry)) {
            Log.d(TAG, "txnmenutype:${txnMenuType}")
            combinedBuffer = ByteBuffer.allocate(bufferManual.size )
            combinedBuffer.apply {
                put(bufferManual)
            }

        }*/

        if(txnType.equals(TxnType.REVERSAL)||txnType.equals(TxnType.M_REVERSAL)){
            Log.d(TAG, "reversalllllll..:" )
            combinedBuffer = ByteBuffer.allocate(firstBuffer.size )
            combinedBuffer.apply {
                put(firstBuffer)
            }
           Log.d("Tag","packeddata"+combinedBuffer)
        }else if(txnType.equals(TxnType.PURCHASE)||txnType.equals(TxnType.REFUND)||txnType.equals(TxnType.BALANCE_INQUIRY)||txnType.equals(TxnType.PRE_AUTH)||txnType.equals(TxnType.PRE_AUTH_COMPLETION)){
            if(isOnlinePin){
                combinedBuffer = ByteBuffer.allocate(firstBuffer.size +field52.size+ bufferF55nF62.size)
                combinedBuffer.apply {
                    put(firstBuffer)
                    put(field52)
                    put(bufferF55nF62)
                }
                    }
            else{

                combinedBuffer = ByteBuffer.allocate(firstBuffer.size + bufferF55nF62.size)
                combinedBuffer.apply {
                    put(firstBuffer)
                    put(bufferF55nF62)
                }

            }

        }else if(txnType.equals(TxnType.SETTLEMENT)){
            combinedBuffer = ByteBuffer.allocate(firstBuffer.size )
            combinedBuffer.apply {
                put(firstBuffer)
            }
        } else if(txnType.equals(TxnType.KEY_DOWNLOAD)){
            combinedBuffer = ByteBuffer.allocate(firstBuffer.size )
            combinedBuffer.apply {
                put(firstBuffer)
            }
        }else if(txnType.equals(TxnType.M_PURCHASE)||txnType.equals(TxnType.M_BALANCE_INQUIRY)){
            combinedBuffer = ByteBuffer.allocate(firstBuffer.size )
            combinedBuffer.apply {
                put(firstBuffer)
                   }
               }
        val result:ByteArray= combinedBuffer.array() ?: "".toByteArray()



        Log.d(TAG, "Sent packet....:" + HexUtil.toHexString(result))

        return result
    }
    fun unpackResponseFields(responseMessage: String) {
        ResponseFields.Header=responseMessage.substring(0,14)
        Log.d(TAG,"header:"+ResponseFields.Header)
        ResponseFields.MTI = responseMessage.substring(14, 18)
        //val MTI = hex2Asc(ResponseFields.MTI)
        Log.d(TAG,"mti:"+ResponseFields.MTI)

        ResponseFields.primaryBitmap = responseMessage.substring(18, 34)
        println("primarybitmap : ${ResponseFields.primaryBitmap}")

        val responseBody = responseMessage.substring(34)
        println("responsebody : $responseBody")

        val binaryBitmap1 = hex2Binary(ResponseFields.primaryBitmap)
        println("binarybitmap1 : $binaryBitmap1")

        assignValue2ResponseFields(binaryBitmap1, responseBody)
    }
    fun clearVariables(){
        ResponseFields.Header=""
        ResponseFields.MTI=""
        ResponseFields.primaryBitmap=""
        ResponseFields.Field02=""
        ResponseFields.Field03=""
        ResponseFields.Field04=""
        ResponseFields.Field07=""
        ResponseFields.Field11=""
        ResponseFields.Field12=""
        ResponseFields.Field13=""
        ResponseFields.Field22=""
        ResponseFields.Field24=""
        ResponseFields.Field25=""
        ResponseFields.Field35=""
        ResponseFields.Field37=""
        ResponseFields.Field38=""
        ResponseFields.Field39=""
        ResponseFields.Field41=""
        ResponseFields.Field42=""
        ResponseFields.Field49=""
        ResponseFields.Field52=""
        ResponseFields.Field55=""
        ResponseFields.Field62=""

        RequestFields. Header=""
        RequestFields. MTI=""
        RequestFields. primaryBitmap=""
        RequestFields. Field02=""
        RequestFields. Field03=""
        RequestFields. Field04=""
        RequestFields. Field07=""
        RequestFields. Field11=""
        RequestFields. Field12=""
        RequestFields. Field13=""
        RequestFields. Field14=""
        RequestFields. Field22=""
        RequestFields. Field24=""
        RequestFields. Field25=""
        RequestFields. Field35=""
        RequestFields. Field37=""
        RequestFields. Field38=""
        RequestFields. Field39= ""
        RequestFields. Field41=""
        RequestFields. Field42=""
        RequestFields. Field49=""
        RequestFields. Field52=""
        RequestFields. Field55=""
        RequestFields. Field60=""
        RequestFields. Field62=""
        RequestFields. Field63=""
        RequestFields. endValue4F63=""

      }



    fun fillGapSequence(data: String, size: Int): String {
        var result = data
        while (result.length != size) {
            result = "0$result"
        }
        return result
    }

    fun assignValue2ResponseFields(binaryBitmap: String, responseBody: String) {
        var n = 0
        var prefix = ""
        val charArray = binaryBitmap.toCharArray()
        val strArrayBitmap = Array(charArray.size) { i -> charArray[i].toString() }

        println("binaryBitmap1 : ${strArrayBitmap[1]}")

        for (i in 0 until 64) {
            if (strArrayBitmap[i] == "1") {
                val j = i + 1
                println("bitmap available : $j")
                when (j) {

                    3 -> {
                        ResponseFields.Field03 = responseBody.substring(n, n + 6)
                        Log.d(TAG, "field03: ${ResponseFields.Field03}")
                        n += 6
                    }
                    4 -> {
                        ResponseFields.Field04 = responseBody.substring(n, n + 12)
                        Log.d(TAG, "field04: ${ResponseFields.Field04}")
                        n += 12
                    }

                    11 -> {
                        ResponseFields.Field11 = responseBody.substring(n, n + 6)
                        Log.d(TAG, "field11: ${ResponseFields.Field11}")
                        n += 6
                    }
                    12 -> {
                        ResponseFields.Field12 = responseBody.substring(n, n + 6)
                        Log.d(TAG, "field12: ${ResponseFields.Field12}")
                        n += 6
                    }
                    13 -> {
                        ResponseFields.Field13 = responseBody.substring(n, n + 4)
                        Log.d(TAG, "field13: ${ResponseFields.Field13}")
                        n += 4
                    }
                    24 -> {
                        ResponseFields.Field24 = responseBody.substring(n, n + 4)
                        Log.d(TAG, "field24: ${ResponseFields.Field24}")
                        n += 4
                    }
                    37 -> {
                        ResponseFields.Field37 = hex2Asc(responseBody.substring(n, n + 24))
                        Log.d(TAG, "field37: ${ResponseFields.Field37}")
                        n += 24
                    }
                    38 -> {
                        ResponseFields.Field38 = hex2Asc(responseBody.substring(n, n + 12))
                        Log.d(TAG, "field38: ${ResponseFields.Field38}")
                        n += 12
                    }
                    39 -> {
                        ResponseFields.Field39 = hex2Asc(responseBody.substring(n, n + 4))
                        Log.d(TAG, "field39: ${ResponseFields.Field39}")
                        n += 4
                    }
                    41 -> {
                        ResponseFields.Field41 = hex2Asc(responseBody.substring(n, n + 16))
                        Log.d(TAG, "field41: ${ResponseFields.Field41}")
                        n += 16
                    }
                    49 -> {
                        ResponseFields.Field49 = responseBody.substring(n, n + 3)
                        Log.d(TAG, "field49: ${ResponseFields.Field49}")
                        n += 3
                    }
                    54 -> {
                        val prefix1 = responseBody.substring(n, n + 3)
                        println("prefix1: $prefix1")
                        val num1 = prefix1.toInt()
                        n += 3

                        ResponseFields.Field54 = responseBody.substring(n, n + num1)
                        Log.d(TAG, "field54: ${ResponseFields.Field54}")
                        n += 3
                    }
                    55 -> {
                        prefix = responseBody.substring(n, n + 4)
                        println("prefix: $prefix")
                        val num = prefix.toInt()
                        n += 4
                        ResponseFields.Field55 = responseBody.substring(n)
                        Log.d(TAG, "field55: ${ResponseFields.Field55}")
                    }
                }
            }
        }
    }
    private fun hex2Binary(hexString: String): String {
        val binary = StringBuilder()
        for (i in hexString.indices) {
            val hexChar = hexString[i]
            val binaryString = Integer.toBinaryString(Integer.parseInt(hexChar.toString(), 16))
            binary.append(String.format("%4s", binaryString).replace(' ', '0'))
        }
        return binary.toString()
    }

    fun hex2Asc(hexString: String): String {
        val output = StringBuilder()
        for (i in hexString.indices step 2) {
            val hex = hexString.substring(i, i + 2)
            output.append(hex.toInt(16).toChar())
        }
        return output.toString()
    }



}