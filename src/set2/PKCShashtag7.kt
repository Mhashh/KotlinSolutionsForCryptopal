package set2
// 9. Implement PKCS#7 padding

@OptIn(ExperimentalStdlibApi::class)
fun main(){

    println("Input msg : ")
    var msg = readln()

    println("Input block size")
    var blockSize = readln().toInt()

    var paddedString = pkcsPadding(msg,blockSize)
    println("Padded string : $paddedString")
    println("Padded hex string : ${paddedString.toByteArray().toHexString()}")
}

fun pkcsPadding(msg:String,blockSize:Int):String{
    var n = msg.length
    var extraSpace = blockSize - (n%blockSize)
    var plainTextBuilder:StringBuilder = StringBuilder(msg)


    var i = 0
    while(i<extraSpace){
        plainTextBuilder.append(extraSpace.toChar())
        i+=1
    }
    if(extraSpace == 0){
        for (j in 0..15){
            plainTextBuilder.append(blockSize.toChar())
        }
    }
    return plainTextBuilder.toString()
}

fun pkcsPadding(msgBuilder:StringBuilder,blockSize:Int){
    var n = msgBuilder.length
    var extraSpace = blockSize - (n%blockSize)


    var i = 0
    while(i<extraSpace){
        msgBuilder.append(extraSpace.toChar())
        i+=1
    }
    if(extraSpace == 0){
        for (j in 0..15){
            msgBuilder.append(blockSize.toChar())
        }
    }

}

fun pkcsStrip(msg:String,blockLength:Int):String{

    var stringBuilder = StringBuilder(msg)

    var n = msg.get(msg.length-1).code
    var padded = true

    if(n<1 || n>blockLength)
        return stringBuilder.toString()

    var i = msg.length-1

    for(j in 1..n){
        var len = msg.get(i).code
        if(len != n){
            padded = false
            break
        }
        i-=1
    }

    if(padded){
        for( j in 1..n){
            stringBuilder.delete(stringBuilder.length-1,stringBuilder.length)
        }
    }

    return stringBuilder.toString()
}