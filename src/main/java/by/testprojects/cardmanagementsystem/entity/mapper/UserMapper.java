package by.testprojects.cardmanagementsystem.security.mapper;

import by.testprojects.cardmanagementsystem.security.entity.Dto.RegisterRequest;
import by.testprojects.cardmanagementsystem.security.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser (RegisterRequest registerRequest);


}
