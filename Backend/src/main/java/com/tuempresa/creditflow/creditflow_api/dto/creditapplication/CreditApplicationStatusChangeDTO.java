package com.tuempresa.creditflow.creditflow_api.dto.creditapplication;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditApplicationStatusChangeDTO {

    @NotBlank
    private String newStatus; // debe corresponder al enum CreditStatus

    private String comments; // comentario que irá al history y/o al operatorComments
}

