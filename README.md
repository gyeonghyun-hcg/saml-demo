# SAML2 with keycloak
## Keyclock 실행
```bash
docker run -p 8081:8080 -e KC_BOOTSTRAP_ADMIN_USERNAME=admin -e KC_BOOTSTRAP_ADMIN_PASSWORD=admin quay.io/keycloak/keycloak:26.1.4 start-dev
```

## Endpoint
### 로그인
`http://localhost:8080/saml2/authenticate?registrationId=keycloak`
### 로그아웃
`http://localhost:8080/logout`

## Reference Documentation
- https://piotrminkowski.com/2024/10/28/spring-boot-with-saml2-and-keycloak/
