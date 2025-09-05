document.addEventListener('DOMContentLoaded', async () => {
  try {
    const response = await fetch('/api/products');
    if (!response.ok) throw new Error('데이터를 불러오는 데 실패했습니다.');

    const products = await response.json();
    const container = document.getElementById('product-list');

    if (!container) return;
    container.innerHTML = '';

    products.forEach(product => {
      const div = document.createElement('div');
      div.className = 'product-item';

      // 이미지 URL 안전 처리
      const imageUrl = resolveProductImageUrl(product.productImg || product.imageUrl, product.productNm);

      div.innerHTML = `
        <div class="product-card">
          <div class="product-image-container">
            <img src="${imageUrl}"
                 alt="상품 이미지"
                 class="product-image"
                 onerror="this.onerror=null; this.src='${getDefaultImage(product.productNm)}'">
          </div>
          <div class="product-info">
            <h3 class="product-title">${escapeHtml(product.productNm || '상품명 없음')}</h3>
            <p class="product-price">${formatPrice(product.productPrice)}원</p>
            <p class="product-detail">${escapeHtml(product.productDetail || '설명 없음')}</p>
          </div>
        </div>
        <hr/>
      `;
      container.appendChild(div);
    });
  } catch (error) {
    console.error('상품 목록 로드 오류:', error);
    const container = document.getElementById('product-list');
    if (container) {
      container.innerHTML = '<p class="error-message">상품 정보를 불러오는 중 오류가 발생했습니다.</p>';
    }
  }
});

/**
 * 상품 이미지 URL을 안전하게 처리
 */
function resolveProductImageUrl(imageUrl, productName) {
  if (imageUrl && imageUrl.trim() !== '') {
    // HTTP URL인 경우 (S3 등)
    if (imageUrl.startsWith('http')) {
      return imageUrl;
    }
    // 절대 경로인 경우
    if (imageUrl.startsWith('/')) {
      return imageUrl;
    }
    // 파일명만 있는 경우
    return `/img/${imageUrl}`;
  }

  // 이미지가 없는 경우 제품명에 따른 기본 이미지
  return getDefaultImage(productName);
}

/**
 * 상품명에 따른 기본 이미지 반환
 */
function getDefaultImage(productName) {
  if (!productName) return '/img/mac.jpeg';

  const name = productName.toLowerCase();
  if (name.includes('노트북') || name.includes('맥북') || name.includes('laptop') || name.includes('갤럭시북')) {
    return '/img/mac.jpeg';
  } else if (name.includes('자전거') || name.includes('bike')) {
    return '/img/bike.jpeg';
  } else if (name.includes('책') || name.includes('book')) {
    return '/img/book.jpeg';
  } else if (name.includes('책상') || name.includes('desk')) {
    return '/img/desk.jpeg';
  } else if (name.includes('그램') || name.includes('gram')) {
    return '/img/gram.jpeg';
  }

  return '/img/mac.jpeg'; // 기본값
}

/**
 * 가격 포맷팅
 */
function formatPrice(price) {
  if (price == null || price === '') return '0';
  return Number(price).toLocaleString();
}

/**
 * HTML 이스케이프 처리
 */
function escapeHtml(text) {
  const div = document.createElement('div');
  div.textContent = text || '';
  return div.innerHTML;
}