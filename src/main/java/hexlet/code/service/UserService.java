package hexlet.code.service;

import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.dto.user.UserDTO;
import hexlet.code.dto.user.UserUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.UserMapper;
import hexlet.code.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserDTO> getAll() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::map)
                .toList();
    }

    public UserDTO findUser(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not Found: " + id));
        return userMapper.map(user);
    }

    public UserDTO createUser(UserCreateDTO userData) {
        var user = userMapper.map(userData);
        userRepository.save(user);
        return userMapper.map(user);
    }

    public UserDTO updateUser(UserUpdateDTO userData, Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + id + " not found"));
        userMapper.update(userData, user);
        userRepository.save(user);
        return userMapper.map(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
