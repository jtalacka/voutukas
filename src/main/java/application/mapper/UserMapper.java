package application.mapper;

import application.domain.User;
import application.dto.UserDto;
import org.modelmapper.ModelMapper;


public class UserMapper {

    private final ModelMapper modelMapper = new ModelMapper();

    public User map(UserDto UserDto){
        User User = modelMapper.map(UserDto, User.class);
        return User;
    }

    public UserDto map(User User){
        UserDto UserDto = modelMapper.map(User, UserDto.class);
        return UserDto;
    }

}
