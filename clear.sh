#!/bin/bash

clean_up_kms_resources() {
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
      *)
        echo "Unknown parameter passed to clean_up_resources: $1\n"
        exit 1
        ;;
    esac
  done

  # 모든 KMS 키 삭제
  echo "Listing all KMS keys..."
  local key_ids
  key_ids=$(aws kms list-keys \
    --endpoint-url "$endpoint" \
    --region "$region" \
    --query "Keys[*].KeyId" \
    --output text)

  for key_id in $key_ids; do
    echo "Deleting KMS key: $key_id"

    # 키 삭제를 위해 비활성화
    aws kms schedule-key-deletion \
      --endpoint-url "$endpoint" \
      --region "$region" \
      --key-id "$key_id" \
      --pending-window-in-days 7

    if [ $? -eq 0 ]; then
      echo "Scheduled deletion for KMS key: $key_id\n"
    else
      echo "Failed to schedule deletion for KMS key: $key_id\n"
    fi
  done

  echo "Cleanup KMS keys completed.\n"
}

clean_up_dynamo_db_resources() {
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
      *)
        echo "Unknown parameter passed to clean_up_resources: $1\n"
        exit 1
        ;;
    esac
  done

  # 모든 DynamoDB 테이블 삭제
  echo "Listing all DynamoDB tables..."
  local table_names
  table_names=$(aws dynamodb list-tables \
    --endpoint-url "$endpoint" \
    --region "$region" \
    --query "TableNames" \
    --output text)

  for table_name in $table_names; do
    echo "Deleting DynamoDB table: $table_name"

    aws dynamodb delete-table \
      --endpoint-url "$endpoint" \
      --region "$region" \
      --table-name "$table_name"

    if [ $? -eq 0 ]; then
      echo "Successfully deleted DynamoDB table: $table_name"
    else
      echo "Failed to delete DynamoDB table: $table_name"
    fi
  done

  echo "Cleanup DynamoDB tables completed.\n"
}

# 메인 함수
main() {
  local aws_dynamodb_endpoint="http://localhost:4566"
  local aws_kms_endpoint="http://localhost:4574"
  local aws_region="ap-northeast-2"

  echo "Starting cleanup process...\n"

  clean_up_kms_resources \
    --endpoint "$aws_kms_endpoint" \
    --region "$aws_region"

  clean_up_dynamo_db_resources \
    --endpoint "$aws_dynamodb_endpoint" \
    --region "$aws_region"

  echo "Cleanup process completed."
}

# 스크립트 실행
main
