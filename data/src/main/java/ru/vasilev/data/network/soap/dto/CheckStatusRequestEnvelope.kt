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
class CheckStatusRequestEnvelope(
    @field:Element(name = "Body")
    var body: CheckStatusRequestBody = CheckStatusRequestBody()
)

class CheckStatusRequestBody(
    @field:Element(name = "CheckStatus")
    var request: CheckStatusRequest = CheckStatusRequest()
)

class CheckStatusRequest(
    @field:Element(name = "id")
    var id: String = ""
)