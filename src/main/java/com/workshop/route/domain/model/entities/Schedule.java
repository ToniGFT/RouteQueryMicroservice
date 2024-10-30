package com.workshop.route.domain.model.entities;

import com.workshop.route.domain.model.valueobjects.WeekSchedule;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Schedule {

    @NotNull(message = "El horario entre semana no puede ser nulo")
    @Valid
    private WeekSchedule weekdays;

    @NotNull(message = "El horario de fin de semana no puede ser nulo")
    @Valid
    private WeekSchedule weekends;

}

