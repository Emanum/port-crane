package dev.emanum.plugins

import dev.emanum.Namespace
import io.fabric8.kubernetes.api.model.Service
import io.fabric8.kubernetes.client.KubernetesClientBuilder
import io.fabric8.kubernetes.client.utils.Serialization
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.openapi.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.kubernetes.client.openapi.ApiClient
import io.kubernetes.client.openapi.Configuration
import io.kubernetes.client.openapi.apis.CoreV1Api
import io.kubernetes.client.util.Config
import java.nio.file.Files
import java.nio.file.Path
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
            val api = getAPI()
            val pods = api.listPodForAllNamespaces(
                null, null, null,
                null, null, null, null,
                null, null, null
            )
            call.respondText { pods.toString() }
        }

        get("/namespace") {
            this@configureRouting.log.info("Getting all pods")
            val api = getAPI()
            val namespaces = api.listNamespace(null, null, null, null, null, null, null, null, null, null)
            val namespacesEntity = namespaces.items.stream()
                .map { Namespace(it.metadata?.name) }
                .collect(Collectors.toList())
            call.respond(namespacesEntity)
        }

        delete("/namespace/{name}") {
            val name = call.parameters["name"] ?: return@delete call.respondText("Missing or malformed name", status = HttpStatusCode.BadRequest)

            val api = getAPI()
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

        // SERVICES

        get("/service/{namespace}") {
            val namespace = call.parameters["namespace"] ?: return@get call.respondText("Missing or malformed namespace", status = HttpStatusCode.BadRequest)
            val api = getAPI()
            val listNamespacedService =
                api.listNamespacedService(namespace, null, null, null, null, null, null, null, null, null, null)

            call.respondText { listNamespacedService.toString() }
        }

        post("/service") {
            val namespace = "default"
            Path.of(ClassLoader.getSystemResource("services/nginxService.yaml").toURI())
            val yamlString = Files.readString(Path.of(ClassLoader.getSystemResource("services/nginxService.yaml").toURI()))

            val client = KubernetesClientBuilder().build()
            val service = Serialization.unmarshal(
                yamlString,
                Service::class.java
            )
            val response = client.services().inNamespace(namespace)
                .resource(service)
                .serverSideApply()
            call.respondText { response.status.toString() }
        }

    }
}

private fun getAPI(): CoreV1Api {
    val client: ApiClient = Config.defaultClient()
    Configuration.setDefaultApiClient(client)

    val api = CoreV1Api()
    return api
}
