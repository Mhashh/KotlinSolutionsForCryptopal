import set1.AES
import set1.hammingDistance
import kotlin.experimental.and
import kotlin.random.Random

fun main(){
    var str1 = "this is a test"
    var str2 = "wokka wokka!!!"

    var dis = hammingDistance(str1,str2)
    println(dis)

    println(AES.classInfo)

    //15 + 32 + 128 = 15 + 160 = 175
    var bit:Byte = 47.toByte()

    var firstFourBits = (bit.rotateRight(4))
    firstFourBits = firstFourBits.and(0b00001111)
    println(firstFourBits)
    println(bit.and(0b10000000.toByte()))

    println("Modulus check")
    var it = (0-1)%4
    println(it)

    var rndm = Random(1)
    println(rndm.nextInt(0,128))

    var wordSet:MutableSet<ByteArray> = mutableSetOf()

    var boo = byteArrayOf(1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1)
    wordSet.add(boo)

    var boo2 = ByteArray(16,{boo[it]})

    println(boo2 in wordSet)

    var a:Byte = 0xaa.toByte()
    var b:Byte = 0xac.toByte()
    var c:Byte = 0xaa.toByte()

    if(a==c)
        println("okay")

    if(b != c)
        println("okay")

    println("decode this message, oh no this message is too long".length)
}