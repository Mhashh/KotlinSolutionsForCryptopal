package set1
// 4. Detect single-character XOR

import java.io.File

fun main(){
    var file = File("data.txt")
    var score= 100.0
    var ans = ""
    file.forEachLine{

        for(key in 0..255){
            var msg = decryptXor(it,key)
            var tempscore = score(msg)
            if(tempscore< score){
                score=tempscore
                ans = msg
            }
        }

    }
    println(ans)
}

fun decryptXor(hex1:String,key:Int):String{
    var hexMap:Map<Char,Int> = mapOf('0' to 0,'1' to 1,'2' to 2,'3' to 3,'4' to 4,
        '5' to 5,'6' to 6,'7' to 7,'8' to 8,'9' to 9,'a' to 10,
        'b' to 11,'c' to 12,'d' to 13,'e' to 14,'f' to 15)


    var ans = ""
    var n = hex1.length
    var i = 0
    while(i < n){
        var c1 = hex1.get(i)
        var c2 = hex1.get(i+1)
        var d1 = hexMap.get(c1)!!.toInt()
        var d2 = hexMap[c2]!!.toInt()
        //print("${c1} ${d1} ${c2} ${d2} ")
        d1 = d1*16 + d2
        var d = d1 xor key
        ans = ans+d.toChar()
        //println("${d1} XOR ${key} = ${d}")
        i=i+2
    }

    return ans
}

fun score(msg:String):Double{
    var frequencyMap = arrayOf(8.2,1.5,2.8,4.3,12.7,2.2,2.0,6.1,7.0,0.15,0.77,4.0,2.4,6.7,7.5,1.9,0.095,6.0,6.3,9.1,2.8,0.98,2.4,0.15,2.0,0.074)
    var score = 0.0
    var alphabet = Array<Double>(26,init = {it->0.0 })
    var n = 0.0
    for(i in msg){
        /*
        if(i.code in 65..90){
            alphabet[i.code-65] = alphabet[i.code-65]+1
            n +=1
        }


        else*/ if(i.code in 97..122){
            alphabet[i.code-97] = alphabet[i.code-97]+1
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