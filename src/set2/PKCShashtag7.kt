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

}