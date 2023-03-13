package org.http4k.routing

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import io.grpc.Status
import io.grpc.StatusException
import io.grpc.protobuf.lite.ProtoLiteUtils.marshaller
import io.grpc.stub.StreamObserver
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.grpc.EchoRequest
import org.http4k.grpc.EchoResponse
import org.http4k.grpc.EchoServiceGrpc
import org.junit.jupiter.api.Test

class GrpcTest {

    private val service = object : EchoServiceGrpc.EchoServiceImplBase() {
        override fun echo(request: EchoRequest, obvs: StreamObserver<EchoResponse>) {
            with(obvs) {
                when {
                    request.message == "success" -> onNext(
                        EchoResponse.newBuilder()
                            .setReversed(request.message.reversed())
                            .build()
                    )

                    else -> onError(Foo)
                }
            }
        }
    }
    val grpc = grpc(service)

    @Test
    fun `can call through to grpc service and get response`() {
        val message = "success"

        val input = marshaller(EchoRequest.getDefaultInstance()).stream(
            EchoRequest.newBuilder().setMessage(message).build()
        )

        val response = grpc(Request(POST, EchoServiceGrpc.getEchoMethod().fullMethodName).body(input))

        assertThat(
            marshaller(EchoResponse.getDefaultInstance())
                .parse(response.body.stream).reversed,
            equalTo(message.reversed())
        )
    }

    @Test
    fun `converts controlled exception to protocol error`() {
        val message = "failure"

        val input = marshaller(EchoRequest.getDefaultInstance()).stream(
            EchoRequest.newBuilder().setMessage(message).build()
        )

        assertThat(
            grpc(Request(POST, EchoServiceGrpc.getEchoMethod().fullMethodName).body(input)).status,
            equalTo(org.http4k.core.Status.CONFLICT)
        )
    }
}

private object Foo : StatusException(Status.ABORTED)
