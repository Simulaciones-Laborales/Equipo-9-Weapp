package com.tuempresa.creditflow.creditflow_api.service;

import com.tuempresa.creditflow.creditflow_api.dto.ExtendedBaseResponse;
import com.tuempresa.creditflow.creditflow_api.dto.user.*;
import com.tuempresa.creditflow.creditflow_api.model.User;

import java.util.List;
import java.util.UUID;

public interface UserService {

    ExtendedBaseResponse<UserDto> findUserById(UUID id);

    ExtendedBaseResponse<UserUpdateResponseDto> updateUser(UserUpdateRequestDto updateUserDto);

    ExtendedBaseResponse<List<UserDto>> userLists();

    ExtendedBaseResponse<List<UserDto>> userListsActive();

    ExtendedBaseResponse<String> deleteUserById(UUID id);

    ExtendedBaseResponse<UserDto> changeUserStatus(UserStatusRequestDto data);
    
    UUID getUserIdByEmail(String email);
    
    // NUEVO: devuelve la entidad User por email (o lanza excepción si no existe)
    User findEntityByEmail(String email);

    // (opcional) devolver entidad por id
    User findEntityById(UUID id);
    
    // Método para encontrar usuario por email, username o id
    User findEntityByPrincipal(String principal);


}
