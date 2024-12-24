# Kotlin solutions to cryptopals challenges. #

### Helpful code ###
 [Simple kotlin Implementation of AES 128](https://github.com/Mhashh/KotlinSolutionsForCryptopal/blob/master/src/set1/AES.kt)

### Set 1:Basics ###

| No. | Topic |
| ---- | ---- |
| 1  | [Convert hex to base64](https://github.com/Mhashh/KotlinSolutionsForCryptopal/blob/master/src/set1/Base64Encoder.kt) |
| 2  | [Fixed XOR](https://github.com/Mhashh/KotlinSolutionsForCryptopal/blob/master/src/set1/HexXOR.kt) |
| 3  | [Single-byte XOR cipher](https://github.com/Mhashh/KotlinSolutionsForCryptopal/blob/master/src/set1/XorCipher.kt) |
| 4  | [Detect single-character XOR](https://github.com/Mhashh/KotlinSolutionsForCryptopal/blob/master/src/set1/XORdetect.kt) |
| 5  | [Implement repeating-key XOR](https://github.com/Mhashh/KotlinSolutionsForCryptopal/blob/master/src/set1/RepeatingKeyXor.kt) |
| 6  | [Break repeating-key XOR](https://github.com/Mhashh/KotlinSolutionsForCryptopal/blob/master/src/set1/BreakRepeatingXor.kt) |
| 7  | [AES in ECB mode](https://github.com/Mhashh/KotlinSolutionsForCryptopal/blob/master/src/set1/AESECB.kt) |
| 8  | [Detect AES in ECB mode](https://github.com/Mhashh/KotlinSolutionsForCryptopal/blob/master/src/set1/DetectAES.kt) | 


### Set 2:Block crypto ###

| No. | Topic |
| ---- | ---- |
| 9 | [Implement PKCS#7 padding](https://github.com/Mhashh/KotlinSolutionsForCryptopal/blob/master/src/set2/PKCShashtag7.kt) | 
| 10 | [Implement CBC mode](https://github.com/Mhashh/KotlinSolutionsForCryptopal/blob/master/src/set2/CBCMode.kt) |
| 11 | [An ECB/CBC detection oracle](https://github.com/Mhashh/KotlinSolutionsForCryptopal/blob/master/src/set2/DetectionOracle.kt) |
| 12 | [Byte-at-a-time ECB decryption (Simple)](https://github.com/Mhashh/KotlinSolutionsForCryptopal/blob/master/src/set2/ECBDecryptionSimple.kt) |
| 13 | [ECB cut-and-paste](https://github.com/Mhashh/KotlinSolutionsForCryptopal/blob/master/src/set2/ECBcutpaste.kt) |
| 14 | [Byte-at-a-time ECB decryption (Harder)](https://github.com/Mhashh/KotlinSolutionsForCryptopal/blob/master/src/set2/ECBDecryptionHard.kt) |
| 15 | [PKCS#7 padding validation](https://github.com/Mhashh/KotlinSolutionsForCryptopal/blob/master/src/set2/PKCSValidation.kt) |
| 16 | [CBC bitflipping attacks](https://github.com/Mhashh/KotlinSolutionsForCryptopal/blob/master/src/set2/CBCBitflipping.kt) |


### Set 3:Block & Stream crypto ###

| No. | Topic                                                                                                                                        |
|-----|----------------------------------------------------------------------------------------------------------------------------------------------|
| 17  | [The CBC padding oracle](https://github.com/Mhashh/KotlinSolutionsForCryptopal/blob/master/src/set3/CBCPadOrc.kt)                            | 
| 18  | [Implement CTR, the stream cipher mode](https://github.com/Mhashh/KotlinSolutionsForCryptopal/blob/master/src/set3/CTR.kt)                   |
| 19 | [Break fixed-nonce CTR mode using substitutions](https://github.com/Mhashh/KotlinSolutionsForCryptopal/blob/master/src/set3/BreakCtrSub.kt)  |
| 20 | [Break fixed-nonce CTR statistically](https://github.com/Mhashh/KotlinSolutionsForCryptopal/blob/master/src/set3/BreakCtrStats.kt) |