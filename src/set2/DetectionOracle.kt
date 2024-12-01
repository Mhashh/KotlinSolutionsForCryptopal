package set2

import kotlin.random.Random

fun main(){

}

fun random128key():ByteArray{
    var key = Random(1).nextBytes(16)

    return key
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