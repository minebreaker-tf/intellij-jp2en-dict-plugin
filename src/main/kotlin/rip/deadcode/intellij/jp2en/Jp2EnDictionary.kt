package rip.deadcode.intellij.jp2en

import com.google.api.client.http.javanet.NetHttpTransport
import rip.deadcode.intellij.dictionary.Dictionary

class Jp2EnDictionary : Dictionary {

    private val defaultHttpTransport = NetHttpTransport()

    override fun canHandle(prefix: String): Boolean = prefix == "j2e"

    override fun getDisplayName(): String = "Jp2En Dictionary"

    override fun lookUp(word: String): String? {
        return Translator.translate(defaultHttpTransport, word)
    }
}