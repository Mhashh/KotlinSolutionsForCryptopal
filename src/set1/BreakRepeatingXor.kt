package set1

import java.io.File

fun main(){
    var ans=""
    var file = File("data6.txt")

    file.forEachLine{

        var msg = decodeBase64(it)
        //println(msg)
        ans = ans+msg

    }

    //in terms of char
    var keySize = hammingDistanceScores(ans)
    println("Probably size is $keySize")

    var key = repeatingXorKey(ans,keySize)

    var decrypted = encryptXOR(ans,key)
    println("Decrypted:")
    println(decrypted)
}

fun base64MapCreator():MutableMap<Char,Int>{
    var map : MutableMap<Char,Int> = mutableMapOf()
    var base64  = listOf('A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','0','1','2','3','4','5','6','7','8','9','+','/')

    for(i in 0..63){
        map.put(base64.get(i),i)
    }

    return map
}
//https://base64.guru/learn/base64-algorithm/decode
fun decodeBase64(msg:String):String{
    var base64indices : Map<Char,Int> = base64MapCreator()
    var ans = ""
    var n = msg.length
    var buffer:Array<Int> = arrayOf(0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0)

    var i = 0
    while(i < n){

        var firstbit = 0
        for(j in 0..3){

            var char = msg.get(i+j)

            if(char in base64indices) {
                var byte = base64indices[char]!!.toInt()
                var k = firstbit + 5
                while (k >= firstbit) {
                    var bit = byte and 1
                    buffer[k] = bit
                    byte = byte shr 1
                    k--
                }
            }
            else{
                var k = firstbit + 5
                while (k >= firstbit) {
                    buffer[k] = 0
                    k--
                }
            }


            firstbit+=6
        }

        var tempstr = bufferToAscii(buffer)
        ans = ans+tempstr


        i+=4
    }
    return ans
}

fun bufferToAscii(buffer:Array<Int>):String{
    var ans = ""
    var indices = arrayOf(0,8,16)

    for( i in indices){
        var ascii = 0
        var powertwo = 128
        for( j in 0..7){
            ascii = ascii + buffer[i+j]*powertwo

            powertwo = powertwo/2
        }

        ans = ans+ascii.toChar()

    }
    return ans
}

fun hammingDistance(str1:String,str2:String):Int{
    var n = str1.length

    if(n!=str2.length){
        return -1
    }
    var score = 0

    var j=0

    for(char1 in str1){
        var char2 = str2.get(j)

        var d1 = char1.code
        var d2 = char2.code

        var xorValue = d1 xor d2

        //counting 1
        for(k in 0..7){
            if ((xorValue and 1) == 1){
                score +=1
            }
            xorValue = xorValue shr 1
        }

        j+=1
    }
    return score
}

fun hammingDistanceScores(str:String):Int{
    var probableKeySizes = arrayOf(100,100,100,100,100,100)
    var scores = arrayOf(100.0,100.0,100.0,100.0,100.0,100.0)
    var keyi = 0


    for(keysize in 2..40){

        var i = 0
        var times = 0
        var currentscore:Double = 0.0
        while(i+keysize+keysize<=str.length) {
            var str1 = str.substring(i, i + keysize)
            var str2 = str.substring(i + keysize, i + keysize + keysize)

            currentscore = currentscore + hammingDistance(str1,str2).toDouble()/keysize
            times+=1
            i+=keysize
        }




        var tempscore = currentscore/times.toDouble()
        if(keyi == 0){
            scores[keyi] = tempscore
            probableKeySizes[keyi] = keysize
            keyi+=1
        }
        else{
            var insertHere = keyi-1
            while(insertHere>=0 && scores[insertHere]>tempscore){
                insertHere-=1
            }

            var shifti = 4
            while(shifti>insertHere){
                scores[shifti+1] = scores[shifti]
                probableKeySizes[shifti+1] = probableKeySizes[shifti]
                shifti-=1
            }
            if(insertHere<5) {
                scores[insertHere + 1] = tempscore
                probableKeySizes[insertHere + 1] = keysize
            }
            if(keyi < 6)
                keyi+=1

        }

    }
    print("Probable Key lengths : ")
    for(j in probableKeySizes){
        print("$j ")
    }
    println()

    print("Probable Key scores  : ")
    for(j in scores){
        print("$j ")
    }
    println()
    return probableKeySizes[0]
}

fun decryptSingleKeyXorAscii(msg:String,key:Int):String{
    var ans=""

    for(char in msg){
        var d = char.code
        var extract  = d xor key
        ans = ans+extract.toChar()
    }

    return ans
}

fun frequencyScore(msg:String):Double{
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

fun repeatingXorKey(msg:String,keysize:Int):String{
    var n = msg.length
    var rowlength = (n/keysize) + (if(n%keysize > 0) 1 else 0)
    var lengths = Array<Int>(keysize,{it->0})
    var blocks = Array<StringBuilder>(rowlength,{StringBuilder(rowlength)})
    var key = ""

    //transpose
    for(i in 0..keysize-1){
        var j = i
        var times = 0
        while(j<n){
            blocks[i].append(msg.get(j))
            times++
            j+=keysize
        }
        lengths[i] = times
    }

    for(i in 0..keysize-1){
        var targetrow = blocks[i].toString()
        var score = 100.0
        var currentKey = 0
        for(trykey in 0..255){
            var decrypted = decryptSingleKeyXorAscii(targetrow,trykey)
            var tempscore = frequencyScore(decrypted)
            if(tempscore<score){
                currentKey=trykey
                score = tempscore
            }
        }

        key = key+(currentKey.toChar())
    }

    println("Here's your key : $key")

    return key
}
