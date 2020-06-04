package it.petretiandrea.sw.restapi

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.http.HttpServer
import io.vertx.ext.web.Router

abstract class RestApiVerticle(val port: Int = 8080, val host: String = "localhost") : AbstractVerticle() {

    private lateinit var httpServer: HttpServer

    override fun start(startPromise: Promise<Void>?) {
        super.start(startPromise)

        httpServer = vertx.createHttpServer()
            .requestHandler(createRouter())
            .listen(port, host)
    }

    abstract fun createRouter(): Router

    override fun stop(stopPromise: Promise<Void>?) {
        super.stop(stopPromise)
        httpServer.close()
    }

}