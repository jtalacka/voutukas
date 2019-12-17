package application.mapper;

import application.domain.User;
import application.dto.UserDto;
import org.modelmapper.ModelMapper;


public class UserMapper {

    private final ModelMapper modelMapper = new ModelMapper();

    public User map(UserDto userDto){
        User user = modelMapper.map(userDto, User.class);
        user.setFullName(userDto.getFullName());
        user.setSlackName(userDto.getSlackName());
        return user;
    }

    public UserDto map(User user){
        UserDto userDto = modelMapper.map(user, UserDto.class);
        userDto.setFullName(user.getFullName());
        userDto.setSlackName(user.getSlackName());
        return userDto;
    }

}
