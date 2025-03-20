# SAML2 with keycloak
### Keyclock 실행
```bash
docker run -p 8081:8080 -e KC_BOOTSTRAP_ADMIN_USERNAME=admin -e KC_BOOTSTRAP_ADMIN_PASSWORD=admin quay.io/keycloak/keycloak:26.1.4 start-dev
```

### Reference Documentation
- https://piotrminkowski.com/2024/10/28/spring-boot-with-saml2-and-keycloak/
