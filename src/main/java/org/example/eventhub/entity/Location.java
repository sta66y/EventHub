package org.example.eventhub.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
public class Location {

    @Column(length = 100)
    private String city;

    @Column(length = 200)
    private String street;

    @Column(length = 20)
    private String house;

    private String additionalInfo;
}