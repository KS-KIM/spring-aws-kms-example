#!/bin/bash

# KMS 키가 이미 존재하는지 확인하는 함수
check_kms_key_exists() {
  while [[ $# -gt 0 ]]; do
    case $1 in
      --endpoint)
        endpoint="$2"
        shift 2
        ;;
      --region)
        region="$2"
        shift 2
        ;;
      --alias-name)
        alias_name="$2"
        shift 2
        ;;
      *)
        echo "Unknown parameter passed to check_kms_key_exists: $1"
        exit 1
        ;;
    esac
  done

  echo "Checking if KMS Key with alias '$alias_name' exists..."
  aws kms list-aliases \
    --endpoint-url "$endpoint" \
    --region "$region" \
    --query "Aliases[?AliasName=='$alias_name'] | length(@)" \
    --output text | grep -q "1"

  if [ $? -eq 0 ]; then
    echo "KMS Key with alias '$alias_name' already exists."
    return 0
  else
    echo "KMS Key with alias '$alias_name' does not exist."
    return 1
  fi
}

# KMS 키 생성 함수
create_kms_key() {
  while [[ $# -gt 0 ]]; do
    case $1 in
      --endpoint)
        endpoint="$2"
        shift 2
        ;;
      --region)
        region="$2"
        shift 2
        ;;
      --alias-name)
        alias_name="$2"
        shift 2
        ;;
      --rotation-days)
        rotation_days="$2"
        shift 2
        ;;
      *)
        echo "Unknown parameter passed to create_kms_key: $1\n"
        exit 1
        ;;
    esac
  done

  # 키 존재 여부 확인
  check_kms_key_exists \
    --endpoint "$endpoint" \
    --region "$region" \
    --alias-name "$alias_name"
  if [ $? -eq 0 ]; then
    echo "Skipping KMS Key creation as it already exists.\n"
    return
  fi

  echo "Creating KMS Key..."
  KEY_ID=$(aws kms create-key \
    --description "Example KMS Key with alias and rotation policy" \
    --key-usage ENCRYPT_DECRYPT \
    --origin AWS_KMS \
    --endpoint-url "$endpoint" \
    --region "$region" \
    --query KeyMetadata.KeyId \
    --output text)

  if [ $? -eq 0 ]; then
    echo "KMS Key Created: $KEY_ID\n"
    create_kms_alias \
      --key-id "$KEY_ID" \
      --endpoint "$endpoint" \
      --region "$region" \
      --alias-name "$alias_name"
    enable_key_rotation \
      --key-id "$KEY_ID" \
      --endpoint "$endpoint" \
      --region "$region" \
      --rotation-days "$rotation_days"
  else
    echo "Failed to create KMS Key.\n"
    exit 1
  fi
}

# KMS Alias 생성 함수
create_kms_alias() {
  while [[ $# -gt 0 ]]; do
    case $1 in
      --key-id)
        key_id="$2"
        shift 2
        ;;
      --endpoint)
        endpoint="$2"
        shift 2
        ;;
      --region)
        region="$2"
        shift 2
        ;;
      --alias-name)
        alias_name="$2"
        shift 2
        ;;
      *)
        echo "Unknown parameter passed to create_kms_alias: $1\n"
        exit 1
        ;;
    esac
  done

  echo "Creating Alias for KMS Key...\n"
  aws kms create-alias \
    --alias-name "$alias_name" \
    --target-key-id "$key_id" \
    --endpoint-url "$endpoint" \
    --region "$region"

  if [ $? -eq 0 ]; then
    echo "Alias Created: $alias_name\n"
  else
    echo "Failed to create Alias.\n"
    exit 1
  fi
}

