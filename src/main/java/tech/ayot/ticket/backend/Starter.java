package tech.ayot.ticket.backend;


import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import tech.ayot.ticket.backend.model.user.User;
import tech.ayot.ticket.backend.repository.user.UserRepository;

@Component
class Starter implements ApplicationListener<ContextRefreshedEvent> {

    private final UserRepository userRepository;

    private final PlatformTransactionManager transactionManager;

    private final PasswordEncoder passwordEncoder;

    public Starter(
        UserRepository userRepository,
        PlatformTransactionManager transactionManager,
        PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.transactionManager = transactionManager;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public void onApplicationEvent(@NonNull ContextRefreshedEvent contextRefreshedEvent) {
        TransactionStatus transactionStatus = transactionManager.getTransaction(null);
        initialize();
        transactionManager.commit(transactionStatus);
    }


    private void initialize() {
        createAdminUser();
    }

    private void createAdminUser() {
        User adminUser = userRepository.findUserByUsername("admin");
        if (adminUser == null) {
            adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.encode("admin"));
            userRepository.save(adminUser);
        }
    }

}