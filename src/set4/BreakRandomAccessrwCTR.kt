package set4
// 25. Break "random access read/write" AES CTR

import set1.AES
import set1.decodeBase64
import set2.random128key
import set3.aesCtr
import set3.incrementByteArray
import java.io.File
import kotlin.experimental.xor


class Global{
    companion object{
        val key = random128key()
        val nonce = ByteArray(8)

        fun initNonce(){
            val randomBytes = random128key()
            for (i in 0..7){
                nonce[i] = randomBytes[i]
            }
        }
    }
}
fun main(){

    // extracting plaintext from encrypted file data
    val file = File("data7.txt")
    val tempkey = "YELLOW SUBMARINE"
    val keyBytes = AES.asciiToByteArray(tempkey)
    val decryptor = AES(keyBytes)
    var cipher = ""
    file.forEachLine {
        val msg = decodeBase64(it)
        cipher=cipher+msg
    }
    val n = cipher.length

    //stores bytes of plaintext
    val plainTextBytes = ByteArray(n)

    var i = 0
    while (i<n){
        val currentBlock = cipher.substring(i,i+16)
        val block = AES.asciiToByteArray(currentBlock)

        val decryptedBlock = decryptor.decipher(block)

        for(j in 0..15){
            plainTextBytes[i+j] = decryptedBlock[j]
        }
        i+=16
    }
    // extracting plaintext

    //initializing nonce value for ctr
    Global.initNonce()

    val key = Global.key
    val nonce = Global.nonce

    val cipherTextBytes = aesCtr(key,nonce,plainTextBytes)
    //encrypted string
    val cipherText = AES.byteArrayToAscii(cipherTextBytes)

    //idea send null bytes to edit text
    //it will allow us to recover keystream

    //default bytearray is of nulls
    val attackString = AES.byteArrayToAscii(ByteArray(cipherText.length))
    val keyStreamStolen = edit(cipherText,0,attackString)

    val keyStream= AES.asciiToByteArray(keyStreamStolen)

    val txtBytes = xorSameSize(keyStream,cipherTextBytes)

    val txt = AES.byteArrayToAscii(txtBytes)

    println("Decrypted --------------------- ")
    println(txt)

}

fun xorSameSize(keyStream:ByteArray,data:ByteArray):ByteArray{
    val n = data.size

    val output = ByteArray(n)
    for(i in 0..<n){
        output[i] = keyStream[i] xor data[i]
    }

    return output
}

fun generateKeyStream(key:ByteArray,nonce:ByteArray,length:Int):ByteArray{
    var i = 0
    val n = length

    val keystream = ByteArray(n)

    val counter = byteArrayOf(0,0,0,0,0,0,0,0)
    val combined = ByteArray(16,{0})
    val aes = AES(key)
    while (i<n){

        //16 bit input for aes
        for(k in 0..7){
            combined[k] = nonce[k]
        }
        for(k in 0..7){
            combined[k+8] = counter[k]
        }

        //xor value
        val cipher = aes.cipher(combined)

        //last byte index
        val end = if( i+15 <n) i+15 else n-1

        //xoring the data and cipher
        var j =0
        while(i<=end){
            keystream[i] = cipher[j]
            j+=1
            i+=1
        }

        //incrementing counter
        incrementByteArray(counter)

    }

    return keystream
}

fun edit(cipherText:String,offset: Int,newText:String):String{
    val length = cipherText.length
    val key = Global.key
    val nonce = Global.nonce

    val currentCipherTextBytes = AES.asciiToByteArray(cipherText)
    val newCipherTextBytes = ByteArray(length,{currentCipherTextBytes[it]})

    val newTextBytes = AES.asciiToByteArray(newText)

    val keystream = generateKeyStream(key,nonce,length)

    var end = offset+newText.length
    if(end > length)
        end = length

    var j = 0

    for(i in offset..<end){
        newCipherTextBytes[i] = keystream[i] xor newTextBytes[j]
        j+=1
    }

    return AES.byteArrayToAscii(newCipherTextBytes)

}