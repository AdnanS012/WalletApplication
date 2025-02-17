package Config;

import Domain.User;
import Repository.IUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Seeder {
    @Bean
    public CommandLineRunner seedAdminUser(IUserRepository userRepository) {
        return args -> {
            if (userRepository.findByUsername("admin") == null) {
                User admin = new User("admin", "password");
                userRepository.save(admin);
                System.out.println("Admin user created: username=admin, password=admin");
            }else{
                System.out.println("Admin already exist in db");
            }
        };
    }
}
