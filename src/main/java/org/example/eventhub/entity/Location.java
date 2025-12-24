package org.example.eventhub.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Location {

    @Column(length = 100)
    @NotBlank
    private String city;

    @Column(length = 200)
    @NotBlank
    private String street;

    @Column(length = 20)
    @NotBlank
    private String house;

    private String additionalInfo;
}