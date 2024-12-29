package set3
// 23. Clone an MT19937 RNG from its output

fun main(){
    val n = 624
    val mt1 : MT19937_32 =MT19937_32(100u)
    val mt2 = MT19937_32(0u)
    for( i in 0..n-1){
        val draw = mt1.random()
        val stateVal = untemper(draw)


        mt2.set(i,stateVal)
    }
    mt2.twist()

    for(i in 0..10){
        println("$i original : ${mt1.random()}")
        println("$i cloned : ${mt2.random()}")
    }
}

//https://blog.ollien.com/posts/reverse-mersenne-twister/
fun reverseRightShift(value:UInt,shift:Int):UInt{
    val w = 32
    var ans = value
    var mask = 0u

    //creates uint with 1s shift times
    //eg shift = 3, so 111
    for(i in 1..shift){
        mask = (mask shl 1) xor 1u
    }
    //need the 1s on the most significant side,then shift gradually to right
    mask  = mask shl (w-shift)

    for (i in 0..31 step shift){
        var selectedBits = mask and ans
        ans = ans xor (selectedBits shr shift)

        mask = mask shr shift
    }

    return ans
}

fun reverseLeftShiftAnd(value:UInt,shift:Int,mask:UInt):UInt{
    val w = 32
    var ans = value
    var extractor = 0u

    //creates uint with 1s shift times
    //eg shift = 3, so 111
    //here we shift the mask left side
    for(i in 1..shift)
        extractor = (extractor shl 1) xor 1u

    for(i in 0..31 step shift){
        var selectedBits = ans and extractor
        ans = ans xor ((selectedBits shl shift) and mask)
        extractor = extractor shl shift
    }

    return ans
}

fun untemper(value:UInt):UInt{
    var u = 11
    val s = 7
    val t = 15
    val l = 18
    val b = 0x9d2c5680U
    val c = 0xefc60000U

    var ans = value

    ans = reverseRightShift(ans,l)
    ans = reverseLeftShiftAnd(ans,t,c)
    ans = reverseLeftShiftAnd(ans,s,b)
    ans = reverseRightShift(ans,u)
    return ans
}