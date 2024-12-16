package set2
// 14. Byte-at-a-time ECB decryption (Harder)

import set1.AES
import set1.AesECBencrypt
import set1.decodeBase64



class Global3{

    companion object{
        var key = random128key()
        var prefix = "xycfgykchch nxgcncnghmnc"
    }
}

fun main(){
    var lengths = detectPrefixLength(::anotherECBOracle)
    var prefixLength = lengths[0]
    var paddingNeeded = lengths[1]
    var startingBlock = lengths[2]
    println("Prefix length > Actual : ${Global3.prefix.length} , Detected : ${prefixLength}")
    println("Padding needed after prefix: $paddingNeeded")
    var suffixLength = detectSuffixLength(::anotherECBOracle,prefixLength,paddingNeeded)
    println("Suffix Length : $suffixLength")
    var decryptedSuffix = decryptHiddenSuffix(::anotherECBOracle,suffixLength,paddingNeeded,startingBlock)
    println("SUFFIX---------------------------------------------->")
    println(decryptedSuffix)

}


fun anotherECBOracle(input:String):ByteArray{
    var key = Global3.key
    var prefixString = Global3.prefix
    var suffixBase64 = "Um9sbGluJyBpbiBteSA1LjAKV2l0aCBteSByYWctdG9wIGRvd24gc28gbXkg" +
            "aGFpciBjYW4gYmxvdwpUaGUgZ2lybGllcyBvbiBzdGFuZGJ5IHdhdmluZyBq" +
            "dXN0IHRvIHNheSBoaQpEaWQgeW91IHN0b3A/IE5vLCBJIGp1c3QgZHJvdmUg" +
            "YnkK"

    var suffixString =  decodeBase64(suffixBase64)

    var dataModified:String = prefixString+input+suffixString

    dataModified = pkcsPadding(dataModified,16)

    var dataBlock = AES.asciiToByteArray(dataModified)

    var encdata  = AesECBencrypt(dataBlock,key)

    return encdata
}

fun detectPrefixLength(encryptionOracle:(String)->(ByteArray)):IntArray{
    var targetBlockIndex = 0
    var prefix1 = "a"
    var prefix2 = "b"

    var enc1 = encryptionOracle(prefix1)
    var enc2 = encryptionOracle(prefix2)
    var found = false
    blockfind@while(!found){
        for(i in 0..15){
            if(enc1[targetBlockIndex+i] != enc2[targetBlockIndex+i]){
                found = true
                break@blockfind
            }
        }
        targetBlockIndex+=16
    }

    println("Target block : $targetBlockIndex")

    var differBlock = -1

    while (differBlock == -1){
        prefix1 = 'a'+prefix1
        prefix2 = 'a'+prefix2

        enc1 = encryptionOracle(prefix1)
        enc2 = encryptionOracle(prefix2)

        var targetBlockMatch = true

        for(i in 0..15){
            if(enc1[targetBlockIndex+i] != enc2[targetBlockIndex+i]){
                targetBlockMatch = false
                break
            }
        }

        if(targetBlockMatch){
            differBlock=targetBlockIndex+16
        }
    }

    var prefixLength = targetBlockIndex+16 - (prefix1.length-1)

    return intArrayOf(prefixLength,prefix2.length-1,differBlock)

}

fun detectSuffixLength(encryptionOracle:(String)->(ByteArray),prefixLength:Int,paddingLength:Int):Int{

    var padding = ""

    var i = 0
    while (i<paddingLength){
        padding = padding+"a"
        i+=1
    }

    var prefix=""
    var encryptedData = encryptionOracle(padding+prefix)

    var emptyLen = encryptedData.size
    var addedLen = 0

    do{
        prefix = prefix+"a"
        addedLen = encryptionOracle(padding+prefix).size

    }while(addedLen==emptyLen);

    return addedLen-prefixLength-paddingLength-prefix.length-15
}

fun decryptHiddenSuffix(encryptionOracle:(String)->ByteArray,secretLength:Int,paddingLength: Int,startingBlock:Int):String{
    var starttime = System.currentTimeMillis()
    var level = 0
    var index = 1

    var padding = ""

    var i = 0
    while (i<paddingLength){
        padding = padding+"a"
        i+=1
    }

    var stringBuilder = StringBuilder(secretLength)

    var prefix = "aaaaaaaaaaaaaaa"
    var prefixadded = ""

    do{

        for(i in 0..15) {


            var encrypted = encryptionOracle(padding+prefix)
            var char = '0'

            for (j in 0..255) {
                var testencrypted = encryptionOracle(padding+prefix+prefixadded+(j.toChar()))
                var match = true
                for(j in 0..15){
                    if(testencrypted[startingBlock+j].compareTo(encrypted[startingBlock+level+j]) != 0){
                        match = false
                        break
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
    var endtime = System.currentTimeMillis()
    var timetook = (endtime-starttime).toFloat()/1000f
    println("Time taken for decryption : $timetook sec")
    return stringBuilder.toString()
}