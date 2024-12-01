import set1.AES
import kotlin.experimental.and

@OptIn(ExperimentalStdlibApi::class)
fun main(){

    var key = "0f1571c947d9e8590cb7add6af7f6798".hexToByteArray()
    var plaintext = "0123456789abcdeffedcba9876543210".hexToByteArray()

    println("ff0b844a0853bf7c6934ab4364148fb9 should be")
    var enc: AES = AES(key)
    var encmsg = enc.cipher(plaintext)
    println(encmsg.toHexString())


    var decmsg = enc.decipher(encmsg)
    println("0123456789abcdeffedcba9876543210 should be")
    println(decmsg.toHexString())

    var msg = "Hello my name is"
    var key2 = "There is my book"
    var msgBytes = AES.asciiToByteArray(key2)
    var keyBytes = AES.asciiToByteArray(msg)

    var enc2 = AES(keyBytes)

    var encmsg2 = enc2.cipher(msgBytes)

    var decmsg2 = enc2.decipher(encmsg2)

    println(AES.byteArrayToAscii(decmsg2))
}