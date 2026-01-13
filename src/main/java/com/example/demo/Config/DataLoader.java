package com.example.demo.Config;

import com.example.demo.Model.Entities.PricesEntity;
import com.example.demo.Model.Entities.UserEntity;
import com.example.demo.Model.Enums.UserRoleEnum;
import com.example.demo.Repositories.PricesRepository;
import com.example.demo.Repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;

@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PricesRepository pricesRepository;

    public DataLoader(UserRepository userRepository, PricesRepository pricesRepository) {
        this.userRepository = userRepository;
        this.pricesRepository = pricesRepository;
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

            PricesEntity prices = new PricesEntity();
            prices.setPricePerSheetBW(200);
            prices.setPriceRingedBinding(2000);
            prices.setPricePerSheetColor(400);
            prices.setValidFrom(Instant.now());
            String fechaString = "2026-07-20T09:00:00Z";
            Instant instanteDesdeString = Instant.parse(fechaString);
            prices.setValidTo(instanteDesdeString);
            pricesRepository.save(prices);
        }
    }
}
