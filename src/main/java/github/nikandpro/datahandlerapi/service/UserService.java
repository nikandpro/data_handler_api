package github.nikandpro.datahandlerapi.service;

import github.nikandpro.datahandlerapi.dto.UserDto;
import github.nikandpro.datahandlerapi.entity.User;
import github.nikandpro.datahandlerapi.mapper.UserMapper;
import github.nikandpro.datahandlerapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public void save(UserDto userDto) {
        validate(userDto);
        User user = userMapper.toEntity(userDto);

        userRepository.save(user);
    }

    private void validate(UserDto userDto) {
        if (userDto == null) {
            throw new NullPointerException("UserDto is null");
        }
        if (userDto.username().isEmpty()) {
            throw new NullPointerException("UserDto name is empty");
        }
    }

    @Async
    public CompletableFuture<UserDto> findById(long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NoSuchElementException("User not found"));

        return CompletableFuture.completedFuture(userMapper.toDto(user));
    }
}
