package com.example.demo.Config;

import com.example.demo.Model.Entities.UserEntity;
import com.example.demo.Model.Enums.UserRoleEnum;
import com.example.demo.Repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;

    public DataLoader(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if(userRepository.count() == 0){
            UserEntity user = new UserEntity();
            user.setUsername("User1");
            user.setName("Usuario");
            user.setSurname("Prueba");
            user.setEmail("test@test.com");
            user.setRole(UserRoleEnum.GUEST);
            user.setPhone("123456789");
            user.setPassword("1234");
            userRepository.save(user);
            System.out.println("Usuario id: "+user.getId());
        }
    }
}
