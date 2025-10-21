package com.tuempresa.creditflow.creditflow_api.service.impl;

import com.tuempresa.creditflow.creditflow_api.dto.BaseResponse;
import com.tuempresa.creditflow.creditflow_api.dto.ExtendedBaseResponse;
import com.tuempresa.creditflow.creditflow_api.dto.user.*;
import com.tuempresa.creditflow.creditflow_api.exception.userExc.UserNotFoundException;
import com.tuempresa.creditflow.creditflow_api.mapper.UserMapper;
import com.tuempresa.creditflow.creditflow_api.model.User;
import com.tuempresa.creditflow.creditflow_api.repository.UserRepository;
import com.tuempresa.creditflow.creditflow_api.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public ExtendedBaseResponse<UserDto> findUserById(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new UserNotFoundException("Este usuario no existe con ese ID: " + id));
        UserDto userDto = userMapper.toDto(user);
        return ExtendedBaseResponse.of(BaseResponse.ok("Usuario encontrado exitosamente"), userDto);
    }

    @Override
    @Transactional
    public ExtendedBaseResponse<UserUpdateResponseDto> updateUser(UserUpdateRequestDto userUpdateRequestDto) {
        User user = userRepository.findById(userUpdateRequestDto.userId())
                .orElseThrow(() -> new UserNotFoundException("Este usuario no existe con ese ID: " + userUpdateRequestDto.userId()));
        if (userUpdateRequestDto.firstName() != null && !userUpdateRequestDto.firstName().isBlank()) {
            user.setFirstName(userUpdateRequestDto.firstName());
        }
        if (userUpdateRequestDto.lastName() != null && !userUpdateRequestDto.lastName().isBlank()) {
            user.setLastName(userUpdateRequestDto.lastName());
        }
        if (userUpdateRequestDto.contact() != null && !userUpdateRequestDto.contact().isBlank()) {
            user.setContact(userUpdateRequestDto.contact());
        }

        userRepository.save(user);
        UserUpdateResponseDto userUpdateResponseDto = userMapper.toUserUpdateResponseDto(user);
        return ExtendedBaseResponse.of(BaseResponse.ok("Usuario actualizado"), userUpdateResponseDto);
    }


    @Override
    @Transactional(readOnly = true)
    public ExtendedBaseResponse<List<UserDto>> userLists() {
        List<User> users = userRepository.findByRoleOrderByCreatedAtAsc(User.Role.PYME);
        return ExtendedBaseResponse.of(BaseResponse.ok("Usuarios PYME encontrados"),
                userMapper.entityListToDtoList(users));
    }

    @Override
    @Transactional(readOnly = true)
    public ExtendedBaseResponse<List<UserDto>> userListsActive() {
        List<User> users = userRepository.findByIsActiveTrueAndRoleOrderByCreatedAtAsc(User.Role.PYME);
        return ExtendedBaseResponse.of(BaseResponse.ok("Usuarios PYME activos encontrados"),
                userMapper.entityListToDtoList(users));
    }

    @Override
    @Transactional
    public ExtendedBaseResponse<UserDto> changeUserStatus(UserStatusRequestDto data) {
        User user = userRepository.findById(data.id()).orElseThrow(() ->
                new UserNotFoundException("Este usuario no existe con ese ID: " + data.id()));
        user.setIsActive(data.status());
        User savedUser = userRepository.save(user);
        return ExtendedBaseResponse.of(BaseResponse.ok("Estado del usuario actualizado"), userMapper.toDto(savedUser));
    }

    @Override
    public UUID getUserIdByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email no encontrado con username: " + email));
        return user.getId();
    }

    @Override
    @Transactional
    public ExtendedBaseResponse<String> deleteUserById(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new UserNotFoundException("Este usuario no existe con ese ID: " + id));
        userRepository.delete(user);
        return ExtendedBaseResponse.of(BaseResponse.ok("Usuario eliminado exitosamente"), null);
    }


    // NUEVO: devuelve la entidad User por email (o lanza excepción si no existe)
    public User findEntityByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con email: " + email));
    }

    // (opcional) devolver entidad por id
    public User findEntityById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con id: " + id));
    }
    
    @Transactional(readOnly = true)
    public User findEntityByPrincipal(String principal) {
        // --1-- Intentar buscar por email
        return userRepository.findByEmail(principal)
                //--2-- Si no encuentra, intentar por username
                .or(() -> userRepository.findByUsername(principal))
                //--3-- Si no encuentra, intentar por UUID (por si el token tuviera id)
                .or(() -> {
                    try {
                        UUID id = UUID.fromString(principal);
                        return userRepository.findById(id);
                    } catch (IllegalArgumentException ex) {
                        return java.util.Optional.empty();
                    }
                })
                //--4--Si no encuentra, lanzar excepción
                .orElseThrow(() -> new UserNotFoundException("User not found with principal: " + principal));
    }

}

