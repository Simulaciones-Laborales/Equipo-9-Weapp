package com.tuempresa.creditflow.creditflow_api.service.impl;

import com.tuempresa.creditflow.creditflow_api.repository.UserRepository;
import com.tuempresa.creditflow.creditflow_api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public ExtendedBaseResponse<String> upDateImagesUser(UpDateImagesUserDto upDateImagesUser) {
        String newImageUrl = uploadSingleImage(upDateImagesUser.getImage());
        User user = userRepository.findById(upDateImagesUser.getUserId()).orElseThrow(() ->
                new UserNotFoundException("Este usuario no existe con ese ID: " + upDateImagesUser.getUserId()));

        if (user.getUserImage() != null && !user.getUserImage().isEmpty()) {
            deleteSingleImage(user.getUserImage());
        }

        user.setUserImage(newImageUrl);
        User savedUser = userRepository.save(user);
        return ExtendedBaseResponse.of(BaseResponse.created("Imagen actualizada/cargada correctamente"), savedUser.getUserImage());
    }


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
    public ExtendedBaseResponse<UpdatedUserDto> updateUser(UpdateUserDto updateUserDto) {
        User user = userRepository.findById(updateUserDto.userId())
                .orElseThrow(() -> new UserNotFoundException("Este usuario no existe con ese ID: " + updateUserDto.userId()));
        if (updateUserDto.username() != null && !updateUserDto.username().isBlank()) {
            user.setUsername(updateUserDto.username());
        }
        if (updateUserDto.email() != null && !updateUserDto.email().isBlank()) {
            user.setEmail(updateUserDto.email());
        }
        if (updateUserDto.contact() != null && !updateUserDto.contact().isBlank()) {
            user.setContact(updateUserDto.contact());
        }
        if (updateUserDto.password() != null && !updateUserDto.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(updateUserDto.password()));
        }
        if (updateUserDto.wantsEmailNotifications() != null) {
            user.setWantsEmailNotifications(updateUserDto.wantsEmailNotifications());
        }
        userRepository.save(user);
        UpdatedUserDto updatedUserDto = userMapper.toUpdatedUser(user);
        return ExtendedBaseResponse.of(BaseResponse.ok("Usuario actualizado"), updatedUserDto);
    }

    @Override
    @Transactional
    public ExtendedBaseResponse<UserRolDto> changeUserRole(ChangeUserRoleDto data) {
        User user = userRepository.findById(data.id())
                .orElseThrow(() -> new UserNotFoundException("Este usuario no existe con ese ID: " + data.id()));
        User.Role rol = user.ChangeRole(data.role());
        user.setRole(rol);
        userRepository.save(user);
        UserRolDto userRolDto = userMapper.toUserRolDto(user);
        return ExtendedBaseResponse.of(BaseResponse.ok("Usuario actualizado"), userRolDto);
    }

    @Override
    @Transactional(readOnly = true)
    public ExtendedBaseResponse<List<UserDto>> userLists() {
        List<User> users = userRepository.findAll(Sort.by(Sort.Direction.ASC, "username"));
        return ExtendedBaseResponse.of(BaseResponse.ok("Usuarios encontrados"), userMapper.entityListToDtoList(users));
    }

    @Override
    @Transactional(readOnly = true)
    public ExtendedBaseResponse<List<UserDto>> userListsActive() {
        List<User> users = userRepository.findByIsActiveTrueOrderByUsernameAsc();
        return ExtendedBaseResponse.of(BaseResponse.ok("Usuarios encontrados"), userMapper.entityListToDtoList(users));
    }

    @Override
    @Transactional
    public ExtendedBaseResponse<UserDto> changeUserStatus(UserStatusRequest data) {
        User user = userRepository.findById(data.id()).orElseThrow(() ->
                new UserNotFoundException("Este usuario no existe con ese ID: " + data.id()));
        user.setIsActive(data.status());
        User savedUser = userRepository.save(user);
        return ExtendedBaseResponse.of(BaseResponse.ok("Estado del usuario actualizado"), userMapper.toDto(savedUser));
    }

    @Override
    @Transactional
    public ExtendedBaseResponse<String> deleteUserById(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new UserNotFoundException("Este usuario no existe con ese ID: " + id));
        userRepository.delete(user);
        return ExtendedBaseResponse.of(BaseResponse.ok("Usuario eliminado exitosamente"), null);
    }

    private String uploadSingleImage(MultipartFile image) {
        return imageService.uploadImage(image);
    }

    private void deleteSingleImage(String imageUrl) {
        try {
            imageService.deleteImage(imageUrl);
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar la imagen", e);
        }
    }

    @Override
    public UUID getUserIdByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email no encontrado con username: " + email));
        return user.getId();
    }
}

