package com.example.demo.Config;

import com.example.demo.Model.Entities.PricesEntity;
import com.example.demo.Model.Entities.StoreLocationEntity;
import com.example.demo.Model.Entities.UserEntity;
import com.example.demo.Repositories.PricesRepository;
import com.example.demo.Repositories.StoreLocationRepository;
import com.example.demo.Repositories.UserRepository;
import com.example.demo.Security.Model.Entities.CredentialsEntity;
import com.example.demo.Security.Model.Entities.PermitEntity;
import com.example.demo.Security.Model.Entities.RoleEntity;
import com.example.demo.Security.Model.Enums.Permits;
import com.example.demo.Security.Model.Enums.Rol;
import com.example.demo.Security.Repositories.CredentialsRepository;
import com.example.demo.Security.Repositories.PermitRepository;
import com.example.demo.Security.Repositories.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Set;

@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PricesRepository pricesRepository;
    private final StoreLocationRepository storeLocationRepository;
    private final RoleRepository roleRepository;
    private final CredentialsRepository credentialsRepository;
    private final PasswordEncoder passwordEncoder;
    private final PermitRepository permitRepository;

    public DataLoader(UserRepository userRepository, PricesRepository pricesRepository, StoreLocationRepository storeLocationRepository, RoleRepository roleRepository, CredentialsRepository credentialsRepository, PasswordEncoder passwordEncoder, PermitRepository permitRepository) {
        this.userRepository = userRepository;
        this.pricesRepository = pricesRepository;
        this.storeLocationRepository = storeLocationRepository;
        this.roleRepository = roleRepository;
        this.credentialsRepository = credentialsRepository;
        this.passwordEncoder = passwordEncoder;
        this.permitRepository = permitRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        RoleEntity adminRole = new RoleEntity(Rol.administrador);
        RoleEntity clienteRole = new RoleEntity(Rol.cliente);

        if (roleRepository.count() == 0) {
            // 1. Crear Roles y Permisos (Si no existen)
            PermitEntity verCuenta = permitRepository.save(new PermitEntity(Permits.VER_CUENTA));
            PermitEntity crearCuenta = permitRepository.save(new PermitEntity(Permits.CREAR_CUENTA));
            PermitEntity modificarCuenta = permitRepository.save(new PermitEntity(Permits.MODIFICAR_CUENTA));
            PermitEntity eliminarCuenta = permitRepository.save(new PermitEntity(Permits.ELIMINAR_CUENTA));
            PermitEntity crearCarrito = permitRepository.save(new PermitEntity(Permits.CREAR_CARRITO));
            PermitEntity cargarPedido = permitRepository.save(new PermitEntity(Permits.CARGAR_PEDIDO));
            PermitEntity eliminarPedido = permitRepository.save(new PermitEntity(Permits.ELIMINAR_PEDIDO));
            PermitEntity verCarrito = permitRepository.save(new PermitEntity(Permits.VER_CARRITO));
            PermitEntity pagarCarrito = permitRepository.save(new PermitEntity(Permits.PAGAR_CARRITO));
            
            // Permisos exclusivos de admin
            PermitEntity verTodosPedidos = permitRepository.save(new PermitEntity(Permits.VER_TODOS_PEDIDOS));
            PermitEntity modificarEstadoPedido = permitRepository.save(new PermitEntity(Permits.MODIFICAR_ESTADO_PEDIDO));
            PermitEntity verTodosUsuarios = permitRepository.save(new PermitEntity(Permits.VER_TODOS_USUARIOS));
            PermitEntity modificarPrecios = permitRepository.save(new PermitEntity(Permits.MODIFICAR_PRECIOS));
            PermitEntity verEstadisticas = permitRepository.save(new PermitEntity(Permits.VER_ESTADISTICAS));

            // Agregar TODOS los permisos al admin
            adminRole.addPermit(verCuenta);
            adminRole.addPermit(crearCuenta);
            adminRole.addPermit(modificarCuenta);
            adminRole.addPermit(eliminarCuenta);
            adminRole.addPermit(eliminarPedido);
            adminRole.addPermit(verCarrito);
            adminRole.addPermit(verTodosPedidos);
            adminRole.addPermit(modificarEstadoPedido);
            adminRole.addPermit(verTodosUsuarios);
            adminRole.addPermit(modificarPrecios);
            adminRole.addPermit(verEstadisticas);

            roleRepository.save(adminRole);

            // Agregar permisos a cliente role
            clienteRole.addPermit(verCuenta);
            clienteRole.addPermit(crearCuenta);
            clienteRole.addPermit(modificarCuenta);
            clienteRole.addPermit(eliminarCuenta);
            clienteRole.addPermit(crearCarrito);
            clienteRole.addPermit(cargarPedido);
            clienteRole.addPermit(eliminarPedido);
            clienteRole.addPermit(verCarrito);
            clienteRole.addPermit(pagarCarrito);

            roleRepository.save(clienteRole);
        }

        if(userRepository.count() == 0){
            if (userRepository.count() == 0) {
                // 2. Crear el perfil del usuario (Datos Personales)
                UserEntity user = new UserEntity();
                user.setName("Usuario");
                user.setSurname("Prueba");
                user.setEmail("test@test.com");
                user.setPhone("123456789");
                userRepository.save(user);

                // 3. Crear las credenciales del usuario (Seguridad)
                // Esto es lo que permite que el sistema de login funcione
                CredentialsEntity credentials = new CredentialsEntity();
                credentials.setEmail(user.getEmail());
                credentials.setPassword(passwordEncoder.encode("cliente123")); // Password hasheada
                credentials.setUser(user); // Vinculamos con el perfil
                credentials.setRoles(Set.of(clienteRole)); // Asignar rol previamente creado

                credentialsRepository.save(credentials);

                System.out.println("Sistema inicializado con usuario cliente: test@test.com / cliente123");
                System.out.println("Id: "+user.getId());

                // Crear usuario Admin
                UserEntity adminUser = new UserEntity();
                adminUser.setName("Admin");
                adminUser.setSurname("Sistema");
                adminUser.setEmail("admin@admin.com");
                adminUser.setPhone("987654321");
                userRepository.save(adminUser);

                CredentialsEntity adminCredentials = new CredentialsEntity();
                adminCredentials.setEmail(adminUser.getEmail());
                adminCredentials.setPassword(passwordEncoder.encode("admin123"));
                adminCredentials.setUser(adminUser);
                adminCredentials.setRoles(Set.of(adminRole));
                credentialsRepository.save(adminCredentials);

                System.out.println("Sistema inicializado con usuario admin: admin@admin.com / admin123");

            PricesEntity prices = new PricesEntity();
            prices.setPricePerSheetBW(200);
            prices.setPriceRingedBinding(2000);
            prices.setPricePerSheetColor(400);
            prices.setValidFrom(Instant.now());
            prices.setValidTo(null);
            pricesRepository.save(prices);

            StoreLocationEntity location = new StoreLocationEntity();
            location.setLat(-38.039534600624734);
            location.setLng(-57.55232818919287);
            location.setAddress("7600, Magallanes 3899, B7603 Mar del Plata, Provincia de Buenos Aires");
            storeLocationRepository.save(location);
            }
        }
    }
}
