package set3
// 18. Implement CTR, the stream cipher mode

import set1.AES
import set1.decodeBase64
import kotlin.experimental.xor

fun main(){
    var str =  "L77na/nrFsKvynd6HzOoG7GHTLXsTVu9qvY/2syLXzhPweyyMTJULu/6/kXX0KSvoOLSFQ=="
    str = decodeBase64(str)
    val strBytes = AES.asciiToByteArray(str)

    val key = AES.asciiToByteArray("YELLOW SUBMARINE")
    val nonce = byteArrayOf(0,0,0,0,0,0,0,0)

    val transformedBytes = aesCtr(key,nonce,strBytes)
    println("Decrypted--------------------------->")
    println(AES.byteArrayToAscii(transformedBytes))
}

fun aesCtr(key: ByteArray,nonce:ByteArray,data:ByteArray):ByteArray{

    var i = 0
    val n = data.size

    val ans = ByteArray(n)

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
            ans[i] = cipher[j] xor data[i]
            j+=1
            i+=1
        }

        //incrementing counter
        incrementByteArray(counter)

    }

    return ans

}

fun incrementByteArray(counter:ByteArray){
    var i = 0
    val n = counter.size

    var carry: Int
    var currentInt = counter[0].toInt()
    currentInt++
    if(currentInt == 256){
        counter[0] = (0).toByte()
        carry= 1
    }
    else{
        counter[0] = currentInt.toByte()
        carry=0
    }
    i++

    while (carry>0 && i<n){
        currentInt = counter[i].toInt()
        currentInt++
        if(currentInt == 256){
            counter[i] = (0).toByte()
            carry= 1
        }
        else{
            counter[i] = currentInt.toByte()
            carry=0
        }
        i++
    }
}