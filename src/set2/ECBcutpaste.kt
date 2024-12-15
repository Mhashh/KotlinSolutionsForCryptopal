package set2
// 13. ECB cut-and-paste

import set1.AES
import set1.AesECBdecrypt
import set1.AesECBencrypt

class Global2{
    companion object{
        var key = random128key()
    }
}

fun main(){
    var attackString = "joke@four."+ pkcsPadding("admin",16) +"com"

    var encryptedCookie = profileEncrypt(attackString)

    var attackCookie = ByteArray(48)

    //first block in first block
    for (i in 0..15){
        attackCookie[i] = encryptedCookie[i]
    }
    //third block in second block
    for (i in 0..15){
        attackCookie[16+i] = encryptedCookie[32+i]
    }

    for (i in 0..15){
        attackCookie[32+i] = encryptedCookie[16+i]
    }

    println(profileDecrypt(attackCookie))
}

fun profileParse(queryString: String):MutableMap<String,String>{
    var properties = queryString.split('&')

    var cookie:MutableMap<String,String> = mutableMapOf()
    for(property in properties){
        var propertyExpanded = property.split('=')
        cookie.put(propertyExpanded[0],propertyExpanded[1])
    }

    return cookie

}

fun profileString(data : MutableMap<String,String>):String{
    var cookieStringBuilder:StringBuilder = StringBuilder()

    var keys = data.keys

    for(key in keys){
        cookieStringBuilder.append(key).append("=").append(data[key]).append('&')

    }

    cookieStringBuilder.delete(cookieStringBuilder.length-1,cookieStringBuilder.length)
    return cookieStringBuilder.toString()
}

fun profileFor(email:String):String{
    var emailSanitized = email.replace("&","").replace("=","")
    var cookie:MutableMap<String,String> = mutableMapOf()

    cookie.put("email",emailSanitized)
    cookie.put("uid","10")
    cookie.put("role","user")
    return profileString(cookie)
}

fun profileEncrypt(email: String):ByteArray{
    var key = Global2.key
    var cookieString = profileFor(email)
    var data = pkcsPadding(cookieString,16)
    var dataBytes = AES.asciiToByteArray(data)

    return AesECBencrypt(dataBytes,key)
}

fun profileDecrypt(encrypted:ByteArray):String{
    var key = Global2.key
    var decryptedData = AesECBdecrypt(encrypted,key)
    var cookie = pkcsStrip( AES.byteArrayToAscii(decryptedData),16)

    return cookie
}