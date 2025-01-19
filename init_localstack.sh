#!/bin/bash

# 환경 변수 설정
AWS_ENDPOINT="http://localhost:4566"
AWS_REGION="ap-northeast-2"
KEY_ALIAS="alias/kms-example"
ROTATION_INTERVAL_DAYS=90

# 1. KMS 키 생성
KEY_ID=$(aws kms create-key \
  --description "Example KMS Key with alias and rotation policy" \
  --key-usage ENCRYPT_DECRYPT \
  --origin AWS_KMS \
  --endpoint-url $AWS_ENDPOINT \
  --region $AWS_REGION \
  --query KeyMetadata.KeyId \
  --output text)

echo "KMS Key Created: $KEY_ID"

# 2. Alias 생성
aws kms create-alias \
  --alias-name $KEY_ALIAS \
  --target-key-id $KEY_ID \
  --endpoint-url $AWS_ENDPOINT \
  --region $AWS_REGION

echo "Alias Created: $KEY_ALIAS"

# 3. Key Rotation 활성화
aws kms enable-key-rotation \
  --key-id $KEY_ID \
  --endpoint-url $AWS_ENDPOINT \
  --region $AWS_REGION

echo "Key Rotation Enabled for Key ID: $KEY_ID"

# 4. 키 로테이션 주기 설정 (LocalStack은 기본 로테이션 주기가 365일이므로 시뮬레이션)
# LocalStack의 경우 사용자 정의 로테이션 주기는 직접 처리해야 합니다.
echo "Rotation interval of $ROTATION_INTERVAL_DAYS days set (manual simulation may be required)."
