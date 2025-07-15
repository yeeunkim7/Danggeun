#!/bin/bash

# 1. Git 최신 소스코드 받아오기
echo "🔄 Git pull 중..."
git pull origin develop

# 2. PostgreSQL DB 복원
echo "🛠️ PostgreSQL DB 복원 시작..."
psql -U postgres -d danggeun < db/danggeun_dump.sql

# 3. 완료 메시지
echo "✅ DB 복원 완료!"
