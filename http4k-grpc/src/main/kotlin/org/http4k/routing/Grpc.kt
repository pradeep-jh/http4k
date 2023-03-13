package org.http4k.routing

import io.grpc.BindableService
import io.grpc.MethodDescriptor
import io.grpc.Status.Code
import io.grpc.Status.Code.*
import io.grpc.Status.fromThrowable
import io.grpc.stub.StreamObserver
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.Status.Companion.OK
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicReference

fun grpc(svc: BindableService) = routes(
    *svc.bindService().methods.map { svc.routeFor(it.methodDescriptor) }.toTypedArray()
)

@Suppress("UNCHECKED_CAST")
fun <IN, OUT> BindableService.routeFor(method: MethodDescriptor<IN, OUT>): RoutingHttpHandler {
    val svcMethod = this@routeFor.javaClass.methods.first { it.name.uppercase() == method.bareMethodName?.uppercase() }

    return method.fullMethodName bind POST to
        { req: Request ->
            val result = AtomicReference<Response>()

            val latch = CountDownLatch(1)

            svcMethod(this, method.parseRequest(req.body.stream), object : StreamObserver<OUT> {
                override fun onNext(value: OUT) =
                    result.set(Response(OK).body(method.responseMarshaller.stream(value)))

                override fun onError(t: Throwable) {
                    fromThrowable(t).code.apply {
                        result.set(Response(toHttp()).body(value().toString()))
                        latch.countDown()
                    }
                }

                override fun onCompleted() = latch.countDown()
            })

            result.get()
        }
}

private fun Code.toHttp() =
    when (this) {
        Code.OK -> OK
        CANCELLED -> Status(499, null)
        UNKNOWN -> Status.INTERNAL_SERVER_ERROR
        INVALID_ARGUMENT -> Status.BAD_REQUEST
        DEADLINE_EXCEEDED -> Status.REQUEST_TIMEOUT
        NOT_FOUND -> Status.NOT_FOUND
        ALREADY_EXISTS -> Status.CONFLICT
        PERMISSION_DENIED -> Status.FORBIDDEN
        RESOURCE_EXHAUSTED -> Status.TOO_MANY_REQUESTS
        FAILED_PRECONDITION -> Status.BAD_REQUEST
        ABORTED -> Status.CONFLICT
        OUT_OF_RANGE -> Status.BAD_REQUEST
        UNIMPLEMENTED -> Status.NOT_IMPLEMENTED
        INTERNAL -> Status.INTERNAL_SERVER_ERROR
        UNAVAILABLE -> Status.INTERNAL_SERVER_ERROR
        DATA_LOSS -> Status.SERVICE_UNAVAILABLE
        UNAUTHENTICATED -> Status.FORBIDDEN
    }
