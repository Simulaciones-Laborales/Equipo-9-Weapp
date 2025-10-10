package com.tuempresa.creditflow.creditflow_api.service;

import java.util.List;
import java.util.UUID;

import com.tuempresa.creditflow.creditflow_api.dto.ExtendedBaseResponse;
import com.tuempresa.creditflow.creditflow_api.model.User;
import com.tuempresa.creditflow.creditflow_api.dto.user.ChangeUserRoleDto;
import com.tuempresa.creditflow.creditflow_api.dto.user.UpdateUserDto;
import com.tuempresa.creditflow.creditflow_api.dto.user.UserDto;
import com.tuempresa.creditflow.creditflow_api.dto.user.UserRolDto;

public interface UserService {

    ExtendedBaseResponse<UserDto> findUserById(UUID id);

    ExtendedBaseResponse<UpdateUserDto> updateUser(UpdateUserDto updateUserDto);

    ExtendedBaseResponse<UserRolDto> changeUserRole(ChangeUserRoleDto data);

    ExtendedBaseResponse<List<UserDto>> userLists();

    ExtendedBaseResponse<List<UserDto>> userListsActive();

    ExtendedBaseResponse<String> deleteUserById(UUID id);

    UUID getUserIdByEmail(String email);
    
    // NUEVO: devuelve la entidad User por email (o lanza excepción si no existe)
    User findEntityByEmail(String email);

    // (opcional) devolver entidad por id
    User findEntityById(UUID id);
    
    // Método para encontrar usuario por email, username o id
    User findEntityByPrincipal(String principal);

}

