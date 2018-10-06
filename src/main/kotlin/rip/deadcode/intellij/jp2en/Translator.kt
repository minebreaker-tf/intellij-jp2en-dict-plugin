package rip.deadcode.intellij.jp2en

import com.google.api.client.http.GenericUrl
import com.google.api.client.http.HttpResponseException
import com.google.api.client.http.HttpTransport
import java.io.IOException

object Translator {

    // Thanks for https://www.est.co.jp/dev/dict/REST
    const val listUrl = "http://public.dejizo.jp/NetDicV09.asmx/SearchDicItemLite"
    const val itemUrl = "http://public.dejizo.jp/NetDicV09.asmx/GetDicItemLite"

    fun translate(httpTransport: HttpTransport, word: String): String? {

        if (word.isEmpty()) {
            return null
        }

        try {
            val itemList = httpTransport.createRequestFactory()
                    .buildGetRequest(GenericUrl(listUrl).also {
                        it["Dic"] = "EdictJE"
                        it["Word"] = word
                        it["Scope"] = "HEADWORD"
                        it["Match"] = "EXACT"
                        it["Merge"] = "AND"
                        it["Prof"] = "XHTML"
                        it["PageSize"] = 1
                        it["PageIndex"] = 0
                    })
                    .execute()
                    .parseAsString()
            val itemId = extractId(itemList) ?: return null

            val itemResult = httpTransport.createRequestFactory()
                    .buildGetRequest(GenericUrl(itemUrl).also {
                        it["Dic"] = "EdictJE"
                        it["Item"] = itemId
                        it["Loc"] = 0
                        it["Prof"] = "XHTML"
                    })
                    .execute()
                    .parseAsString()
            return extractItem(itemResult)

        } catch (e: HttpResponseException) {
            return "<p>Unexpected HTTP error.</p>"
        } catch (e: IOException) {
            return "<p>Failed to connect. The server seems to be down.</p>"
        }
    }

    // Use regex because XMLObjectParser fails to parse xml response returned

    private val regList = Regex("<ItemID>(.*)</ItemID>")
    fun extractId(response: String): String? {
        val result = regList.find(response.removeLinebreak())
        return if (result != null && result.groupValues.size >= 2) {
            result.groupValues[1].trim()
        } else {
            null
        }
    }

    private val regItem = Regex("<Body>(.*)</Body>")
    fun extractItem(response: String): String? {
        val result = regItem.find(response.removeLinebreak())
        return if (result != null && result.groupValues.size >= 2) {
            result.groupValues[1].trim()
        } else {
            null
        }
    }

    internal fun String.removeLinebreak() = this.replace("\r", "").replace("\n", "").trim()
}
