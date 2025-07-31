-- 유저 (존재하지 않으면 필요)
INSERT INTO "user" (id, email, nickname, profile_image_url, region)
VALUES (1, 'testuser@example.com', '테스트유저', '/images/default-profile.png', '서울시 마포구');

-- 카테고리
INSERT INTO category (id, name)
VALUES (1, '자전거'), (2, '가구'), (3, '도서');

-- 상품 아이템 (item)
INSERT INTO item (id, title, content, price, first_image_url, location, status, category_id, seller_id, created_at)
VALUES
(1, '자전거 팝니다', '새 자전거 판매합니다.', 100000, '/img/sample1.jpeg', '서울시 마포구', 'SALE', 1, 1, NOW()),
(2, '책상 판매', '사용감 적은 책상입니다.', 50000, '/img/sample2.jpeg', '서울시 강남구', 'SALE', 2, 1, NOW()),
(3, '수능 참고서 모음', '국어, 수학, 영어 참고서 포함', 20000, '/img/sample3.jpeg', '서울시 은평구', 'SALE', 3, 1, NOW());

-- item_image 테이블이 존재한다면 (없다면 생략)
-- INSERT INTO item_image (image_url, item_id) VALUES
-- ('/img/sample1.jpeg', 1),
-- ('/img/sample2.jpeg', 2),
-- ('/img/sample3.jpeg', 3);
