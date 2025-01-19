# aws-kms-example

AWS KMS 대칭키 암호화 예제

- aws-encryption-sdk를 이용하여 대칭키 암호화/복호화 기능을 구현한다.
- 비용 효율 및 성능 이점을 얻기 위해 data key를 캐싱하여 kms 호출을 최소화한다.

## 초기 환경 구성

본 예제를 사용하기 위해 AWS CLI가 로컬에 설치되어 있어야 한다.

### LocalStack 실행

로컬에서 AWS KMS 서비스를 대체하기 위해 LocalStack을 사용한다.
프로젝트 루트에서 아래 명령어를 입력하여 도커 이미지를 실행 가능하다.

```shell
$ docker-compose up -d
```

### KMS 키 생성

키 생성을 위한 스크립트가 이미 준비되어 있다.

```shell
$ chmod +x init_localstack.sh
$ ./init_localstack.sh
```

### 어플리케이션 빌드

어플리케이션을 빌드한다. java 21.0.5를 사용한다. sdkman을 사용하여 프로젝트에 맞는 자바 버전을 설정할 수 있다.

```shell
$ ./gradlew build
```

### 실행

어플리케이션을 실행한다.

```shell
$  java -jar build/libs/aws-kms-example-0.0.1-SNAPSHOT.jar
```

### 테스트

[테스트 파일](./http/kms-test.http)을 이용하여 API 호출을 테스트한다.

## 참고

- [data key caching](https://docs.aws.amazon.com/encryption-sdk/latest/developer-guide/data-key-caching.html)
