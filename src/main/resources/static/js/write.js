function previewImage(event) {
    const input = event.target;
    const reader = new FileReader();

    reader.onload = function () {
        const preview = document.getElementById('preview-image');
        preview.src = reader.result;
        preview.style.objectFit = 'cover';
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

        const productCreatedAt = new Date().toISOString(); // 예: 2025-07-09T01:11:10.737Z

        console.log("productCreatedAt:", productCreatedAt);

        const imageFile = imageInput.files[0];
        if (!imageFile) {
            alert("이미지를 선택해주세요.");
            return;
        }

        const base64Img = await toBase64(imageFile);

        const payload = {
            productNm,
            productPrice: Number(productPrice),
            productDetail,
            address,
            productCreatedAt,
            productImg: base64Img.split(",")[1] // 'data:image/jpeg;base64,' 제거
        };

        console.log("Payload to send:", payload);

        fetch("/write", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
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
                jsonData = JSON.parse(responseText);
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

function toBase64(file) {
    return new Promise((resolve, reject) => {
        const reader = new FileReader();
        reader.onload = () => resolve(reader.result);
        reader.onerror = error => reject(error);
        reader.readAsDataURL(file);
    });
}