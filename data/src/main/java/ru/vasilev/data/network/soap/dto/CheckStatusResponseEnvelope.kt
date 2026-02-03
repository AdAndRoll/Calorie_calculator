package ru.vasilev.data.network.soap.dto

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "Envelope")
class CheckStatusResponseEnvelope(
    @field:Element(name = "Body")
    var body: CheckStatusResponseBody = CheckStatusResponseBody()
)

class CheckStatusResponseBody(
    @field:Element(name = "CheckStatusResponse")
    var response: CheckStatusResponse = CheckStatusResponse()
)

class CheckStatusResponse(
    @field:Element(name = "status")
    var status: String = "",

    @field:Element(name = "result", required = false)
    var result: String? = null
)