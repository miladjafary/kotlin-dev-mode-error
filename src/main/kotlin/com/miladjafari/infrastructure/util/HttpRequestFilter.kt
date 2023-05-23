package com.miladjafari.infrastructure.util

import io.opentracing.util.GlobalTracer
import io.vertx.core.http.HttpServerRequest
import jakarta.ws.rs.container.ContainerRequestContext
import jakarta.ws.rs.core.HttpHeaders
import jakarta.ws.rs.core.MediaType
import org.jboss.logging.Logger
import org.jboss.resteasy.reactive.RestResponse
import org.jboss.resteasy.reactive.server.ServerRequestFilter
import java.util.*

class HttpRequestFilter(private val logger: Logger) {

    @ServerRequestFilter
    fun logHttpRequest(request: HttpServerRequest, requestContext: ContainerRequestContext): Optional<RestResponse<Void>> {
        val span = GlobalTracer.get().activeSpan()

        incomingLog("${requestContext.method} ${requestContext.uriInfo.requestUri.path}")

        request.headers().forEach { header ->
            val headerValue = getMaskValueIfHeaderIsSensitive(header.key, header.value)
            span?.setTag("http.headers.${header.key}", headerValue)
            incomingLog(header.toString())
        }

        request.body { buffer ->
            if (requestContext.mediaType == MediaType.APPLICATION_JSON_TYPE) {
                val requestBodyAsString = buffer.result().toString()
                span?.setTag("http.requestBody", requestBodyAsString)
                incomingLog(requestBodyAsString)
            }
        }

        return Optional.empty()
    }

    private fun getMaskValueIfHeaderIsSensitive(headerKey: String, headerValue: String): String {
        return if (HttpHeaders.AUTHORIZATION.equals(headerKey, true)) "****" else headerValue
    }

    private fun incomingLog(log: String) {
        logger.debug("http-incoming << $log")
    }
}