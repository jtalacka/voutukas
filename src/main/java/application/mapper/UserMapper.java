package application.mapper;

import application.domain.Option;
import application.domain.User;
import application.dto.OptionDto;
import application.dto.UserDto;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.Collections;


public class UserMapper {

    ModelMapper modelMapper = new ModelMapper();

    public User map(UserDto UserDto){
        User User = modelMapper.map(UserDto, User.class);
        return User;
    }

    public UserDto map(User User){
        UserDto UserDto = modelMapper.map(User, UserDto.class);
        return UserDto;
    }

}
