package set3
// 17. The CBC padding oracle

import set1.AES
import set1.decodeBase64
import set2.*
import kotlin.experimental.xor
import kotlin.random.Random



class Global{

    companion object{
        var key = random128key()
        var iv = random128key()
    }
}

fun main(){
    //cipher and iv received
    val cipher = cbcRndmEncrypt()
    var iv = Global.iv

    val stringBuilder = StringBuilder(cipher.size)

    var blockIndex = 0
    while(blockIndex*16 < cipher.size){

        val start = blockIndex*16
        val end = start+15

        val currentBlock = cipher.sliceArray(start..end)
        val str = extractDecryptedByte(::oracle,currentBlock,iv)
        stringBuilder.append(str)

        iv = currentBlock

        blockIndex+=1
    }
    println("DECRYPTED---------------------->")
    println(stringBuilder.toString())

}

fun cbcRndmEncrypt():ByteArray{
    val toChoose = Array<String>(10,{it->""})

    toChoose[0]="MDAwMDAwTm93IHRoYXQgdGhlIHBhcnR5IGlzIGp1bXBpbmc="
    toChoose[1]="MDAwMDAxV2l0aCB0aGUgYmFzcyBraWNrZWQgaW4gYW5kIHRoZSBWZWdhJ3MgYXJlIHB1bXBpbic="
    toChoose[2]="MDAwMDAyUXVpY2sgdG8gdGhlIHBvaW50LCB0byB0aGUgcG9pbnQsIG5vIGZha2luZw=="
    toChoose[3]="MDAwMDAzQ29va2luZyBNQydzIGxpa2UgYSBwb3VuZCBvZiBiYWNvbg=="
    toChoose[4]="MDAwMDA0QnVybmluZyAnZW0sIGlmIHlvdSBhaW4ndCBxdWljayBhbmQgbmltYmxl"
    toChoose[5]="MDAwMDA1SSBnbyBjcmF6eSB3aGVuIEkgaGVhciBhIGN5bWJhbA=="
    toChoose[6]="MDAwMDA2QW5kIGEgaGlnaCBoYXQgd2l0aCBhIHNvdXBlZCB1cCB0ZW1wbw=="
    toChoose[7]="MDAwMDA3SSdtIG9uIGEgcm9sbCwgaXQncyB0aW1lIHRvIGdvIHNvbG8="
    toChoose[8]="MDAwMDA4b2xsaW4nIGluIG15IGZpdmUgcG9pbnQgb2g="
    toChoose[9]="MDAwMDA5aXRoIG15IHJhZy10b3AgZG93biBzbyBteSBoYWlyIGNhbiBibG93"

    val stringIndex = Random(System.currentTimeMillis()).nextInt(0,10)

    var chosenString = toChoose[stringIndex]

    chosenString = decodeBase64(chosenString)

    val data = pkcsPadding(chosenString,16)
    val dataBytes = AES.asciiToByteArray(data)
    val key = Global.key
    val iv = Global.iv
    val cipher = AesCBCEncrypt(dataBytes,iv,key)

    return cipher
}

fun oracle(cipher:ByteArray,iv:ByteArray):Boolean{
    val key = Global.key

    val plaintextBytes = AesCBCDecrypt(cipher,iv,key)
    val plaintext = AES.byteArrayToAscii(plaintextBytes)

    try{
        pkcsValidation(plaintext,16)
        return true

    }
    catch (e:InvalidPKCSPadding){
        return false
    }
}

fun extractDecryptedByte(cbcPadOracle:(ByteArray,ByteArray)->Boolean,cipherBlock:ByteArray,iv:ByteArray):String{
    val stringBuilder = StringBuilder(16)
    val attackIv = byteArrayOf(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16)

    val decryptedByte = byteArrayOf(0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0)
    var pad = 1
    for(i in 15 downTo 0){

        //all possible bytes
        for(j in 0..255){
            attackIv[i] = j.toByte()

            val validPad = cbcPadOracle(cipherBlock,attackIv)

            if(validPad){
                decryptedByte[i] = attackIv[i] xor pad.toByte()
                break
            }
        }

        pad+=1

        for(j in 15 downTo (15-pad+2)){
            attackIv[j] = decryptedByte[j] xor pad.toByte()
        }
    }

    for( i in 0..15){
        val char = (decryptedByte[i] xor iv[i]).toInt().toChar()
        stringBuilder.append(char)
    }

    return stringBuilder.toString()
}