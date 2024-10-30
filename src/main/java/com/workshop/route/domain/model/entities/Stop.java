package com.workshop.route.domain.model.entities;

import com.workshop.route.domain.model.valueobjects.Coordinates;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Stop {

    @NotEmpty(message = "El ID de la parada no puede estar vacío")
    private String stopId;

    @NotEmpty(message = "El nombre de la parada no puede estar vacío")
    private String stopName;

    @NotNull(message = "Las coordenadas no pueden ser nulas")
    @Valid
    private Coordinates coordinates;

    @NotNull(message = "Debe haber al menos un horario de llegada")
    @NotEmpty(message = "La lista de horarios de llegada no puede estar vacía")
    private List<String> arrivalTimes;

}

