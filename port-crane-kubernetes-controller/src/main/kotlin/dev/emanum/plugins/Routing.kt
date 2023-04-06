package dev.emanum.plugins

import io.ktor.server.application.*
//import io.ktor.server.plugins.openapi.*
//import io.ktor.server.plugins.swagger.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.routing.get
import io.kubernetes.client.openapi.ApiClient
import io.kubernetes.client.openapi.Configuration
import io.kubernetes.client.openapi.apis.CoreV1Api
import io.kubernetes.client.util.Config


fun Application.configureRouting() {
    routing {
//        openAPI(path="openapi", swaggerFile = "openapi/documentation.yaml")
//        swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml")

        get("/") {
            call.respondText("Hello World!")
        }

        get("/pod") {
            this@configureRouting.log.info("Getting all pods")
            val client: ApiClient = Config.defaultClient()
            Configuration.setDefaultApiClient(client)

            val api = CoreV1Api()
            val pods = api.listPodForAllNamespaces(
                null, null, null,
                null, null, null, null,
                null, null, null
            )
            call.respondText { pods.toString() }
        }

        get("/namespace") {
            this@configureRouting.log.info("Getting all pods")
            val client: ApiClient = Config.defaultClient()
            Configuration.setDefaultApiClient(client)

            val api = CoreV1Api()
            val namespaces = api.listNamespace(null, null, null, null, null, null, null, null, null, null)
            call.respondText { namespaces.toString() }
        }
        



    }
}
