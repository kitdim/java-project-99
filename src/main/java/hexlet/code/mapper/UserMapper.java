package hexlet.code.mapper;
import hexlet.code.dto.UserCreateDto;
import hexlet.code.dto.UserDto;
import hexlet.code.dto.UserUpdateDto;
import hexlet.code.model.User;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Mapper(
        uses = {JsonNullableMapper.class, ReferenceMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class UserMapper {
    @Autowired
    private BCryptPasswordEncoder encoder;
    public  abstract User map(UserCreateDto dto);
    public abstract UserDto map(User model);
    public abstract void update(UserUpdateDto dto, @MappingTarget User model);
    @BeforeMapping
    public void encryptPassword(UserCreateDto data) {
        var password = data.getPassword();
        data.setPassword(encoder.encode(password));
    }
}
