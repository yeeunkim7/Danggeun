//package org.example.danggeun.security;
//
//import org.example.danggeun.user.entity.User;
//import org.example.danggeun.user.repository.UserRepository;
//import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
//import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//
//import java.util.Map;
//import java.util.Optional;
//
//public class CustomOAuth2UserService extends DefaultOAuth2UserService {
//
//    private final UserRepository userRepository;
//
//    public CustomOAuth2UserService(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }
//
//    @Override
//    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
//        OAuth2User OAuth2User = super.loadUser(userRequest);
//
//        Map<String, Object> attributes = OAuth2User.getAttributes();
//        String email = (String) attributes.get("email");
//        String providerId = (String) attributes.get("sub");
//        String provider = userRequest.getClientRegistration().getRegistrationId();
//
//        Optional<User> userOptional = userRepository.findByProviderAndProviderId(provider, providerId);
//
//        User user = userOptional.orElseGet(() ->
//                userRepository.save(User.builder()
//                        .email(email)
//                        .provider(provider)
//                        .providerId(providerId)
//                        .build())
//        );
//
//        return OAuth2User;
//    }
//}