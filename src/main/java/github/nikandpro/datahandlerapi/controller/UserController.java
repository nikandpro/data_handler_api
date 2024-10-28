package github.nikandpro.datahandlerapi.controller;

import github.nikandpro.datahandlerapi.dto.UserDto;
import github.nikandpro.datahandlerapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/create")
    public void createUser(@RequestBody UserDto user) {
        userService.save(user);
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable long id) {
        return userService.findById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable long id) {
        userService.deleteById(id);
    }
}
