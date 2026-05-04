package edu.moravian.csci395.carman.data

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class OverpassResponse(
    val elements: List<OverpassElement>
)

@Serializable
data class OverpassElement(
    val id: Long,
    val lat: Double? = null,
    val lon: Double? = null,
    val center: OverpassCenter? = null, // for ways/areas
    val tags: Map<String, String>? = null
)

@Serializable
data class OverpassCenter(
    val lat: Double,
    val lon: Double
)

class OverpassService {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
            })
        }
    }

    /**
     * Searches for car repair shops in a bounding box.
     * @param north northern latitude
     * @param south southern latitude
     * @param east eastern longitude
     * @param west western longitude
     */
    suspend fun searchMechanics(north: Double, south: Double, east: Double, west: Double): List<MechanicEntity> {
        val query = """
            [out:json];
            (
              node["amenity"="car_repair"]($south,$west,$north,$east);
              way["amenity"="car_repair"]($south,$west,$north,$east);
              node["shop"="car_repair"]($south,$west,$north,$east);
              way["shop"="car_repair"]($south,$west,$north,$east);
            );
            out center;
        """.trimIndent()

        val response: OverpassResponse = client.get("https://overpass-api.de/api/interpreter") {
            parameter("data", query)
        }.body()

        return response.elements.map { element ->
            val lat = element.lat ?: element.center?.lat ?: 0.0
            val lon = element.lon ?: element.center?.lon ?: 0.0
            val name = element.tags?.get("name") ?: "Unknown Mechanic"
            val phone = element.tags?.get("phone") ?: element.tags?.get("contact:phone")
            val street = element.tags?.get("addr:street")
            val houseNumber = element.tags?.get("addr:housenumber")
            val address = if (street != null) "${houseNumber ?: ""} $street".trim() else null

            MechanicEntity(
                name = name,
                latitude = lat,
                longitude = lon,
                phone = phone,
                addressLine = address,
                createdAt = 0, // Not saved yet
                notes = "Discovered via OpenStreetMap"
            )
        }
    }
}
