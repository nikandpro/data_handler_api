package github.nikandpro.datahandlerapi.service;

import github.nikandpro.datahandlerapi.dto.UserDto;
import github.nikandpro.datahandlerapi.entity.User;
import github.nikandpro.datahandlerapi.mapper.UserMapper;
import github.nikandpro.datahandlerapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
