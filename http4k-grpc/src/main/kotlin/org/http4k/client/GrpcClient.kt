package org.http4k.client

import io.grpc.MethodDescriptor
import io.grpc.Status.UNKNOWN
import io.grpc.Status.fromCodeValue
import io.grpc.StatusRuntimeException
import org.http4k.core.HttpHandler
import org.http4k.core.Method.POST
import org.http4k.core.Request
import java.lang.Integer.parseInt

class GrpcClient(private val http: HttpHandler) {
    operator fun <IN, OUT> invoke(method: MethodDescriptor<IN, OUT>, input: IN): OUT {
        val response = http(
            Request(POST, method.fullMethodName)
                .body(method.requestMarshaller.stream(input))
        )

        return when {
            response.status.successful -> method.responseMarshaller.parse(response.body.stream)
            else -> try {
                throw StatusRuntimeException(fromCodeValue(parseInt(response.bodyString())))
            } catch (e: Exception) {
                throw StatusRuntimeException(UNKNOWN)
            }
        }
    }
}
