package com.local.amjbc.model

import org.apache.http.*
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.params.CoreConnectionPNames
import org.apache.http.params.HttpProtocolParams
import org.apache.http.util.EntityUtils
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.xml.sax.InputSource
import org.xml.sax.SAXException
import java.io.IOException
import java.io.StringReader
import java.io.UnsupportedEncodingException
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException

class XMLParser {

    fun getXmlFromUrl(url: String?): String? {
        var xml: String? = null
        val httpClient = DefaultHttpClient()
        val params = httpClient.params
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1)
        params.setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 3000)
        params.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 3000)
        val httpGet = HttpGet(url)
        try {
            val httpResponse = httpClient.execute(httpGet)
            val status = httpResponse.statusLine
            if (status.statusCode != 200) throw IOException("Invalid responce from server: " + status.toString())
            val httpEntity = httpResponse.entity
            xml = EntityUtils.toString(httpEntity)
            httpClient.connectionManager.shutdown()
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        } catch (e: ClientProtocolException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return xml
    }

    fun getDomElement(xml: String): Document? {
        var doc: Document? = null
        val dbf = DocumentBuilderFactory.newInstance()
        try {
            val db = dbf.newDocumentBuilder()
            val `is` = InputSource()
            `is`.characterStream = StringReader(xml)
            doc = db.parse(`is`)
        } catch (e: ParserConfigurationException) {
            //Log.e("Error: ", e.getMessage());
            return null
        } catch (e: SAXException) {
            //Log.e("Error: ", e.getMessage());
            return null
        } catch (e: IOException) {
            //Log.e("Error: ", e.getMessage());
            return null
        }
        return doc
    }

    fun getElementValue(elem: Node?): String? {
        var child: Node?
        if (elem != null) {
            if (elem.hasChildNodes()) {
                child = elem.firstChild
                while (child != null) {
                    if (child.nodeType == Node.TEXT_NODE) {
                        return child.nodeValue
                    }
                    child = child.nextSibling
                }
            }
        }
        return ""
    }

    fun getValue(item: Element, str: String?): String? {
        val n = item.getElementsByTagName(str)
        return getElementValue(n.item(0))
    }
}
