package set1

import java.io.File
import kotlin.experimental.xor

fun main(){

    var file = File("data7.txt")
    val key = "YELLOW SUBMARINE"
    val keyBytes = AES.asciiToByteArray(key)
    val decryptor = AES(keyBytes)
    var cipher = ""
    file.forEachLine {
        var msg = decodeBase64(it)
        cipher=cipher+msg
    }
    var n = cipher.length
    var i = 0
    //taking 16 bytes of string and decrypting it
    while (i<n){
        var currentBlock = cipher.substring(i,i+16)
        var block = AES.asciiToByteArray(currentBlock)

        var decryptedBlock = decryptor.decipher(block)
        print(AES.byteArrayToAscii(decryptedBlock))
        i+=16
    }
    println()

}

fun AesECBencrypt(blocks:ByteArray,key:ByteArray):ByteArray{
    var encryptedText = ByteArray(blocks.size)
    var n = blocks.size

    val aes = AES(key)

    var i = 0
    var currentBlock = ByteArray(16)
    while(i<n){

        for(j in 0..15){
            currentBlock[j] = blocks[i+j]
        }

        var temp = aes.cipher(currentBlock)

        for (j in 0..15){
            encryptedText[i+j] = temp[j]
        }
        i+=16
    }
    return encryptedText
}

fun AesECBdecrypt(blocks:ByteArray,key:ByteArray):ByteArray{
    var plaintext = ByteArray(blocks.size)
    var n = blocks.size

    val aes = AES(key)

    var i = 0
    var currentBlock = ByteArray(16)
    while(i<n){

        for(j in 0..15){
            currentBlock[j] = blocks[i+j]
        }

        var temp = aes.decipher(currentBlock)

        for (j in 0..15){
            plaintext[i+j] = temp[j]
        }
        i+=16
    }
    return plaintext
}
