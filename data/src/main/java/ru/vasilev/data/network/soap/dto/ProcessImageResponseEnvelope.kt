package ru.vasilev.data.network.soap.dto

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "Envelope")
class ProcessImageResponseEnvelope(
    @field:Element(name = "Body")
    var body: ProcessImageResponseBody = ProcessImageResponseBody() // Значение по умолчанию
)

class ProcessImageResponseBody(
    @field:Element(name = "ProcessImageResponse")
    var response: ProcessImageResponse = ProcessImageResponse() // Значение по умолчанию
)

class ProcessImageResponse(
    @field:Element(name = "id")
    var id: String = "", // Пустая строка как дефолт

    @field:Element(name = "status")
    var status: String = ""
)