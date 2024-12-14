package set1
// 1. Convert hex to base64

fun main(){
    var base64  = listOf('A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','0','1','2','3','4','5','6','7','8','9','+','/')
    println("Input a hex string : ")
    var hexString = readln()

    var msglength = hexString.length

    if(msglength%3 != 0){
        var rem = msglength%3
        if(rem == 2){
            hexString = hexString+'0'
        }
        else
            hexString = hexString+'0'+'0'
    }

    msglength = hexString.length
    println(hexString)
    var ans = encoder(hexString)

    println(ans)
}

fun encoder(hexString:String):String{

    var hexMap:Map<Char,String> = mapOf('0' to "0000",'1' to "0001",'2' to "0010",'3' to "0011",'4' to "0100",
        '5' to "0101",'6' to "0110",'7' to "0111",'8' to "1000",'9' to "1001",'a' to "1010",
        'b' to "1011",'c' to "1100",'d' to "1101",'e' to "1110",'f' to "1111")

    var i = 0

    var last = hexString.length
    var buffer = arrayOf(0,0,0,0,0,0,0,0,0,0,0,0)

    var ans:String = ""
    while(i<last){
        var k = 0
        //3 hex characters
        for(j in i..i+2){
            var hex = hexString.get(j)
            var binEqv = hexMap.get(hex)
            if(binEqv != null){

                for(p in 0..3){
                    buffer[k] = if(binEqv.get(p) == '0')0 else 1
                    k+=1
                }
            }


        }

        var tempans = binaryToBase64(buffer)
        ans = ans+tempans
        i = i+3
    }

    return ans
}

fun binaryToBase64(buffer:Array<Int>):String{
    var indices = arrayOf(0,6)
    var base64  = listOf('A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','0','1','2','3','4','5','6','7','8','9','+','/')
    var ans = ""
    for(i in indices){
        var base = 32
        var index = 0
        for ( j in i..i+5){
            if(buffer[j] == 1)
                index += base
            base = base/2
        }

        ans = ans+base64[index]

    }

    return ans
}