package set1
// 2. Fixed XOR

fun main(){

    println("Input hex string one : ")
    var hex1 = readln()
    println("Input hex string two : ")
    var hex2 = readln()

    var ans = hexXor(hex1,hex2)
    println("XOR : $ans")
}


fun hexXor(hex1:String,hex2:String):String{
    var hexMap:Map<Char,Int> = mapOf('0' to 0,'1' to 1,'2' to 2,'3' to 3,'4' to 4,
        '5' to 5,'6' to 6,'7' to 7,'8' to 8,'9' to 9,'a' to 10,
        'b' to 11,'c' to 12,'d' to 13,'e' to 14,'f' to 15)

    val hexDecode = arrayOf('0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f')

    var ans = ""
    var n = hex1.length
    if( n != hex2.length)
        return ans

    for(i in 0..n-1){
        var c1 = hex1.get(i)
        var c2 = hex2.get(i)
        var d1 = hexMap.get(c1)!!.toInt()
        var d2 = hexMap[c2]!!.toInt()

        var d = d1 xor d2
        ans = ans+hexDecode[d]
    }

    return ans
}