package set3
//MT19937 32 bit rng implementation

class MTstate(n:Int,w:Int,f:UInt,seed:UInt){
    lateinit var stateArray : Array<UInt>
    var stateIndex : Int = 0

    init {
        stateArray = Array<UInt>(n,{0u})
        var tempseed = seed
        stateArray[0] = tempseed

        for( i in 1..(n-1)){
            tempseed = f*(tempseed xor (tempseed shr (w-2)))+i.toUInt()
            stateArray[i] = tempseed
        }
        stateIndex=0

    }

}
class MT19937_32(seed:UInt){
    private var n  = 624
    private var m = 397
    private var w = 32
    private var r = 31
    private var umask = (0xffffffffu shl r)
    private var lmask = (0xffffffffu shr (w-r))
    private var a = 0x9908b0dfu
    private var u = 11
    private var s = 7
    private var t = 15
    private var l = 18
    private var b = 0x9d2c5680U
    private var c = 0xefc60000U
    private var f = 1812433253u
    lateinit var state:MTstate

    init {
        state = MTstate(n,w,f,seed)
        twist()
    }

    fun set(index:Int,value:UInt){
        if(index >= 0 && index < n){
            state.stateArray[index] = value
        }
    }

    fun twist() {
        var stateArray = state.stateArray
        // point to current state location

        for(k in 0..n-1){
            // point to state n-1 iterations before, mod n
            var j = k - (n - 1)
            if (j < 0)
                j += n

            var x: UInt = (stateArray[k] and umask) or (stateArray[j] and lmask)
            var xA: UInt = x shr 1
            if ((x and 0x00000001u) == 1u)
                xA = xA xor a

            // point to state n-m iterations before, mod n
            j = k - (n - m)
            if (j < 0)
                j += n

            // compute next value in the state
            x = stateArray[j] xor xA
            // update new state value
            stateArray[k] = x
        }

        state.stateIndex=0

    }

    fun random():UInt{
        if(state.stateIndex == n){
            twist()
        }

        var x = state.stateArray[state.stateIndex]

        //tempering
        var y:UInt = x xor (x shr u)
        y = y xor ((y shl s) and b)
        y = y xor ((y shl t) and c)
        var z : UInt = y xor (y shr l)

        state.stateIndex = state.stateIndex+1

        return z
    }


}