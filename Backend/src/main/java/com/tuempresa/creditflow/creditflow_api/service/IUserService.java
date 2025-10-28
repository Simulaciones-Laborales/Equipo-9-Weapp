package com.tuempresa.creditflow.creditflow_api.service;

import com.tuempresa.creditflow.creditflow_api.dto.ExtendedBaseResponse;
import com.tuempresa.creditflow.creditflow_api.dto.company.CompanyResponseDTO;
import com.tuempresa.creditflow.creditflow_api.dto.kyc.KycVerificationResponseDTO;
import com.tuempresa.creditflow.creditflow_api.dto.user.*;
import com.tuempresa.creditflow.creditflow_api.model.User;

import java.util.List;
import java.util.UUID;

public interface IUserService {

    ExtendedBaseResponse<UserDto> findUserById(UUID id);

    ExtendedBaseResponse<UserUpdateResponseDto> updateUser(UserUpdateRequestDto updateUserDto);

    ExtendedBaseResponse<List<UserDto>> userLists();

    ExtendedBaseResponse<List<UserDto>> userListsActive();

    ExtendedBaseResponse<String> deleteUserById(UUID id);

    ExtendedBaseResponse<UserDto> changeUserStatus(UserStatusRequestDto data);

    User findEntityByPrincipal(String principal);

    ExtendedBaseResponse<UserDto> findOnlineUser();

    ExtendedBaseResponse<List<CompanyResponseDTO>> getCompaniesByUserId(UUID userId);
}
