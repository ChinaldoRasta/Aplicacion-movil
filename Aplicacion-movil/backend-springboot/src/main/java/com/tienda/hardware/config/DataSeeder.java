package com.tienda.hardware.config;

import com.tienda.hardware.model.*;
import com.tienda.hardware.repo.*;
import java.math.BigDecimal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {

  @Bean
  CommandLineRunner seed(UserRepository users, ProductRepository products, PasswordEncoder encoder) {
    return args -> {
      if(!users.existsByEmail("admin@tienda.com")) {
        users.save(new User("Administrador", "admin@tienda.com", encoder.encode("Admin123*"), Role.ADMIN));
      }
      if(!users.existsByEmail("user@tienda.com")) {
        users.save(new User("Cliente Demo", "user@tienda.com", encoder.encode("User123*"), Role.CLIENTE));
      }

      if(products.count() == 0) {
        products.save(new Product("Teclado", "Logitech", "N/A", 15, new BigDecimal("599.00")));
        products.save(new Product("Mouse", "Razer", "N/A", 8, new BigDecimal("899.00")));
        products.save(new Product("SSD", "Kingston", "1TB", 12, new BigDecimal("1299.00")));
        products.save(new Product("USB", "SanDisk", "128GB", 30, new BigDecimal("249.00")));
        products.save(new Product("Audífonos", "HyperX", "N/A", 10, new BigDecimal("1099.00")));
      }
    };
  }
}
