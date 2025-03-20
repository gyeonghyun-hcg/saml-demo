package com.example.samldemo.config;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.saml2.core.Saml2ResponseValidatorResult;
import org.springframework.security.saml2.provider.service.authentication.OpenSaml4AuthenticationProvider;
import org.springframework.security.saml2.provider.service.authentication.Saml2Authentication;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticationToken;
import org.springframework.security.saml2.provider.service.registration.InMemoryRelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistration;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrations;
import org.springframework.security.saml2.provider.service.registration.Saml2MessageBinding;
import org.springframework.security.saml2.provider.service.web.DefaultRelyingPartyRegistrationResolver;
import org.springframework.security.saml2.provider.service.web.RelyingPartyRegistrationResolver;
import org.springframework.security.saml2.provider.service.web.authentication.OpenSaml4AuthenticationRequestResolver;
import org.springframework.security.saml2.provider.service.web.authentication.Saml2AuthenticationRequestResolver;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SamlConfig {

  private static final Logger LOG = LoggerFactory.getLogger(SamlConfig.class);

  @Bean
  public RelyingPartyRegistrationRepository relyingPartyRegistrationRepository() throws IOException {
    ClassPathResource resource = new ClassPathResource("descriptor.xml");
    RelyingPartyRegistration registration = RelyingPartyRegistrations
        .fromMetadata(resource.getInputStream())
        .registrationId("keycloak")
        .singleLogoutServiceLocation("http://localhost:8080/logout/saml2/slo")
        .singleLogoutServiceBinding(Saml2MessageBinding.POST)
        .build();

    return new InMemoryRelyingPartyRegistrationRepository(registration);
  }

  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    OpenSaml4AuthenticationProvider provider = new OpenSaml4AuthenticationProvider();
    provider.setResponseValidator((responseToken) -> {
      Saml2ResponseValidatorResult result = OpenSaml4AuthenticationProvider
          .createDefaultResponseValidator()
          .convert(responseToken);
      LOG.info("SAML2 RESPONSE: {}", responseToken.getToken().getSaml2Response());
      return result;
    });

    provider.setResponseAuthenticationConverter(responseToken -> {
      Saml2Authentication authentication = OpenSaml4AuthenticationProvider.createDefaultResponseAuthenticationConverter().convert(responseToken);
      return authentication;
    });

    http.csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(authorize -> authorize.anyRequest()
                                                     .authenticated())
        .saml2Login(saml2 -> {
                      try {
                        saml2
                            .authenticationManager(new ProviderManager(provider))
                            .relyingPartyRegistrationRepository(relyingPartyRegistrationRepository());
                      } catch (IOException e) {
                        throw new RuntimeException(e);
                      }
                    }
        )
        .saml2Logout(logout -> logout
            .logoutUrl("/logout")
            .logoutRequest(request -> request.logoutUrl("/logout/saml2/slo")))
        .saml2Metadata(withDefaults());
    return http.build();
  }
}
