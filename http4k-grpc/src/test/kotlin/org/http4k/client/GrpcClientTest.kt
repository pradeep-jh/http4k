package org.http4k.client

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import io.grpc.Status.ABORTED
import io.grpc.StatusRuntimeException
import io.grpc.stub.StreamObserver
import org.http4k.grpc.EchoRequest
import org.http4k.grpc.EchoRequest.newBuilder
import org.http4k.grpc.EchoResponse
import org.http4k.grpc.EchoServiceGrpc
import org.http4k.grpc.EchoServiceGrpc.getEchoMethod
import org.http4k.routing.grpc
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class GrpcClientTest {

    private val service = object : EchoServiceGrpc.EchoServiceImplBase() {
        override fun echo(request: EchoRequest, obvs: StreamObserver<EchoResponse>) {
            with(obvs) {
                when {
                    request.message == "success" -> {
                        onNext(
                            EchoResponse.newBuilder()
                                .setReversed(request.message.reversed())
                                .build()
                        )
                    }

                    else -> onError(Foo)
                }
            }
        }
    }

    private val client = GrpcClient(grpc(service))

    @Test
    fun `can call client successfully`() {
        val message = "success"

        assertThat(
            client(getEchoMethod(), newBuilder().setMessage(message).build()).reversed,
            equalTo(message.reversed())
        )
    }

    @Test
    fun `client rethrows on error`() {
        assertThrows<StatusRuntimeException> {
            client(getEchoMethod(), newBuilder().setMessage("failure").build())
        }
    }
}

private object Foo : StatusRuntimeException(ABORTED)
