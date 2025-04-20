package by.testprojects.cardmanagementsystem.entity.mapper;

import by.testprojects.cardmanagementsystem.entity.dto.RegisterRequest;
import by.testprojects.cardmanagementsystem.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser (RegisterRequest registerRequest);


}
