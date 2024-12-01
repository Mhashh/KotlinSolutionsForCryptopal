package set2

import set1.AES
import set1.decodeBase64
import java.io.File
import kotlin.experimental.xor

fun main(){
    var file = File("data10.txt")
    var stringBuilder = StringBuilder()
    file.forEachLine {
        var currentString = decodeBase64(it)
        stringBuilder.append(currentString)
    }

    pkcsPadding(stringBuilder,16)


    var blocks = AES.asciiToByteArray(stringBuilder.toString())
    var iv = byteArrayOf(0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0)
    var key = AES.asciiToByteArray("YELLOW SUBMARINE")

    //var encBytes = AesCBCEncrypt(blocks,iv,key)
    var plainBytes = AesCBCDecrypt(blocks,iv,key)
    var str = AES.byteArrayToAscii(plainBytes)
    println(str)
}

fun AesCBCEncrypt(blocks:ByteArray,iv:ByteArray,key:ByteArray):ByteArray{


    var encryptedtext = ByteArray(blocks.size)
    var n = blocks.size

    val aes = AES(key)

    var xorIv = ByteArray(16,{iv[it]})
    var currentBlock = ByteArray(16,{0})
    var i = 0
    while(i<n){
        //block xor (iv or previous cipher)
        for(j in 0..15){
            currentBlock[j] = xorIv[j].xor(blocks[i+j])
        }

        //encryption of block
        var encBlock = aes.cipher(currentBlock)

        //copying into return array and cipher block for next xor
        for(j in 0..15){
            encryptedtext[i+j] = encBlock[j]
            xorIv[j] = encBlock[j]
        }


        i+=16
    }
    return encryptedtext
}

fun AesCBCDecrypt(blocks:ByteArray,iv:ByteArray,key:ByteArray):ByteArray{
    var i = 0

    var plaintext = ByteArray(blocks.size)
    var n = blocks.size

    val aes = AES(key)

    var xorIv = ByteArray(16,{iv[it]})
    var currentBlock = ByteArray(16,{0})

    while(i<n){
        //current 16 byte block
        for(j in 0..15){
            currentBlock[j] = blocks[i+j]
        }

        //decryption of block
        var decBlock = aes.decipher(currentBlock)

        //copying into return array and cipher block for next xor
        for(j in 0..15){
            plaintext[i+j] = decBlock[j].xor(xorIv[j])
            xorIv[j] = currentBlock[j]
        }


        i+=16
    }
    return plaintext
}