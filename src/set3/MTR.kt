package set3
// 21. Implement the MT19937 Mersenne Twister RNG

import set3.MT19937_32


fun main(){
    val mt : MT19937_32 = MT19937_32(100u)
    for( i in 0..5)
        println(mt.random())
}