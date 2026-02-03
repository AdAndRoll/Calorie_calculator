package ru.vasilev.data.network.soap.dto

import org.simpleframework.xml.Element
import org.simpleframework.xml.Namespace
import org.simpleframework.xml.NamespaceList
import org.simpleframework.xml.Root

@Root(name = "soap:Envelope")
@NamespaceList(
    Namespace(prefix = "soap", reference = "http://schemas.xmlsoap.org/soap/envelope/"),
    Namespace(prefix = "web", reference = "http://fake-api-soap.com/")
)
class ProcessImageRequestEnvelope(
    @field:Element(name = "Body")
    var body: ProcessImageRequestBody = ProcessImageRequestBody()
)

class ProcessImageRequestBody(
    @field:Element(name = "ProcessImage")
    var request: ProcessImageRequest = ProcessImageRequest()
)

class ProcessImageRequest(
    @field:Element(name = "image_base64")
    var imageBase64: String = "",

    @field:Element(name = "description")
    var description: String = ""
)