package org.example.eventhub.mapper

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.example.eventhub.dto.location.LocationCreateRequest
import org.example.eventhub.dto.location.LocationUpdateRequest
import org.example.eventhub.entity.Location

class LocationMapperTest : StringSpec({

    val mapper = LocationMapper()

    "toEntity маппит LocationCreateRequest в Location" {
        val dto = LocationCreateRequest(
            city = "Moscow",
            street = "Tverskaya",
            house = "1",
            additionalInfo = "Near metro"
        )

        val entity = mapper.toEntity(dto)

        entity.city shouldBe "Moscow"
        entity.street shouldBe "Tverskaya"
        entity.house shouldBe "1"
        entity.additionalInfo shouldBe "Near metro"
    }

    "toEntity маппит LocationUpdateRequest в Location" {
        val dto = LocationUpdateRequest(
            city = "SPB",
            street = "Nevsky",
            house = "10",
            additionalInfo = null
        )

        val entity = mapper.toEntity(dto)

        entity.city shouldBe "SPB"
        entity.street shouldBe "Nevsky"
        entity.house shouldBe "10"
        entity.additionalInfo shouldBe null
    }

    "toLongDto маппит entity в dto" {
        val entity = Location(
            city = "Moscow",
            street = "Tverskaya",
            house = "1",
            additionalInfo = "Near metro"
        )

        val dto = mapper.toLongDto(entity)

        dto!!.city shouldBe "Moscow"
        dto.street shouldBe "Tverskaya"
        dto.house shouldBe "1"
        dto.additionalInfo shouldBe "Near metro"
    }

    "toLongDto возвращает null если entity null" {
        val dto = mapper.toLongDto(null)
        dto shouldBe null
    }
})
