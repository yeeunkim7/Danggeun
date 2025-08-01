DELETE FROM chat;
DELETE FROM product;
DELETE FROM item;
DELETE FROM message;
DELETE FROM category;
DELETE FROM "user";

-- 유저 (user_id = 1)
INSERT INTO "user" (
  user_id, provider, provider_id, role,
  user_createdat, user_email, user_nm, user_password,
  user_phone, user_created_at, nickname, profile_image_url, region
) VALUES (
  1, 'local', 'local_1', 'USER',
  NOW(), 'test@example.com', '테스트유저', '1234',
  '010-0000-0000', NOW(), 'testnick', NULL, '서울시 강남구'
);

-- 카테고리 (1~3번)
INSERT INTO category (category_id, category_nm) VALUES (1, '전자기기');
INSERT INTO category (category_id, category_nm) VALUES (2, '가구');
INSERT INTO category (category_id, category_nm) VALUES (3, '도서');

-- 상품: 노트북
INSERT INTO product (
  product_state, category_id, product_created_at,
  product_price, user_id2, product_nm,
  address, product_detail, product_img,
  chats, image_url, product_name, title, views, user_id
) VALUES (
  'S', 1, NOW(),
  500000, 1, '노트북 상품',
  NULL, NULL, NULL,
  0, NULL, '삼성 노트북', '삼성 갤럭시북 노트북 팝니다', 0, 1
);

-- 상품: 책상
INSERT INTO product (
  product_state, category_id, product_created_at,
  product_price, user_id2, product_nm,
  address, product_detail, product_img,
  chats, image_url, product_name, title, views, user_id
) VALUES (
  'S', 2, NOW(),
  30000, 1, '책상 상품',
  NULL, NULL, NULL,
  0, NULL, '심플한 책상', '이케아 책상 저렴하게 팝니다', 0, 1
);

-- 상품: 참고서
INSERT INTO product (
  product_state, category_id, product_created_at,
  product_price, user_id2, product_nm,
  address, product_detail, product_img,
  chats, image_url, product_name, title, views, user_id
) VALUES (
  'S', 3, NOW(),
  10000, 1, '참고서 상품',
  NULL, NULL, NULL,
  0, NULL, '정보처리기사 참고서', '2024 최신판 정보처리기사 필기 참고서 팝니다', 0, 1
);
