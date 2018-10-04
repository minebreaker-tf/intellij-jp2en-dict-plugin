package rip.deadcode.intellij.jp2en

import com.google.api.client.http.HttpTransport
import com.google.api.client.http.LowLevelHttpRequest
import com.google.api.client.http.LowLevelHttpResponse
import com.google.api.client.testing.http.MockHttpTransport
import com.google.api.client.testing.http.MockLowLevelHttpRequest
import com.google.api.client.testing.http.MockLowLevelHttpResponse
import com.google.common.net.MediaType
import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import rip.deadcode.intellij.jp2en.Translator.extractId
import rip.deadcode.intellij.jp2en.Translator.extractItem
import rip.deadcode.intellij.jp2en.Translator.itemUrl
import rip.deadcode.intellij.jp2en.Translator.listUrl
import rip.deadcode.intellij.jp2en.Translator.removeLinebreak
import rip.deadcode.intellij.jp2en.Translator.translate

internal class TranslatorTest {

    private fun mockTransport(responseToReturn: (String) -> String): HttpTransport = object : MockHttpTransport() {
        override fun buildRequest(method: String, url: String): LowLevelHttpRequest {
            return object : MockLowLevelHttpRequest() {
                override fun execute(): LowLevelHttpResponse {
                    val response = MockLowLevelHttpResponse()
                    response.contentType = MediaType.XML_UTF_8.toString()
                    response.setContent(responseToReturn(url))
                    return response
                }
            }
        }
    }

    @Test
    fun testTranslate() {
        val result = translate(mockTransport {
            when {
                it.startsWith(listUrl) -> paramList
                it.startsWith(itemUrl) -> paramItem
                else -> fail("")
            }
        }, "株")
        assertThat(result).isEqualTo(expectedItem.removeLinebreak())
    }

    @Test
    fun testExtractId() {
        val result = extractId(paramList)
        assertThat(result).isEqualTo("033331")
    }

    @Test
    fun testExtractItem() {
        val result = extractItem(paramItem)
        assertThat(result).isEqualTo(expectedItem.removeLinebreak())
    }

    private val paramList = """<?xml version="1.0" encoding="utf-8"?>
<SearchDicItemResult xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns="http://btonic.est.co.jp/NetDic/NetDicV09">
    <ErrorMessage />
    <TotalHitCount>1</TotalHitCount>
    <ItemCount>1</ItemCount>
    <TitleList>
        <DicItemTitle>
            <ItemID>033331</ItemID>
            <LocID />
            <Title>
                <span class="NetDicTitle" xmlns="">株</span>
            </Title>
        </DicItemTitle>
    </TitleList>
</SearchDicItemResult>"""

    private val paramItem = """<?xml version="1.0" encoding="utf-8"?>
<GetDicItemResult xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns="http://btonic.est.co.jp/NetDic/NetDicV09">
    <ErrorMessage />
    <Head>
        <div class="NetDicHead" xml:space="preserve" xmlns="">
            <span class="NetDicHeadTitle">株
				［かぶ］
			</span>
        </div>
    </Head>
    <Body>
        <div class="NetDicBody" xml:space="preserve" xmlns="">
            <div>
                <div>(n) share</div>
                <div>stock</div>
                <div>stump (of tree)</div>
                <div>(P)</div>
                <div>
        </div>
            </div>
        </div>
    </Body>
</GetDicItemResult>"""

    private val expectedItem = """<div class="NetDicBody" xml:space="preserve" xmlns="">
            <div>
                <div>(n) share</div>
                <div>stock</div>
                <div>stump (of tree)</div>
                <div>(P)</div>
                <div>
        </div>
            </div>
        </div>"""
}
