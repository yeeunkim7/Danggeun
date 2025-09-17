#!/bin/bash
set -e
cd ~/app/Danggeun

echo "👉 최신 코드 가져오기"
git pull origin main

echo "👉 빌드 시작 (Maven Wrapper)"
chmod +x mvnw
./mvnw clean package -DskipTests

echo "👉 서버 재시작"
sudo systemctl restart danggeun

echo "✅ 배포 완료"
