package app.user.model.service;

import app.exception.DomainException;
import app.subscription.model.service.SubscriptionService;
import app.user.model.User;
import app.user.model.UserRole;
import app.user.model.repository.UserRepository;
import app.wallet.model.service.WalletService;
import app.web.dto.LoginRequest;
import app.web.dto.RegisterRequest;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SubscriptionService subscriptionService;
    private final WalletService walletService;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, SubscriptionService subscriptionService, WalletService walletService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.subscriptionService = subscriptionService;
        this.walletService = walletService;
    }


    public User login(LoginRequest loginRequest){

        Optional<User> userOptional = userRepository.findByUsername(loginRequest.getUsername());
        if (userOptional.isEmpty()){
            throw  new DomainException("Username or password are incorrect");
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())){
            throw new DomainException("Username or password are incorrect");
        }

        return user;
    }
    @Transactional
    public User register(RegisterRequest registerRequest) {

        Optional<User> userOptional = userRepository.findByUsername(registerRequest.getUsername());
        if (userOptional.isPresent()){
            throw new DomainException("Username [%s] already exist.".formatted(registerRequest.getUsername()));
        }

        User user =userRepository.save(initializeUser(registerRequest));

        subscriptionService.createDefaultSubscription(user);
        walletService.createNewWallet(user);

        log.info("Successfully created new user account for username [%s] and id [%s]".formatted(user.getUsername(), user.getId()));

        return user;
    }
    private User initializeUser(RegisterRequest registerRequest){

        return User.builder()
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .userRole(UserRole.USER)
                .isActive(true)
                .country(registerRequest.getCountry())
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }


}
