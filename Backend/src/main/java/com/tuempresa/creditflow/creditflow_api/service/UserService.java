package com.tuempresa.creditflow.creditflow_api.service;

import com.tuempresa.creditflow.creditflow_api.dtos.ExtendedBaseResponse;
import com.tuempresa.creditflow.creditflow_api.dtos.user.*;

import java.util.List;
import java.util.UUID;

public interface UserService {

    ExtendedBaseResponse<UserDto> findUserById(UUID id);

    ExtendedBaseResponse<UpdateUserDto> updateUser(UpdateUserDto updateUserDto);

    ExtendedBaseResponse<UserRolDto> changeUserRole(ChangeUserRoleDto data);

    ExtendedBaseResponse<List<UserDto>> userLists();

    ExtendedBaseResponse<List<UserDto>> userListsActive();

    ExtendedBaseResponse<String> deleteUserById(UUID id);

    ExtendedBaseResponse<UserDto> changeUserStatus(UserStatusRequestDto data);
    
    UUID getUserIdByEmail(String email);


}
