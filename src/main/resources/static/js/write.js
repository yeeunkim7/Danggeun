function previewImage(event) {
    const input = event.target;
    const reader = new FileReader();

    reader.onload = function () {
        const preview = document.getElementById('preview-image');
        preview.src = reader.result;
        preview.style.objectFit = 'cover'; // 꽉 채우기
    };

    if (input.files && input.files[0]) {
        reader.readAsDataURL(input.files[0]);
    }
}

document.addEventListener("DOMContentLoaded", () => {
    document.getElementById("write-form").addEventListener("submit", function (e) {
        e.preventDefault();
    });

    const submitBtn = document.getElementById("submit-btn");
    const imageInput = document.querySelector("input[name='productImage']");

    submitBtn.addEventListener("click", async () => {
        const productNm = document.getElementById("title").value;
        const productPrice = document.getElementById("productPrice").value;
        const productDetail = document.getElementById("productDetail").value;
        const address = document.getElementById("address").value;
        const productCreatedAt = new Date().toISOString();

        const imageFile = imageInput.files[0];
        if (!imageFile) {
            alert("이미지를 선택해주세요.");
            return;
        }

        const formData = new FormData();
        formData.append("productNm", productNm);
        formData.append("productPrice", productPrice);
        formData.append("productDetail", productDetail);
        formData.append("address", address);
        formData.append("productCreatedAt", productCreatedAt);
        formData.append("productImg", imageFile);

        fetch("/write", {
            method: "POST",
            body: formData
        })
        .then(async res => {
            const contentType = res.headers.get("content-type") || "";
            const responseText = await res.text();
            if (!res.ok) {
                console.error("서버 오류 응답:", responseText);
                throw new Error(`서버 오류: ${res.status}`);
            }

            let jsonData;
            try {
                if (contentType.includes("application/json")) {
                    jsonData = JSON.parse(responseText);
                } else {
                    throw new Error("JSON 형식 아님");
                }
            } catch (e) {
                console.error("JSON 파싱 실패:", e, responseText);
                throw new Error("응답 JSON 파싱 실패");
            }

            alert("작성 완료!");
            localStorage.setItem("lastPost", JSON.stringify(jsonData));
            window.location.href = "/trade/post";
        })
        .catch(err => {
            console.error("작성 실패:", err);
            alert("작성 중 오류가 발생했습니다.");
        });
    });
});

function showResult(data) {
    const container = document.getElementById("api-data");
    container.innerHTML = `
        <h2>작성된 글</h2>
        <img src="data:image/jpeg;base64,${data.productImg}"
             style="width:140px;height:140px;object-fit:cover;border-radius:8px;" />
        <p><strong>상품명:</strong> ${data.productNm}</p>
        <p><strong>가격:</strong> ${data.productPrice} 원</p>
        <p><strong>설명:</strong> ${data.productDetail}</p>
        <p><strong>주소:</strong> ${data.address}</p>
        <p><strong>등록일:</strong> ${new Date(data.productCreatedAt).toLocaleString()}</p>
    `;
}