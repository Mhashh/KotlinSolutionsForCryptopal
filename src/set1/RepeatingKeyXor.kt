package set1
// 5. Implement repeating-key XOR

fun main(){
    //println("Input text : ")
    var str = "Burning 'em, if you ain't quick and nimble\nI go crazy when I hear a cymbal"

    println("Input key : ")
    var key = readln()

    var ans = encryptXOR(str,key)

    println("Ascii output :\n$ans")

    var hex = asciiToHex(ans)
    println("Hex output :\n$hex")
}

fun encryptXOR(text:String,key:String):String{
    var ans = ""
    var keyIndex = 0
    var keyLength = key.length
    for(char in text){

        var a = char.code
        var keyByte = key.get(keyIndex).code

        var encryptedByte = a xor keyByte

        ans = ans + encryptedByte.toChar()
        keyIndex = (keyIndex+1)%keyLength

    }

    return ans
}


fun asciiToHex(str:String):String{
    var ans = ""
    val hexDecode:Array<Char> = arrayOf('0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f')
    for(char in str){
        var byte = char.code
        var firstHex = byte shr 4
        var lastHex = byte and 15

        ans = ans + hexDecode[firstHex]+hexDecode[lastHex]
    }
    return ans
}