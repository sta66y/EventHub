package org.example.eventhub.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
class Location(
    @Column(length = 100)
    var city: String? = null,

    @Column(length = 200)
    var street: String? = null,

    @Column(length = 20)
    var house: String? = null,

    var additionalInfo: String? = null
)