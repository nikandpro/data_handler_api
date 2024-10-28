package github.nikandpro.datahandlerapi.service;

import github.nikandpro.datahandlerapi.dto.UserDto;
import github.nikandpro.datahandlerapi.entity.User;
import github.nikandpro.datahandlerapi.mapper.UserMapper;
import github.nikandpro.datahandlerapi.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public void save(UserDto userDto) {
        validate(userDto);
        User user = userMapper.toEntity(userDto);

        userRepository.save(user);
    }

    @Transactional
    public void update(UserDto userDto) {
        validate(userDto);
        User user = userRepository.findById(userDto.id()).orElseThrow(() -> new NoSuchElementException("User not found"));
        user.setUsername(userDto.username());

        userRepository.save(user);
    }

    public UserDto findById(long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NoSuchElementException("User not found"));

        return userMapper.toDto(user);
    }

    @Transactional
    public void deleteById(long id) {
        if (!userRepository.existsById(id)) {
            throw new NoSuchElementException("User not found");
        }
        userRepository.deleteById(id);
    }

    private void validate(UserDto userDto) {
        if (userDto == null) {
            throw new NullPointerException("UserDto is null");
        }
        if (userDto.username().isEmpty()) {
            throw new NullPointerException("UserDto name is empty");
        }
    }
}
