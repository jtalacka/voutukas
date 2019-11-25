package application.service;

import application.Repositories.UserRepository;
import application.domain.User;
import application.dto.UserDto;
import application.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private UserRepository userRepository;
    private UserMapper userMapper = new UserMapper();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserDto findUserByID(String id){
        return userMapper.map(userRepository.getOne(id));
    }

    public List<UserDto> findAllUser(){
        List<UserDto> userDtoList = new ArrayList<>();
        userRepository.findAll().forEach(user -> {
            userDtoList.add(userMapper.map(user));
        });
        return userDtoList;
    }

    public void deleteUserByID(String id){
        userRepository.deleteById(id);
    }

    public UserDto insert(UserDto userDto){
        return saveUser(userMapper.map(userDto));
    }

    public UserDto update(UserDto userDto){
        return saveUser(userMapper.map(userDto));
    }

    private UserDto saveUser(User user){
        userRepository.save(user);
        return findUserByID(user.getId());
    }
}
