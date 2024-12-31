package set3
// 24. Create the MT19937 stream cipher and break it

import set1.AES
import set3.MT19937_32
import kotlin.experimental.xor
import kotlin.random.Random
import kotlin.random.nextUInt

fun main(){
    val seed:UInt = Random(System.currentTimeMillis()).nextUInt(0x10000u)
    println("Randomly selected seed : $seed")
    val chosenString = "AAAAAAAAAAAAA"
    val encrypted = streamCipher(seed,chosenString)

    val totalLength = encrypted.size
    val prefixLength = totalLength - chosenString.length

    var attackString = ""
    for(i in 1..totalLength){
        attackString += "A"
    }

    var foundseed = 0x10000u

    //bruteforce all 2^16 keys and compare output from prefixlength to end
    for (key in 0u..0xffffu){
        val guess = streamCipher(key,attackString)

        var seedFound = true

        for(i in prefixLength..<totalLength){
            if(guess[i] != encrypted[i]){
                seedFound = false
                break
            }
        }

        if(seedFound){
            foundseed = key
            break
        }

    }

    if(foundseed == 0x10000u){
        println("Seed not found")
    }
    else{
        println("Seed : $foundseed")
    }
}

fun streamCipher(seed:UInt,msg:String):ByteArray{
    val text = "random$msg"

    return streamEncrypt(seed,text)
}

fun streamEncrypt(seed:UInt,msg:String):ByteArray{
    val data = AES.asciiToByteArray(msg)
    val cipher = ByteArray(data.size)
    val mt = MT19937_32(seed)
    var i = 0
    while(i<data.size){
        cipher[i] = byteEncrypt(mt,data[i])
        i++
    }

    return cipher
}

fun byteEncrypt(mt:MT19937_32,byte:Byte):Byte{

    val rng = mt.random()

    val key = (rng and 0b11111111u).toByte()

    val xor = key xor byte
    return xor
}
