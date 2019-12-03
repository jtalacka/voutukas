package application.controllers;

import application.dto.UserDto;
import application.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UsersAPIController {
    private final UserService userService;
    public UsersAPIController(UserService userService)
    {
        this.userService = userService;
    }

    @GetMapping(value = "/all")
    public ResponseEntity<List<UserDto>> getAllUsers()
    {
        return ResponseEntity.ok(userService.findAllUser());
    }

    @GetMapping(value = "/{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable String userId)
    {
        UserDto userDto = userService.findUserByID(userId);
        if(userDto == null)
        {
            return ResponseEntity.notFound().build();
        }
        else
        {
            userService.insert(userDto);
            return ResponseEntity.ok().body(userDto);
        }
    }


    @DeleteMapping(value = "/{userId}")
    public ResponseEntity deleteUserById(@PathVariable String userId)
    {
        UserDto userDto = userService.findUserByID(userId);
        if(userDto == null)
        {
            return ResponseEntity.notFound().build();
        }
        else
        {
            userService.deleteUserByID(userId);
            return ResponseEntity.ok().body(null);
        }
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDto> PutUser(@RequestBody UserDto userDto)
    {
        if(userService.findUserByID(userDto.getId()) != null)
        {
            userService.update(userDto);
            return ResponseEntity.ok(userDto);
        }
        else
        {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDto> PostUser(@RequestBody UserDto userDto)
    {
        if(userService.findUserByID(userDto.getId()) != null)
        {
            return ResponseEntity.badRequest().body(null);
        }
        else
        {
            userService.insert(userDto);
            return ResponseEntity.ok(userDto);
        }
    }

}
