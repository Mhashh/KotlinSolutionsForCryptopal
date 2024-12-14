package set1
// 8. Detect AES in ECB mode

import java.io.File

fun main(){
    var textSet:MutableSet<String> = mutableSetOf()
    var file = File("data8.txt")
    var linenumber = 1
    file.forEachLine {
        //println(it.length) 10 16byte string
        var column = 1
        var n = it.length

        var i = 0
        while (i<n){
            var byte16 = it.substring(i,i+32)
            if(byte16 in textSet){
                println("AES DETECTED $linenumber $column")
                println(byte16)
            }else{
                textSet.add(byte16)
            }
            column+=32
            i+=32
        }

        linenumber+=1

    }
}