# KMS 키 로테이션 활성화 함수
enable_key_rotation() {
  while [[ $# -gt 0 ]]; do
    case $1 in
      --key-id)
        key_id="$2"
        shift 2
        ;;
      --endpoint)
        endpoint="$2"
        shift 2
        ;;
      --region)
        region="$2"
        shift 2
        ;;
      --rotation-days)
        rotation_days="$2"
        shift 2
        ;;
      *)
        echo "Unknown parameter passed to enable_key_rotation: $1\n"
        exit 1
        ;;
    esac
  done

  echo "Enabling Key Rotation for KMS Key..."
  aws kms enable-key-rotation \
    --key-id "$key_id" \
    --endpoint-url "$endpoint" \
    --region "$region"

  if [ $? -eq 0 ]; then
    echo "Key Rotation Enabled for Key ID: $key_id"
    echo "Rotation interval of $rotation_days days set (manual simulation may be required).\n"
  else
    echo "Failed to enable Key Rotation.\n"
    exit 1
  fi
}

# DynamoDB 테이블 생성 함수
create_dynamodb_table() {
  while [[ $# -gt 0 ]]; do
    case $1 in
      --endpoint)
        endpoint="$2"
        shift 2
        ;;
      --region)
        region="$2"
        shift 2
        ;;
      --table-name)
        table_name="$2"
        shift 2
        ;;
      *)
        echo "Unknown parameter passed to create_dynamodb_table: $1\n"
        exit 1
        ;;
    esac
  done

  echo "Checking if table '$table_name' exists..."
  aws dynamodb describe-table \
    --endpoint-url "$endpoint" \
    --region "$region" \
    --table-name "$table_name" &>/dev/null

  if [ $? -eq 0 ]; then
    echo "Table '$table_name' already exists. Skipping creation.\n"
    return
  fi

  echo "Creating DynamoDB table: $table_name..."
  aws dynamodb create-table \
    --endpoint-url "$endpoint" \
    --region "$region" \
    --table-name "$table_name" \
    --attribute-definitions \
        AttributeName=branch-key-id,AttributeType=S \
        AttributeName=type,AttributeType=S \
    --key-schema \
        AttributeName=branch-key-id,KeyType=HASH \
        AttributeName=type,KeyType=RANGE \
    --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5


  if [ $? -eq 0 ]; then
    echo "Table '$table_name' created successfully.\n"
  else
    echo "Failed to create table '$table_name'.\n"
    exit 1
  fi
}

create_environment_file_to_resource() {
  while [[ $# -gt 0 ]]; do
    case $1 in
      --endpoint)
        endpoint="$2"
        shift 2
        ;;
      --region)
        region="$2"
        shift 2
        ;;
      --alias-name)
        alias_name="$2"
        shift 2
        ;;
      *)
        echo "Unknown parameter passed to print_arn: $1\n"
        exit 1
        ;;
    esac
  done

  KEY_ID=$(aws kms list-aliases \
    --endpoint-url "$endpoint" \
    --region "$region" \
    --query "Aliases[?AliasName=='${alias_name}'].TargetKeyId" \
    --output text)
  ARN="arn:aws:kms:ap-northeast-2:000000000000:key/$KEY_ID"

  echo "KMS_ARN=$ARN" > .env
  echo ".env file created with ARN: $ARN\n"
}

# 메인 함수
main() {
  local aws_dynamodb_endpoint="http://localhost:4566"
  local aws_kms_endpoint="http://localhost:4574"
  local aws_region="ap-northeast-2"
  local kms_key_alias="alias/kms-example"
  local kms_rotation_days=90
  local keystore_table_name="kms-key-store"

  echo "Starting KMS and DynamoDB setup...\n"

  create_kms_key \
    --endpoint "$aws_kms_endpoint" \
    --region "$aws_region" \
    --alias-name "$kms_key_alias" \
    --rotation-days "$kms_rotation_days"

  create_dynamodb_table \
    --endpoint "$aws_dynamodb_endpoint" \
    --region "$aws_region" \
    --table-name "$keystore_table_name"

  create_environment_file_to_resource \
    --endpoint "$aws_kms_endpoint" \
    --region "$aws_region" \
    --alias-name "$kms_key_alias"

  echo "Setup completed successfully."
}

# 실행
main