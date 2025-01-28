#!/bin/bash

# 모든 KMS 키와 DynamoDB 테이블 삭제 함수
clean_up_resources() {
  local endpoint=""
  local region=""

  # 전달된 인자를 파싱
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
        echo "Unknown parameter passed to clean_up_resources: $1"
        exit 1
        ;;
    esac
  done

  echo "Starting cleanup process..."

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
      echo "Scheduled deletion for KMS key: $key_id"
    else
      echo "Failed to schedule deletion for KMS key: $key_id"
    fi
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

  echo "Cleanup process completed."
}

# 메인 함수
main() {
  local aws_endpoint="http://localhost:4566"
  local aws_region="ap-northeast-2"

  clean_up_resources \
    --endpoint "$aws_endpoint" \
    --region "$aws_region"
}

# 스크립트 실행
main
