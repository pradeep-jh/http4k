package org.http4k.routing.websocket

import org.http4k.core.Method
import org.http4k.core.UriTemplate
import org.http4k.routing.asPredicate
import org.http4k.websocket.WsHandler

data class WsPathMethod(val path: String, val method: Method) {
    infix fun to(handler: WsHandler) = when (handler) {
        is RoutingWsHandler -> handler.withPredicate(method.asPredicate()).withBasePath(path)
        else -> RoutingWsHandler(listOf(TemplatedWsRoute(UriTemplate.from(path), handler, method.asPredicate())))
    }
}