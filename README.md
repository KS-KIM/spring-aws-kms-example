# aws-kms-example

AWS KMS 대칭키 암호화 예제

- aws-encryption-sdk를 이용하여 대칭키 암호화/복호화 기능을 구현한다.
- 비용 효율 및 성능 이점을 얻기 위해 data key를 캐싱하여 kms 호출을 최소화한다.

## 초기 환경 구성

본 예제를 사용하기 위해 AWS CLI가 로컬에 설치되어 있어야 한다.

### docker compose 실행

DynamoDB를 로컬에서 시뮬레이션 하기 위해 Localstack을 사용한다.
KMS는 Localstack에서 reencrypt 함수를 지원하지 않으므로 local-kms 도커 이미지를 사용한다.
프로젝트 루트에서 아래 명령어를 입력하여 도커 이미지를 실행 가능하다.

```shell
$ docker-compose up -d
```

### KMS 키 생성

키 생성을 위한 스크립트가 이미 준비되어 있다. 졍상적으로 수행되지 않는 경우 `docker compose down` 하여 인스턴스 제거 후 재시도한다.

```shell
$ chmod +x init.sh
$ ./init.sh
```

### 어플리케이션 빌드

어플리케이션을 빌드한다. java 21.0.5를 사용한다. sdkman을 사용하여 프로젝트에 맞는 자바 버전을 설정할 수 있다.

```shell
$ ./gradlew build
```

### 실행

어플리케이션을 실행한다.

```shell
$ java -jar build/libs/aws-kms-example-0.0.1-SNAPSHOT.jar
```

### 테스트

[키 생성/로테이션 테스트](./http/branch-key-test.http), [암호화/복호화 테스트](./http/encryption-test.http)를 이용하여 API 호출을 테스트한다.

## 참고

- [data key caching](https://docs.aws.amazon.com/encryption-sdk/latest/developer-guide/data-key-caching.html)
