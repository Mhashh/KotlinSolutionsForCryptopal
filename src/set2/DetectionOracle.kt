package set2

import set1.AES
import set1.AesECBdecrypt
import kotlin.random.Random

class ModeData(var type:String,var blockData: ByteArray)
{}

fun main(){

    for (i in 0..20){
        detectionOracle(::randomModeEncrypt)
    }
}

fun random128key():ByteArray{
    var key = Random(System.currentTimeMillis()).nextBytes(16)

    return key
}

//encryption oracle returns encryption mode and data
fun randomModeEncrypt(data:String):ModeData{
    var randomGen = Random(System.currentTimeMillis())

    var extraCount = 5+randomGen.nextInt()%6
    var extraFirst = randomGen.nextBytes(extraCount)
    var extraLast = randomGen.nextBytes(extraCount)

    var dataModified = extraFirst.toString()+data+extraLast.toString()

    dataModified = pkcsPadding(dataModified,16)

    var dataBlock = AES.asciiToByteArray(dataModified)

    var key = random128key()
    var mode = randomGen.nextInt()%2

    if(mode == 1){
        var encdata  = AesECBdecrypt(dataBlock,key)
        return ModeData("ECB",encdata)
    }
    else{
        var encdata = AesCBCEncrypt(dataBlock,randomGen.nextBytes(16),key)
        return ModeData("CBC",encdata)
    }
}

//detects encryption type using a smartly chosen input string
fun detectionOracle(encryptionOracle:(data:String)->ModeData){
    var inputString = ""
    var inputLength = (16-5)+16+16

    while(inputLength > 0){
        inputString = inputString+"h"
        inputLength-=1
    }

    var enc:ModeData = encryptionOracle(inputString)
    var actualMode = enc.type
    var encryptedData = enc.blockData

    var secondBlock = encryptedData.sliceArray(16..31)
    var thirdBlock = encryptedData.sliceArray(32..47)
    var predicted = "ECB"

    for(i in 0..15){
        if(secondBlock[i].compareTo(thirdBlock[i]) != 0){
            predicted = "CBC"
            break
        }
    }

    println("Actual : $actualMode , Predicted : $predicted")
}

@OptIn(ExperimentalStdlibApi::class)
fun cipherModeDetection(encMsg:ByteArray):String{

    var numBlocks = encMsg.size / 16

    var mutableSet:MutableSet<String> = mutableSetOf()

    var i = 0
    var currentBlock = ByteArray(16,{0})

    while (i<numBlocks){
        for(j in 0..15){
            currentBlock[j] = encMsg[i+j]
        }

        var targetString = currentBlock.toHexString()
        if(targetString in mutableSet){
            return "ECB"
        }else{
            mutableSet.add(targetString)
        }
        i+=16
    }

    return "CBC"
}