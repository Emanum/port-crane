package dev.emanum.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.kubernetes.client.openapi.ApiClient
import io.kubernetes.client.openapi.Configuration
import io.kubernetes.client.openapi.apis.CoreV1Api
import io.kubernetes.client.util.Config
import kotlin.math.log


fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        get("/pods") {
            this@configureRouting.log.info("Getting all pods")
            val client: ApiClient = Config.defaultClient()
            Configuration.setDefaultApiClient(client)

            val api = CoreV1Api()
            val list = api.listPodForAllNamespaces(
                null, null, null,
                null, null, null, null,
                null, null, null
            )
            this@configureRouting.log.info(list.toString())
            call.respondText { list.toString() }
        }

        post("/namespaces") {

        }
        



    }
}
