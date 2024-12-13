package set2

import set1.AES
import set1.AesECBencrypt
import set1.decodeBase64

class Global{

    companion object{
        var key = random128key()
    }
}

fun main(){
    var lengths = blockLength()
    var blockLen = lengths[0]
    var secretLen = lengths[1]
    println("Block length : $blockLen, Secret length : $secretLen")
    var mode = detectECB(::newECBOracle)
    println("Encryption mode : $mode")


    if(mode.compareTo("ECB")==0){
        var decrypted = decryptHiddenString(::newECBOracle,secretLen)
        println("DECRYPTED----------------------------------------->")
        println(decrypted)
    }

}

fun newECBOracle(input:String):ByteArray{
    var key = Global.key
    var suffixBase64 = "Um9sbGluJyBpbiBteSA1LjAKV2l0aCBteSByYWctdG9wIGRvd24gc28gbXkg" +
            "aGFpciBjYW4gYmxvdwpUaGUgZ2lybGllcyBvbiBzdGFuZGJ5IHdhdmluZyBq" +
            "dXN0IHRvIHNheSBoaQpEaWQgeW91IHN0b3A/IE5vLCBJIGp1c3QgZHJvdmUg" +
            "YnkK"

    var suffixString =  decodeBase64(suffixBase64)

    var dataModified:String = input+suffixString

    dataModified = pkcsPadding(dataModified,16)

    var dataBlock = AES.asciiToByteArray(dataModified)

    var encdata  = AesECBencrypt(dataBlock,key)

    return encdata

}

fun blockLength():IntArray{

    var lengthOfBlock = 0
    var prefix=""
    var encryptedData = newECBOracle(prefix)

    var emptyLen = encryptedData.size
    var addedLen = 0

    do{
        prefix = prefix+"a"
        addedLen = newECBOracle(prefix).size

    }while(addedLen==emptyLen);

    var lengths:IntArray = intArrayOf(0,0)
    //block length
    lengths[0] = addedLen-emptyLen
    //secretlength
    lengths[1] = addedLen-prefix.length-15
    return lengths

}

fun detectECB(encryptionOracle:(str:String)->ByteArray):String{
    var inputString = ""
    var inputLength = 32

    while(inputLength > 0){
        inputString = inputString+"h"
        inputLength-=1
    }

    var encryptedData = encryptionOracle(inputString)

    var firstBlock = encryptedData.sliceArray(0..15)
    var secondBlock = encryptedData.sliceArray(16..31)
    var predicted = "ECB"

    for(i in 0..15){
        if(secondBlock[i].compareTo(firstBlock[i]) != 0){
            predicted = "CBC"
            break
        }
    }

    return predicted
}

fun decryptHiddenString(encryptionOracle:(str:String)->ByteArray,secretLength:Int):String{
    var level = 0
    var index = 1
    var stringBuilder = StringBuilder(secretLength)

    var prefix = "aaaaaaaaaaaaaaa"
    var prefixadded = ""

    do{

        for(i in 0..15) {


            var encrypted = encryptionOracle(prefix)
            var char = '0'

            for (j in 0..255) {
                var testencrypted = encryptionOracle(prefix+prefixadded+(j.toChar()))
                var match = true
                for(j in 0..15){
                    if(testencrypted[j].compareTo(encrypted[level+j]) != 0){
                        match = false
                    }
                }
                if(match){
                    char = j.toChar()
                    stringBuilder.append(char)

                    break
                }
            }


            index++

            prefix = prefix.drop(1)
            prefixadded = prefixadded+char

            if(index == secretLength)
                break

        }
        prefix = prefixadded.drop(1)
        prefixadded=""

        level+=16
    }while(secretLength>index)

    return stringBuilder.toString()
}
