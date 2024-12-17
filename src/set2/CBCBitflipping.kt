package set2
// 16. CBC bitflipping attacks

import set1.AES
import kotlin.experimental.xor

class Global4{

    companion object{
        var key = random128key()
        var iv = random128key()
    }
}

fun main(){
    val attackString = "wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww"

    val enc = queryEncryptor(attackString)

    val attackBytes = AES.asciiToByteArray("wwwwwwwwwwwwwwww")
    val targetBlock = enc.sliceArray(32..47)
    val toadd = AES.asciiToByteArray("44;admin=true;44")

    xorByteArrays(attackBytes,targetBlock)
    xorByteArrays(attackBytes,toadd)

    for(i in 0..15){
        enc[32+i] = attackBytes[i]
    }

    queryDecryptor(enc)
}

fun xorByteArrays(b1:ByteArray,b2:ByteArray){

    val n = b1.size-1

    for(i in 0..n){
        b1[i] = b1[i].xor(b2[i])
    }
}

fun queryEncryptor(userData:String):ByteArray{
    val prefix = "comment1=cooking%20MCs;userdata="
    val suffix = ";comment2=%20like%20a%20pound%20of%20bacon"

    //removing ; =
    var data = userData.replace(";","%3b")
    data = data.replace("=","%3d")

    val query = pkcsPadding( prefix+data+suffix,16)
    val queryBlock = AES.asciiToByteArray(query)

    val key = Global4.key
    val iv = Global4.iv
    val encrypted = AesCBCEncrypt(queryBlock,iv,key)
    return encrypted
}

fun queryDecryptor(cipher:ByteArray){
    val key = Global4.key
    val iv = Global4.iv

    val queryBlock = AesCBCDecrypt(cipher,iv,key)

    val query = AES.byteArrayToAscii(queryBlock)

    val auth = query.contains(";admin=true;")
    println("Decrypted query ---------------------------->")
    println(query)

    print("Admin : ")
    if(auth){
        println("YES")
    }
    else{
        println("NO")
    }
}