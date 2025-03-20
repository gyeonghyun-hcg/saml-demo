# SAML2 with keycloak
## Keyclock 실행
```bash
docker run -p 8081:8080 -e KC_BOOTSTRAP_ADMIN_USERNAME=admin -e KC_BOOTSTRAP_ADMIN_PASSWORD=admin quay.io/keycloak/keycloak:26.1.4 start-dev
```
### keyclock reaml 설정
- `http://localhost:8081` 접속
- `admin/admin`으로 로그인
- 새로운 realm 생성
- Reousrce file에 해당 레포의 `keycloak-realm/realm-export.json` 파일을 업로드하여 생성

## Endpoint
### 로그인
`http://localhost:8080/saml2/authenticate?registrationId=keycloak`
### 로그아웃
`http://localhost:8080/logout`

## Reference Documentation
- https://piotrminkowski.com/2024/10/28/spring-boot-with-saml2-and-keycloak/
