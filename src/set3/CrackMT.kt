package set3
// 22. Crack an MT19937 seed

import kotlin.random.Random
import set3.MT19937_32

fun main(){
    println("Function Called ")
    var randomValue:UInt = randomNumber()

    println("Random Value : $randomValue")

    //milliseconds is in long, so passing seed in terms of seconds(32 bit uint)
    var currentTime = (System.currentTimeMillis()/1000).toUInt()
    var seed = 0u

    for( i in 35..1010){
        var rng = MT19937_32(currentTime-i.toUInt())
        var currentGuess = rng.random()
        if(currentGuess == randomValue){
            println("Seed found ")
            seed = currentTime-i.toUInt()
            break
        }
    }

    println("Seed : $seed")
    println("Double checking : ${MT19937_32(seed).random()}")
}

fun randomNumber():UInt{
    var secs = Random(System.currentTimeMillis()).nextInt(40,1001)
    var timeout = (secs*1000).toLong()
    println("Waiting for $secs secs..")
    Thread.sleep(timeout)

    //milliseconds is in long, so passing seed in terms of seconds(32 bit uint)
    var seed = (System.currentTimeMillis()/1000).toUInt()
    var rng = MT19937_32(seed)

    secs = Random(System.currentTimeMillis()).nextInt(40,1001)
    timeout = (secs*1000).toLong()
    println("Again waiting for $secs secs..")
    Thread.sleep(timeout)

    return rng.random()

}