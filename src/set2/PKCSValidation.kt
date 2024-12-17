package set2
// 15. PKCS#7 padding validation
class InvalidPKCSPadding : Exception("Invalid PKCS#7 padding detected")

fun main(){
    var string = "ICE ICE BABY\u0004\u0004\u0004\u0004"
    println(string)
    println(pkcsValidation(string,16))
    try{
        string = "ICE ICE BABY\u0005\u0005\u0005\u0005"
        println(string)
        var unpadded = pkcsValidation(string,16)
        println(unpadded)
    }
    catch (e:InvalidPKCSPadding){
        println(e.message)
    }

    try{
        string = "ICE ICE BABY\u0001\u0002\u0003\u0004"
        println(string)
        var unpadded = pkcsValidation(string,16)
        println(unpadded)
    }
    catch (e:InvalidPKCSPadding){
        println(e.message)
    }
}

fun pkcsValidation(msg:String,blockLength:Int):String{

    var stringBuilder = StringBuilder(msg)

    var n = msg.get(msg.length-1).code
    var padded = true

    if(n>=blockLength || n<=0)
        return msg

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
    else
        throw InvalidPKCSPadding()

    return stringBuilder.toString()
}