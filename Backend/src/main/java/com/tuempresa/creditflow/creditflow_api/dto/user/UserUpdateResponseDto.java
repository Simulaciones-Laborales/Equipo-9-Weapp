package com.tuempresa.creditflow.creditflow_api.dto.user;

import java.io.Serializable;
import java.util.UUID;

public record UserUpdateResponseDto(UUID userId,
                                    String firstName,
                                    String lastName,
                                    String username,
                                    String contact,
                                    String birthDate,
                                    String country
) implements Serializable {

}
