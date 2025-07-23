function previewImage(event) {
    const reader = new FileReader();
    reader.onload = () => {
        const img = document.getElementById('preview-image');
        img.src = reader.result;
        img.style.objectFit = 'cover';
    };
    if (event.target.files[0]) {
        reader.readAsDataURL(event.target.files[0]);
    }
}

document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("write-form");
    const submitBtn = document.getElementById("submit-btn");
    const imageInput = form.querySelector("input[type=file]");
    const categorySelect = document.getElementById("category-select");
    const categoryAddBtn = document.querySelector(".category-add-btn");
    const modal= document.querySelector(".category-modal");
    const closeBtn = document.getElementById("close-category-modal");
    const saveCategoryBtn  = document.getElementById("save-category-btn");
    const newCategoryInput = document.getElementById("new-category-name");

    categoryAddBtn.onclick = () => { newCategoryInput.value = ""; modal.style.display = "flex"; };
    closeBtn.onclick = () => modal.style.display = "none";
    window.addEventListener("click", e => {
        if (e.target === modal) modal.style.display = "none";
    });

    saveCategoryBtn.onclick = async () => {
        const name = newCategoryInput.value.trim();
        if (!name) {
            alert("카테고리 이름을 입력해주세요.");
            return;
        }
        try {
            const res = await fetch("/api/categories", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ name })
            });
            if (!res.ok) throw new Error(res.status);
            const { id, name: savedName } = await res.json();
            const opt = document.createElement("option");
            opt.value = id;
            opt.textContent = savedName;
            categorySelect.append(opt);
            categorySelect.value = id;
            modal.style.display = "none";
        } catch (err) {
            console.error(err);
            alert("카테고리 추가에 실패했습니다.");
        }
    };

    // 폼 비동기 제출 (이미지 포함)
    submitBtn.onclick = async e => {
        e.preventDefault();
        if (!imageInput.files[0]) {
            alert("이미지를 선택해주세요.");
            return;
        }
        const fd = new FormData(form);
        try {
            const res = await fetch(form.action, {
                method: form.method,
                body: fd
            });

            if (!res.ok) {
                const errText = await res.text();
                console.error(`서버 에러 ${res.status}:`, errText);
                throw new Error(`서버 에러 ${res.status}`);
            }

            alert("작성 완료!");
            window.location.href = "/trade/post";
        } catch (err) {
            console.error("작성 중 오류 발생:", err);
            alert("작성 중 문제가 발생했습니다. (콘솔을 확인하세요)");
        }
    };
});
