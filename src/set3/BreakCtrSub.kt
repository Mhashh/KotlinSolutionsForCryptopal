package set3
// 19. Break fixed-nonce CTR mode using substitutions

//I have solved this via breaking xor of transpose of cipher strings
//and have no idea how it can be done using substitutions

import set1.AES
import set1.decodeBase64
import set2.random128key
import java.io.File
import kotlin.experimental.xor

class GLobal{
    companion object{
        val key = random128key()

    }
}

fun main(){

    val file = File("data19.txt")
    val plaintexts = Array<String>(40,{""})
    val nonce = byteArrayOf(0,0,0,0,0,0,0,0)
    var i = 0

    file.forEachLine{
        plaintexts[i] = decodeBase64(it)
        i++
    }

    val ciphers = Array<ByteArray>(40,{ByteArray(1)})

    i=0

    for (pt in plaintexts){
        val bytes = AES.asciiToByteArray(pt)
        ciphers[i] = aesCtr(GLobal.key,nonce,bytes)
        i++
    }

    val keystream = breakXor(ciphers)
    println("Decrypted ---------------------------------------->")
    for( bytes in ciphers){
        val plaintext = xorByteArrays(bytes,keystream)
        println(AES.byteArrayToAscii(plaintext))

    }


}

fun xorByteArrays(data:ByteArray,keystream:ByteArray):ByteArray{
    val transformed = ByteArray(data.size)

    for(i in 0..data.size-1){
        transformed[i] = data[i] xor keystream[i]
    }

    return transformed
}

fun breakXor(ciphers:Array<ByteArray>):ByteArray{

    val n = ciphers.size
    var max = 0

    for(bytes in ciphers){
        if(bytes.size > max){
            max = bytes.size
        }
    }

    var i = 0
    //stores keybyte values
    val keystream = ByteArray(max)

    //loop through all bytes of a cipher to transpose and find xor key
    while(i<max){

        val byteArray = ByteArray(n)
        var rowcount = 0

        //transpose of ith byte
        for(j in 0..n-1){

            if(i < ciphers[j].size) {
                byteArray[rowcount] = ciphers[j][i]
                rowcount++
            }
        }

        var score = 100.0
        var keybyte = 0.toByte()


        //trying every byte
        for(key in 0..255){
            val stringBuilder = StringBuilder(rowcount)
            for ( j in 0..rowcount-1){
                stringBuilder.append((byteArray[j] xor key.toByte()).toInt().toChar())
            }
            val tempscore = frequencyScore(stringBuilder.toString())
            if(tempscore<score){

                score = tempscore
                keybyte = key.toByte()

            }
        }

        keystream[i] = keybyte

        i++
    }

    return keystream

}

fun frequencyScore(msg:String):Double{
    val frequencyMap = arrayOf(8.2,1.5,2.8,4.3,12.7,2.2,2.0,6.1,7.0,0.15,0.77,4.0,2.4,6.7,7.5,1.9,0.095,6.0,6.3,9.1,2.8,0.98,2.4,0.15,2.0,0.074)
    var score = 0.0
    val alphabet = Array<Double>(26,init = {it->0.0 })
    var n = 0.0
    for(i in msg){
        //A-Z
        if(i.code in 65..90){
            alphabet[i.code-65] = alphabet[i.code-65]+1
            n +=1
        }
        //a-z
        else if(i.code in 97..122){
            alphabet[i.code-97] = alphabet[i.code-97]+1
            n+=1
        }
        //empty space comman quote
        else if(i.code == 32 || i.code == 39 || i.code == 44 || i.code == 59 || i.code == 63){
            n+=1
        }


    }

    for(i in 0..25){
        alphabet[i] = (alphabet[i]*100.0)/n
        alphabet[i] = (Math.abs(alphabet[i]-frequencyMap[i]))/n
        score += alphabet[i]
    }

    return score
}


