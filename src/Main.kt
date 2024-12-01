fun main() {
    var int = 129

    var up = int shr 4
    var down = 15 and int

    int = 139

    for(i in 0..3){
        var j = int and 1
        int = int shr 1
        print(j)
    }

    println()

    var str = "abcdefgh"
    println(str.substring(0,4))
    println(str.substring(4,10))
}