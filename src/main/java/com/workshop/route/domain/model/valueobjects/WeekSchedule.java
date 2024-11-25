package com.workshop.route.domain.model.valueobjects;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeekSchedule {

    @NotNull(message = "La hora de inicio no puede ser nula")
    private LocalTime startTime;

    @NotNull(message = "La hora de finalización no puede ser nula")
    private LocalTime endTime;

    @NotNull(message = "La frecuencia de minutos no puede ser nula")
    @Positive(message = "La frecuencia debe ser un número positivo")
    private Integer frequencyMinutes;

    @AssertTrue(message = "La hora de finalización debe ser después de la hora de inicio")
    public boolean isEndTimeAfterStartTime() {
        return endTime == null || startTime == null || endTime.isAfter(startTime);
    }
}
