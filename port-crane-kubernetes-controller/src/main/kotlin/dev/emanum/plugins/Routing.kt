package dev.emanum.plugins

import dev.emanum.Namespace
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.openapi.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.routing.get
import io.kubernetes.client.openapi.ApiClient
import io.kubernetes.client.openapi.Configuration
import io.kubernetes.client.openapi.apis.CoreV1Api
import io.kubernetes.client.util.Config
import java.util.stream.Collectors


fun Application.configureRouting() {
    routing {
        openAPI(path="openapi", swaggerFile = "openapi/documentation.yaml")
        swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml")

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
            val namespacesEntity = namespaces.items.stream()
                .map { Namespace(it.metadata?.name) }
                .collect(Collectors.toList())
            call.respond(namespacesEntity)
        }

        delete("/namespace/{name}") {
            val name = call.parameters["name"] ?: return@delete call.respondText("Missing or malformed name", status = HttpStatusCode.BadRequest)

            val client: ApiClient = Config.defaultClient()
            Configuration.setDefaultApiClient(client)

            val api = CoreV1Api()
            this@configureRouting.log.info("Deleting namespace $name")
            try {
                val deleteNamespace = api.deleteNamespace(name, null, null, null, null, null, null)
                if(deleteNamespace.status == "Success") {
                    call.respondText("Namespace $name deleted", status = HttpStatusCode.OK)
                }
            }catch (e: Exception) {
                this@configureRouting.log.error("Could not delete namespace $name", e)
            }
            call.respondText("Namespace $name could not be deleted", status = HttpStatusCode.InternalServerError)
        }
        



    }
}
