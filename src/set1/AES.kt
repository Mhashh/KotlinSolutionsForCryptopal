package set1

import kotlin.experimental.and
import kotlin.experimental.xor
import kotlin.math.exp

@OptIn(ExperimentalStdlibApi::class)
class AES(keyString: ByteArray) {

    lateinit var key:ByteArray

    init {
        this.key = keyString
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun cipher(message:ByteArray):ByteArray{
        var msgLength = message.size

        //to replace with error
        if(msgLength != 16 ){
            val empty = byteArrayOf()
            return empty
        }

        //keyexpansion
        var expandedKey:Array<ByteArray> = keyExpansion()

        var state:Array<ByteArray> = Array<ByteArray>(4,{
            byteArrayOf(0,0,0,0)
        })

        //input into 4x4 state

        for( i in 0..3){
            for( j in 0..3){
                state[i][j] = message[i+4*j]
            }
        }

        //total rounds
        var nr = 10

        //round counter
        var round:Int = 0

        //first addRoundKey
        addRoundKey(round,state,expandedKey)


        round+=1


        //1 to 9 rounds
        while(round<nr){

            subBytes(state)
            shiftRows(state)
            mixColumns(state)
            addRoundKey(round,state,expandedKey)

            round+=1
        }
        //10th round

        subBytes(state)
        shiftRows(state)
        addRoundKey(round,state, expandedKey)


        var encryptedMsg = ByteArray(16,{0})
        for(j in 0..3){
            for(i in 0..3){
                encryptedMsg[4*j+i]=state[i][j]
            }
        }
        return encryptedMsg
    }

    fun decipher(message: ByteArray):ByteArray{
        var msgLength = message.size

        //to replace with error
        if(msgLength != 16 ){
            val empty = byteArrayOf()
            return empty
        }

        //keyexpansion
        var expandedKey:Array<ByteArray> = keyExpansion()

        var state:Array<ByteArray> = Array<ByteArray>(4,{
            byteArrayOf(0,0,0,0)
        })

        //input into 4x4 state

        for( i in 0..3){
            for( j in 0..3){
                state[i][j] = message[i+4*j]
            }
        }
        var nr = 10
        //round counter
        var round:Int = nr

        //round 10
        addRoundKey(round,state,expandedKey)
        round -=1
        //round 9 down to 1
        while(round>0){
            invShiftRows(state)
            invSubBytes(state)
            addRoundKey(round,state,expandedKey)
            invMixColumns(state)
            round-=1
        }
        //round 0
        invShiftRows(state)
        invSubBytes(state)
        addRoundKey(round,state,expandedKey)

        var encryptedMsg = ByteArray(16,{0})
        for(j in 0..3){
            for(i in 0..3){
                encryptedMsg[4*j+i]=state[i][j]
            }
        }
        return encryptedMsg

    }

    //CIPHER FUNCTIONS START
    private fun subBytes(state:Array<ByteArray>){
        var sbox : Array<ByteArray> = arrayOf(
            byteArrayOf(0x63.toByte(),0x7c.toByte(),0x77.toByte(),0x7b.toByte(),0xf2.toByte(),0x6b.toByte(),0x6f.toByte(),0xc5.toByte(),0x30.toByte(),0x01.toByte(),0x67.toByte(),0x2b.toByte(),0xfe.toByte(),0xd7.toByte(),0xab.toByte(),0x76.toByte()),
            byteArrayOf(0xca.toByte(),0x82.toByte(),0xc9.toByte(),0x7d.toByte(),0xfa.toByte(),0x59.toByte(),0x47.toByte(),0xf0.toByte(),0xad.toByte(),0xd4.toByte(),0xa2.toByte(),0xaf.toByte(),0x9c.toByte(),0xa4.toByte(),0x72.toByte(),0xc0.toByte()),
            byteArrayOf(0xb7.toByte(),0xfd.toByte(),0x93.toByte(),0x26.toByte(),0x36.toByte(),0x3f.toByte(),0xf7.toByte(),0xcc.toByte(),0x34.toByte(),0xa5.toByte(),0xe5.toByte(),0xf1.toByte(),0x71.toByte(),0xd8.toByte(),0x31.toByte(),0x15.toByte()),
            byteArrayOf(0x04.toByte(),0xc7.toByte(),0x23.toByte(),0xc3.toByte(),0x18.toByte(),0x96.toByte(),0x05.toByte(),0x9a.toByte(),0x07.toByte(),0x12.toByte(),0x80.toByte(),0xe2.toByte(),0xeb.toByte(),0x27.toByte(),0xb2.toByte(),0x75.toByte()),
            byteArrayOf(0x09.toByte(),0x83.toByte(),0x2c.toByte(),0x1a.toByte(),0x1b.toByte(),0x6e.toByte(),0x5a.toByte(),0xa0.toByte(),0x52.toByte(),0x3b.toByte(),0xd6.toByte(),0xb3.toByte(),0x29.toByte(),0xe3.toByte(),0x2f.toByte(),0x84.toByte()),
            byteArrayOf(0x53.toByte(),0xd1.toByte(),0x00.toByte(),0xed.toByte(),0x20.toByte(),0xfc.toByte(),0xb1.toByte(),0x5b.toByte(),0x6a.toByte(),0xcb.toByte(),0xbe.toByte(),0x39.toByte(),0x4a.toByte(),0x4c.toByte(),0x58.toByte(),0xcf.toByte() ),
            byteArrayOf(0xd0.toByte(),0xef.toByte(),0xaa.toByte(),0xfb.toByte(),0x43.toByte(),0x4d.toByte(),0x33.toByte(),0x85.toByte(),0x45.toByte(),0xf9.toByte(),0x02.toByte(),0x7f.toByte(),0x50.toByte(),0x3c.toByte(),0x9f.toByte(),0xa8.toByte()),
            byteArrayOf(0x51.toByte(),0xa3.toByte(),0x40.toByte(),0x8f.toByte(),0x92.toByte(),0x9d.toByte(),0x38.toByte(),0xf5.toByte(),0xbc.toByte(),0xb6.toByte(),0xda.toByte(),0x21.toByte(),0x10.toByte(),0xff.toByte(),0xf3.toByte(),0xd2.toByte()),

            byteArrayOf(0xcd.toByte(),0x0c.toByte(),0x13.toByte(),0xec.toByte(),0x5f.toByte(),0x97.toByte(),0x44.toByte(),0x17.toByte(),0xc4.toByte(),0xa7.toByte(),0x7e.toByte(),0x3d.toByte(),0x64.toByte(),0x5d.toByte(),0x19.toByte(),0x73.toByte()),
            byteArrayOf(0x60.toByte(),0x81.toByte(),0x4f.toByte(),0xdc.toByte(),0x22.toByte(),0x2a.toByte(),0x90.toByte(),0x88.toByte(),0x46.toByte(),0xee.toByte(),0xb8.toByte(),0x14.toByte(),0xde.toByte(),0x5e.toByte(),0x0b.toByte(),0xdb.toByte()),
            byteArrayOf(0xe0.toByte(),0x32.toByte(),0x3a.toByte(),0x0a.toByte(),0x49.toByte(),0x06.toByte(),0x24.toByte(),0x5c.toByte(),0xc2.toByte(),0xd3.toByte(),0xac.toByte(),0x62.toByte(),0x91.toByte(),0x95.toByte(),0xe4.toByte(),0x79.toByte()),
            byteArrayOf(0xe7.toByte(),0xc8.toByte(),0x37.toByte(),0x6d.toByte(),0x8d.toByte(),0xd5.toByte(),0x4e.toByte(),0xa9.toByte(),0x6c.toByte(),0x56.toByte(),0xf4.toByte(),0xea.toByte(),0x65.toByte(),0x7a.toByte(),0xae.toByte(),0x08.toByte()),
            byteArrayOf(0xba.toByte(),0x78.toByte(),0x25.toByte(),0x2e.toByte(),0x1c.toByte(),0xa6.toByte(),0xb4.toByte(),0xc6.toByte(),0xe8.toByte(),0xdd.toByte(),0x74.toByte(),0x1f.toByte(),0x4b.toByte(),0xbd.toByte(),0x8b.toByte(),0x8a.toByte()),
            byteArrayOf(0x70.toByte(),0x3e.toByte(),0xb5.toByte(),0x66.toByte(),0x48.toByte(),0x03.toByte(),0xf6.toByte(),0x0e.toByte(),0x61.toByte(),0x35.toByte(),0x57.toByte(),0xb9.toByte(),0x86.toByte(),0xc1.toByte(),0x1d.toByte(),0x9e.toByte()),
            byteArrayOf(0xe1.toByte(),0xf8.toByte(),0x98.toByte(),0x11.toByte(),0x69.toByte(),0xd9.toByte(),0x8e.toByte(),0x94.toByte(),0x9b.toByte(),0x1e.toByte(),0x87.toByte(),0xe9.toByte(),0xce.toByte(),0x55.toByte(),0x28.toByte(),0xdf.toByte()),
            byteArrayOf(0x8c.toByte(),0xa1.toByte(),0x89.toByte(),0x0d.toByte(),0xbf.toByte(),0xe6.toByte(),0x42.toByte(),0x68.toByte(),0x41.toByte(),0x99.toByte(),0x2d.toByte(),0x0f.toByte(),0xb0.toByte(),0x54.toByte(),0xbb.toByte(),0x16.toByte()),
        )

        for( i in 0..3){
            for( j in 0..3){

                var currentByte = state[i][j]

                var row = currentByte.rotateRight(4).and(0b00001111).toInt()
                var col = currentByte.and(0b00001111).toInt()

                state[i][j] = sbox[row][col]
            }
        }
    }

    private fun shiftRows(state:Array<ByteArray>){
        for( i in 1..3){

            var tempArray = ByteArray(4,{state[i][it]})
            for( j in 0..3){
                state[i][j] = tempArray[(i+j)%4]
            }
        }
    }

    private fun mixColumns(state:Array<ByteArray>){

        for(c in 0..3){
            var s1 = gfMul(0x02,state[0][c]).xor(gfMul(0x03,state[1][c])).xor(state[2][c]).xor(state[3][c])
            var s2 = state[0][c].xor(gfMul(0x02,state[1][c])).xor(gfMul(0x03,state[2][c])).xor(state[3][c])
            var s3 = state[0][c].xor(state[1][c]).xor(gfMul(0x02,state[2][c])).xor(gfMul(0x03,state[3][c]))
            var s4 = gfMul(0x03,state[0][c]).xor(state[1][c]).xor(state[2][c]).xor(gfMul(0x02,state[3][c]))

            state[0][c] = s1
            state[1][c] = s2
            state[2][c] = s3
            state[3][c] = s4

        }
    }

    private fun addRoundKey(round:Int,state: Array<ByteArray>, w:Array<ByteArray>){
        for( j in 0..3){
            for(i in 0..3){
                state[i][j] = state[i][j].xor(w[4*round+j][i])
            }
        }
    }

    private fun subWord(state:ByteArray):ByteArray{
        var sbox : Array<ByteArray> = arrayOf(
            byteArrayOf(0x63.toByte(),0x7c.toByte(),0x77.toByte(),0x7b.toByte(),0xf2.toByte(),0x6b.toByte(),0x6f.toByte(),0xc5.toByte(),0x30.toByte(),0x01.toByte(),0x67.toByte(),0x2b.toByte(),0xfe.toByte(),0xd7.toByte(),0xab.toByte(),0x76.toByte()),
            byteArrayOf(0xca.toByte(),0x82.toByte(),0xc9.toByte(),0x7d.toByte(),0xfa.toByte(),0x59.toByte(),0x47.toByte(),0xf0.toByte(),0xad.toByte(),0xd4.toByte(),0xa2.toByte(),0xaf.toByte(),0x9c.toByte(),0xa4.toByte(),0x72.toByte(),0xc0.toByte()),
            byteArrayOf(0xb7.toByte(),0xfd.toByte(),0x93.toByte(),0x26.toByte(),0x36.toByte(),0x3f.toByte(),0xf7.toByte(),0xcc.toByte(),0x34.toByte(),0xa5.toByte(),0xe5.toByte(),0xf1.toByte(),0x71.toByte(),0xd8.toByte(),0x31.toByte(),0x15.toByte()),
            byteArrayOf(0x04.toByte(),0xc7.toByte(),0x23.toByte(),0xc3.toByte(),0x18.toByte(),0x96.toByte(),0x05.toByte(),0x9a.toByte(),0x07.toByte(),0x12.toByte(),0x80.toByte(),0xe2.toByte(),0xeb.toByte(),0x27.toByte(),0xb2.toByte(),0x75.toByte()),
            byteArrayOf(0x09.toByte(),0x83.toByte(),0x2c.toByte(),0x1a.toByte(),0x1b.toByte(),0x6e.toByte(),0x5a.toByte(),0xa0.toByte(),0x52.toByte(),0x3b.toByte(),0xd6.toByte(),0xb3.toByte(),0x29.toByte(),0xe3.toByte(),0x2f.toByte(),0x84.toByte()),
            byteArrayOf(0x53.toByte(),0xd1.toByte(),0x00.toByte(),0xed.toByte(),0x20.toByte(),0xfc.toByte(),0xb1.toByte(),0x5b.toByte(),0x6a.toByte(),0xcb.toByte(),0xbe.toByte(),0x39.toByte(),0x4a.toByte(),0x4c.toByte(),0x58.toByte(),0xcf.toByte() ),
            byteArrayOf(0xd0.toByte(),0xef.toByte(),0xaa.toByte(),0xfb.toByte(),0x43.toByte(),0x4d.toByte(),0x33.toByte(),0x85.toByte(),0x45.toByte(),0xf9.toByte(),0x02.toByte(),0x7f.toByte(),0x50.toByte(),0x3c.toByte(),0x9f.toByte(),0xa8.toByte()),
            byteArrayOf(0x51.toByte(),0xa3.toByte(),0x40.toByte(),0x8f.toByte(),0x92.toByte(),0x9d.toByte(),0x38.toByte(),0xf5.toByte(),0xbc.toByte(),0xb6.toByte(),0xda.toByte(),0x21.toByte(),0x10.toByte(),0xff.toByte(),0xf3.toByte(),0xd2.toByte()),

            byteArrayOf(0xcd.toByte(),0x0c.toByte(),0x13.toByte(),0xec.toByte(),0x5f.toByte(),0x97.toByte(),0x44.toByte(),0x17.toByte(),0xc4.toByte(),0xa7.toByte(),0x7e.toByte(),0x3d.toByte(),0x64.toByte(),0x5d.toByte(),0x19.toByte(),0x73.toByte()),
            byteArrayOf(0x60.toByte(),0x81.toByte(),0x4f.toByte(),0xdc.toByte(),0x22.toByte(),0x2a.toByte(),0x90.toByte(),0x88.toByte(),0x46.toByte(),0xee.toByte(),0xb8.toByte(),0x14.toByte(),0xde.toByte(),0x5e.toByte(),0x0b.toByte(),0xdb.toByte()),
            byteArrayOf(0xe0.toByte(),0x32.toByte(),0x3a.toByte(),0x0a.toByte(),0x49.toByte(),0x06.toByte(),0x24.toByte(),0x5c.toByte(),0xc2.toByte(),0xd3.toByte(),0xac.toByte(),0x62.toByte(),0x91.toByte(),0x95.toByte(),0xe4.toByte(),0x79.toByte()),
            byteArrayOf(0xe7.toByte(),0xc8.toByte(),0x37.toByte(),0x6d.toByte(),0x8d.toByte(),0xd5.toByte(),0x4e.toByte(),0xa9.toByte(),0x6c.toByte(),0x56.toByte(),0xf4.toByte(),0xea.toByte(),0x65.toByte(),0x7a.toByte(),0xae.toByte(),0x08.toByte()),
            byteArrayOf(0xba.toByte(),0x78.toByte(),0x25.toByte(),0x2e.toByte(),0x1c.toByte(),0xa6.toByte(),0xb4.toByte(),0xc6.toByte(),0xe8.toByte(),0xdd.toByte(),0x74.toByte(),0x1f.toByte(),0x4b.toByte(),0xbd.toByte(),0x8b.toByte(),0x8a.toByte()),
            byteArrayOf(0x70.toByte(),0x3e.toByte(),0xb5.toByte(),0x66.toByte(),0x48.toByte(),0x03.toByte(),0xf6.toByte(),0x0e.toByte(),0x61.toByte(),0x35.toByte(),0x57.toByte(),0xb9.toByte(),0x86.toByte(),0xc1.toByte(),0x1d.toByte(),0x9e.toByte()),
            byteArrayOf(0xe1.toByte(),0xf8.toByte(),0x98.toByte(),0x11.toByte(),0x69.toByte(),0xd9.toByte(),0x8e.toByte(),0x94.toByte(),0x9b.toByte(),0x1e.toByte(),0x87.toByte(),0xe9.toByte(),0xce.toByte(),0x55.toByte(),0x28.toByte(),0xdf.toByte()),
            byteArrayOf(0x8c.toByte(),0xa1.toByte(),0x89.toByte(),0x0d.toByte(),0xbf.toByte(),0xe6.toByte(),0x42.toByte(),0x68.toByte(),0x41.toByte(),0x99.toByte(),0x2d.toByte(),0x0f.toByte(),0xb0.toByte(),0x54.toByte(),0xbb.toByte(),0x16.toByte()),
        )

        for( i in 0..3){

            var currentByte = state[i]

            var row = currentByte.rotateRight(4).and(0b00001111).toInt()
            var col = currentByte.and(0b00001111).toInt()

            state[i] = sbox[row][col]

        }
        return state
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun keyExpansion():Array<ByteArray>{
        var w : Array<ByteArray> = Array<ByteArray>(44,{
            byteArrayOf(0,0,0,0)
        })

        var temp :ByteArray = byteArrayOf(0,0,0,0)

        for( i in 0..3){
            for(j in 0..3) {
                w[i][j] = key[4 * i + j]
            }

        }

        for(i in 4..43){
            //previous word
            for(j in 0..3) {
                temp[j] = w[i-1][j]
            }

            if(i%4 == 0){
                rconXor(subWord(rotWord(temp)),i)
            }
            xorWord(temp,w[i-4])

            for(j in 0..3){
                w[i][j] = temp[j]
            }
        }
        return w

    }

    //CIPHER FUNCTIONS END

    //DECIPHER FUNCTION START
    private fun invShiftRows(state: Array<ByteArray>){
        for( i in 1..3){

            var tempArray = ByteArray(4,{state[i][it]})
            for( j in 0..3){
                var mod = (j-i)%4
                state[i][j] = tempArray[if(mod<0) 4+mod else mod]
            }
        }
    }

    private fun invSubBytes(state: Array<ByteArray>){
        var invSbox : Array<ByteArray> = arrayOf(
            byteArrayOf(0x52.toByte(),0x09.toByte(),0x6a.toByte(),0xd5.toByte(),0x30.toByte(),0x36.toByte(),0xa5.toByte(),0x38.toByte(),0xbf.toByte(),0x40.toByte(),0xa3.toByte(),0x9e.toByte(),0x81.toByte(),0xf3.toByte(),0xd7.toByte(),0xfb.toByte()),
            byteArrayOf(0x7c.toByte(),0xe3.toByte(),0x39.toByte(),0x82.toByte(),0x9b.toByte(),0x2f.toByte(),0xff.toByte(),0x87.toByte(),0x34.toByte(),0x8e.toByte(),0x43.toByte(),0x44.toByte(),0xc4.toByte(),0xde.toByte(),0xe9.toByte(),0xcb.toByte()),
            byteArrayOf(0x54.toByte(),0x7b.toByte(),0x94.toByte(),0x32.toByte(),0xa6.toByte(),0xc2.toByte(),0x23.toByte(),0x3d.toByte(),0xee.toByte(),0x4c.toByte(),0x95.toByte(),0x0b.toByte(),0x42.toByte(),0xfa.toByte(),0xc3.toByte(),0x4e.toByte()),
            byteArrayOf(0x08.toByte(),0x2e.toByte(),0xa1.toByte(),0x66.toByte(),0x28.toByte(),0xd9.toByte(),0x24.toByte(),0xb2.toByte(),0x76.toByte(),0x5b.toByte(),0xa2.toByte(),0x49.toByte(),0x6d.toByte(),0x8b.toByte(),0xd1.toByte(),0x25.toByte()),
            byteArrayOf(0x72.toByte(),0xf8.toByte(),0xf6.toByte(),0x64.toByte(),0x86.toByte(),0x68.toByte(),0x98.toByte(),0x16.toByte(),0xd4.toByte(),0xa4.toByte(),0x5c.toByte(),0xcc.toByte(),0x5d.toByte(),0x65.toByte(),0xb6.toByte(),0x92.toByte()),
            byteArrayOf(0x6c.toByte(),0x70.toByte(),0x48.toByte(),0x50.toByte(),0xfd.toByte(),0xed.toByte(),0xb9.toByte(),0xda.toByte(),0x5e.toByte(),0x15.toByte(),0x46.toByte(),0x57.toByte(),0xa7.toByte(),0x8d.toByte(),0x9d.toByte(),0x84.toByte()),
            byteArrayOf(0x90.toByte(),0xd8.toByte(),0xab.toByte(),0x00.toByte(),0x8c.toByte(),0xbc.toByte(),0xd3.toByte(),0x0a.toByte(),0xf7.toByte(),0xe4.toByte(),0x58.toByte(),0x05.toByte(),0xb8.toByte(),0xb3.toByte(),0x45.toByte(),0x06.toByte()),
            byteArrayOf(0xd0.toByte(),0x2c.toByte(),0x1e.toByte(),0x8f.toByte(),0xca.toByte(),0x3f.toByte(),0x0f.toByte(),0x02.toByte(),0xc1.toByte(),0xaf.toByte(),0xbd.toByte(),0x03.toByte(),0x01.toByte(),0x13.toByte(),0x8a.toByte(),0x6b.toByte()),
            byteArrayOf(0x3a.toByte(),0x91.toByte(),0x11.toByte(),0x41.toByte(),0x4f.toByte(),0x67.toByte(),0xdc.toByte(),0xea.toByte(),0x97.toByte(),0xf2.toByte(),0xcf.toByte(),0xce.toByte(),0xf0.toByte(),0xb4.toByte(),0xe6.toByte(),0x73.toByte()),
            byteArrayOf(0x96.toByte(),0xac.toByte(),0x74.toByte(),0x22.toByte(),0xe7.toByte(),0xad.toByte(),0x35.toByte(),0x85.toByte(),0xe2.toByte(),0xf9.toByte(),0x37.toByte(),0xe8.toByte(),0x1c.toByte(),0x75.toByte(),0xdf.toByte(),0x6e.toByte()),
            byteArrayOf(0x47.toByte(),0xf1.toByte(),0x1a.toByte(),0x71.toByte(),0x1d.toByte(),0x29.toByte(),0xc5.toByte(),0x89.toByte(),0x6f.toByte(),0xb7.toByte(),0x62.toByte(),0x0e.toByte(),0xaa.toByte(),0x18.toByte(),0xbe.toByte(),0x1b.toByte()),
            byteArrayOf(0xfc.toByte(),0x56.toByte(),0x3e.toByte(),0x4b.toByte(),0xc6.toByte(),0xd2.toByte(),0x79.toByte(),0x20.toByte(),0x9a.toByte(),0xdb.toByte(),0xc0.toByte(),0xfe.toByte(),0x78.toByte(),0xcd.toByte(),0x5a.toByte(),0xf4.toByte()),
            byteArrayOf(0x1f.toByte(),0xdd.toByte(),0xa8.toByte(),0x33.toByte(),0x88.toByte(),0x07.toByte(),0xc7.toByte(),0x31.toByte(),0xb1.toByte(),0x12.toByte(),0x10.toByte(),0x59.toByte(),0x27.toByte(),0x80.toByte(),0xec.toByte(),0x5f.toByte()),
            byteArrayOf(0x60.toByte(),0x51.toByte(),0x7f.toByte(),0xa9.toByte(),0x19.toByte(),0xb5.toByte(),0x4a.toByte(),0x0d.toByte(),0x2d.toByte(),0xe5.toByte(),0x7a.toByte(),0x9f.toByte(),0x93.toByte(),0xc9.toByte(),0x9c.toByte(),0xef.toByte()),
            byteArrayOf(0xa0.toByte(),0xe0.toByte(),0x3b.toByte(),0x4d.toByte(),0xae.toByte(),0x2a.toByte(),0xf5.toByte(),0xb0.toByte(),0xc8.toByte(),0xeb.toByte(),0xbb.toByte(),0x3c.toByte(),0x83.toByte(),0x53.toByte(),0x99.toByte(),0x61.toByte()),
            byteArrayOf(0x17.toByte(),0x2b.toByte(),0x04.toByte(),0x7e.toByte(),0xba.toByte(),0x77.toByte(),0xd6.toByte(),0x26.toByte(),0xe1.toByte(),0x69.toByte(),0x14.toByte(),0x63.toByte(),0x55.toByte(),0x21.toByte(),0x0c.toByte(),0x7d.toByte()),
        )

        for( i in 0..3){
            for( j in 0..3){

                var currentByte = state[i][j]

                var row = currentByte.rotateRight(4).and(0b00001111).toInt()
                var col = currentByte.and(0b00001111).toInt()

                state[i][j] = invSbox[row][col]
            }
        }
    }

    private fun invMixColumns(state: Array<ByteArray>){
        for(c in 0..3){
            var s1 = gfMul(0x0e,state[0][c]).xor(gfMul(0x0b,state[1][c])).xor(gfMul(0x0d,state[2][c])).xor(gfMul(0x09,state[3][c]))
            var s2 = gfMul(0x09,state[0][c]).xor(gfMul(0x0e,state[1][c])).xor(gfMul(0x0b,state[2][c])).xor(gfMul(0x0d,state[3][c]))
            var s3 = gfMul(0x0d,state[0][c]).xor(gfMul(0x09,state[1][c])).xor(gfMul(0x0e,state[2][c])).xor(gfMul(0x0b,state[3][c]))
            var s4 = gfMul(0x0b,state[0][c]).xor(gfMul(0x0d,state[1][c])).xor(gfMul(0x09,state[2][c])).xor(gfMul(0x0e,state[3][c]))

            state[0][c] = s1
            state[1][c] = s2
            state[2][c] = s3
            state[3][c] = s4

        }
    }

    //DECIPHER FUNCTION END


    private fun gfMul(op1:Byte,op2:Byte):Byte{
        var product:Byte = 0

        var a = op1
        var b = op2
        var reduce = 0b00011011.toByte()

        for(i in 0..7){

            if(b.and(1) == 1.toByte()){
                product = product.xor(a)

            }

            var hasHighestPower = a.takeHighestOneBit().compareTo(0x80.toByte()) == 0
            a = a.rotateLeft(1).and(0b11111110.toByte())
            if(hasHighestPower){

                a = a.xor(reduce)
            }
            b = b.rotateRight(1).and(0b01111111.toByte())

        }
        return product
    }

    private fun rotWord(word:ByteArray):ByteArray{
        var rotatedWord = byteArrayOf(0,0,0,0)
        var lastByte = word[0]
        for(i in 0..2){
            word[i] = word[i+1]
        }

        word[3] = lastByte
        return word
    }

    private fun rconXor(temp:ByteArray,round:Int){
        var rcon:ByteArray = byteArrayOf(0x00,0x01,0x02,0x04,0x08,0x10,0x20,0x40, 0x80.toByte(),0x1B,0x36)

        var rconByte:Byte = rcon[round/4]

        temp[0] = temp[0].xor(rconByte)

    }

    private fun xorWord(temp:ByteArray,wi:ByteArray){
        for(i in 0..3){
            temp[i] = temp[i].xor(wi[i])
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun statePrint(state: Array<ByteArray>){

        for (i in 0..3){
            for(j in 0..3){

                print("${state[i][j].toHexString()} ")
            }
            println()
        }

    }

    companion object {
        const val classInfo = "Advanced Encryption Standard 128"
        
        fun asciiToByteArray(str:String):ByteArray{
            var n = str.length

            var byteArray = ByteArray(n, { it-> 0})
            var index = 0
            for(i in str){
                byteArray[index] = i.code.toByte()
                index++
            }
            return byteArray
        }

        fun byteArrayToAscii(msg:ByteArray):String{
            var n = msg.size

            var stringBuilder = StringBuilder(n)

            for(byte in msg){
                stringBuilder.append(byte.toInt().toChar())
            }
            return stringBuilder.toString()
        }
    }
}