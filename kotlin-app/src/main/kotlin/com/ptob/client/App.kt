package com.ptob.client

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

fun main() {
    val baseUrl = System.getenv("OMNIBUS_BASE_URL") ?: "http://localhost:8080"
    val healthUrl = "$baseUrl/actuator/health"

    val request = HttpRequest.newBuilder()
        .uri(URI.create(healthUrl))
        .GET()
        .build()

    val client = HttpClient.newHttpClient()

    runCatching {
        client.send(request, HttpResponse.BodyHandlers.ofString())
    }.onSuccess { response ->
        println("Kotlin app connected successfully.")
        println("GET $healthUrl")
        println("Status: ${response.statusCode()}")
        println("Body: ${response.body()}")
    }.onFailure { error ->
        println("Kotlin app could not reach $healthUrl")
        println("Reason: ${error.message}")
    }
}
