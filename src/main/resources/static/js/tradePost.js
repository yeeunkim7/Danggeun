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
      div.innerHTML = `
        <h3>${product.productNm || '상품명 없음'}</h3>
        <p>가격: ${product.productPrice} 원</p>
        <p>상세: ${product.productDetail || '설명 없음'}</p>
        <img src="${product.productImg}" alt="상품 이미지" style="max-width:200px;">
        <hr/>
      `;
      container.appendChild(div);
    });
  } catch (error) {
    console.error(error);
    alert('상품 정보를 불러오는 중 오류가 발생했습니다.');
  }
});