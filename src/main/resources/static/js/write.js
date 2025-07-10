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
    const form = document.getElementById("write-form");
    const submitBtn = document.getElementById("submit-btn");
    const imageInput = document.querySelector("input[name='productImage']");

    submitBtn.addEventListener("click", async (e) => {
        e.preventDefault();

        const formData = new FormData(form);

        const imageFile = imageInput.files[0];
        if (!imageFile) {
            alert("이미지를 선택해주세요.");
            return;
        }

        formData.append("productCreatedAt", new Date().toISOString());
        formData.append("productImage", imageFile); // 서버에서 @RequestParam("productImage")로 받음

        fetch("/write", {
            method: "POST",
            body: formData
        })
        .then(res => {
            if (!res.ok) throw new Error("작성 실패");
            alert("작성 완료!");
            window.location.href = "/trade/post";
        })
        .catch(err => {
            console.error("작성 중 오류 발생:", err);
            alert("작성 중 문제가 발생했습니다.");
        });
    });
});