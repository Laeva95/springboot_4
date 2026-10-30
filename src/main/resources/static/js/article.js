// [8장 · article.js 전체 안내]
// 이 파일은 글 작성/수정/삭제 화면(newArticle.html, article.html)의 "버튼 동작"을 담당합니다.
// 브라우저에서 버튼을 누르면 → 이 파일의 함수가 실행 → fetch()로 서버 API를 호출 → 결과를 화면에 반영
// 8장에서 추가된 부분: 이미지 업로드(/api/upload), AI 도움받기(/api/ai-suggestions), AI 섬네일(/api/ai-thumbnails)

// 페이지가 열릴 때(DOMContentLoaded) 실행됩니다.
// 수정 화면처럼 이미 이미지가 있는 글이면, 숨겨 둔 image-url 값으로 미리보기를 바로 보여 줍니다.
window.addEventListener('DOMContentLoaded', () => {
    // ?. (옵셔널 체이닝): 요소가 없으면 에러 없이 undefined가 됩니다.
    const imageUrl = document.getElementById('image-url')?.value;
    if (imageUrl) {
        displayImagePreview(imageUrl);
    }
});

// [8장 · 이미지 업로드] 파일 선택 상자(id=image-upload)에서 파일을 고르면 실행됩니다.
// 흐름: 파일 검사 → FormData에 담기 → POST /api/upload → 받은 imageUrl을 숨김칸에 저장 → 미리보기
// 이미지 업로드 버튼
const imageUpload = document.getElementById('image-upload');
if (imageUpload) {
    // async: 이 함수 안에서 기다림(await) 없이 fetch를 쓰지만, 나중에 기다림을 넣기 쉽도록 붙여 둡니다.
    imageUpload.addEventListener('change', async (event) => {
        // 사용자가 고른 파일 목록 중 첫 번째 파일을 꺼냅니다.
        const file = event.target.files[0];
        if (!file) return;

        // 확장자가 아니라 "MIME 타입"(image/png, image/jpeg 등)으로 이미지인지 확인합니다.
        if (!file.type.startsWith('image/')) {
            alert('이미지 파일만 업로드 가능합니다.');
            return;
        }

        // FormData: 파일을 "폼 전송"처럼 담는 상자입니다. key 이름 'file'은 서버의 @RequestParam("file")과 같아야 합니다.
        const formData = new FormData();
        formData.append('file', file);

        // POST /api/upload 로 파일을 보냅니다. (Content-Type은 브라우저가 자동으로 맞춰 줍니다)
        fetch('/api/upload', {
            method: 'POST',
            body: formData
        }).then((response) => {
            // 서버가 201(Created) 등 성공 코드를 주면 response.ok는 true입니다.
            if (!response.ok) {
                alert('이미지 업로드에 실패했습니다.');
                throw new Error();
            }
            // 응답 본문(JSON)을 자바스크립트 객체로 바꿉니다. 예) {imageUrl: "/uploads/...png"}
            return response.json();
        })
            .then((data) => {
                // 받은 주소를 숨김칸(image-url)에 저장합니다. 나중에 "등록" 버튼이 이 값을 서버로 보냅니다.
                document.getElementById('image-url').value = data.imageUrl;
                displayImagePreview(data.imageUrl);
            })
            .catch((e) => console.error(e));
    });
}

// [8장 · 이미지 미리보기] imageUrl이 있으면 미리보기 영역을 보여 주고, 그 주소를 <img>에 넣습니다.
function displayImagePreview(imageUrl) {
    const preview = document.getElementById('image-preview');
    const previewImg = document.getElementById('preview-img');

    if (preview && previewImg && imageUrl) {
        previewImg.src = imageUrl;
        preview.style.display = 'block';
    }
}

// [8장 · 이미지 제거] "이미지 제거" 버튼: 숨김칸, 파일 선택, 미리보기를 모두 비웁니다.
// 이미지 제거 버튼
const removeImageButton = document.getElementById('remove-image-btn');
if (removeImageButton) {
    removeImageButton.addEventListener('click', () => {
        document.getElementById('image-url').value = '';
        document.getElementById('image-upload').value = '';
        document.getElementById('image-preview').style.display = 'none';
    });
}

// [8장 · AI 섬네일 생성 버튼] 이 버튼은 기본적으로 숨겨져 있습니다(newArticle.html 참고).
// 무료 모델은 이미지 생성을 지원하지 않아서, 기능을 켜기 전까지는 사용되지 않습니다.
// AI 썸네일 생성 버튼
const aiThumbnailButton = document.getElementById('ai-thumbnail-btn');
if (aiThumbnailButton) {
    aiThumbnailButton.addEventListener('click', async () => {
        const title = document.getElementById('title').value;
        const content = document.getElementById('content').value;

        // 제목과 본문이 모두 비어 있으면 생성할 근거가 없으니 막습니다. (trim: 앞뒤 공백 제거)
        if (!title.trim() && !content.trim()) {
            alert('제목이나 내용을 먼저 입력해주세요.');
            return;
        }

        // 생성에 시간이 걸리므로 로딩 문구를 보여 주고, 중복 클릭을 막으려고 버튼을 잠급니다.
        const loadingDiv = document.getElementById('ai-thumbnail-loading');
        loadingDiv.style.display = 'block';
        aiThumbnailButton.disabled = true;

        // POST /api/ai-thumbnails 호출. 성공하면 받은 imageUrl을 숨김칸에 저장하고 미리보기를 보여 줍니다.
        fetch('/api/ai-thumbnails', {
            method: 'POST',
            body: JSON.stringify({
                title: title,
                content: content
            }),
            headers: {
                'Content-Type': 'application/json',
            },
        }).then((response) => {
            if (!response.ok) {
                alert('썸네일 생성에 실패했습니다.');
                throw new Error();
            }
            return response.json();
        }).then((data) => {
            document.getElementById('image-url').value = data.imageUrl;
            displayImagePreview(data.imageUrl);
        }).finally(() => {
            // 성공하든 실패하든 마지막에는 로딩 문구를 숨기고 버튼을 다시 누를 수 있게 합니다.
            loadingDiv.style.display = 'none';
            aiThumbnailButton.disabled = false;
        });
    });
}


// 삭제 기능 (수정 화면에서 "삭제" 버튼이 있을 때만 동작)
const deleteButton = document.getElementById('delete-btn');

if (deleteButton) {
    deleteButton.addEventListener('click', event => {
        let id = document.getElementById('article-id').value;
        fetch(`/api/articles/${id}`, {
            method: 'DELETE'
        })
            .then(() => {
                alert('삭제가 완료되었습니다.');
                location.replace('/articles');
            });
    });
}

// 수정 기능: 주소창의 ?id=번호 를 읽어서 그 글을 PUT 요청으로 수정합니다.
// 수정 때도 imageUrl을 같이 보내야 이미지가 유지되거나 바뀝니다. (8장에서 추가된 부분)
const modifyButton = document.getElementById('modify-btn');

if (modifyButton) {
    modifyButton.addEventListener('click', event => {
        let params = new URLSearchParams(location.search);
        let id = params.get('id');

        fetch(`/api/articles/${id}`, {
            method: 'PUT',
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                title: document.getElementById('title').value,
                content: document.getElementById('content').value,
                imageUrl: document.getElementById('image-url').value
            })
        })
            .then(() => {
                alert('수정이 완료되었습니다.');
                location.replace(`/articles/${id}`);
            });
    });
}

// 생성(등록) 기능: 제목, 본문, 이미지 주소(imageUrl)를 POST /api/articles로 보냅니다.
// 이미지 주소는 위의 업로드 단계에서 숨김칸(image-url)에 저장해 둔 값입니다.
const createButton = document.getElementById('create-btn');

if (createButton) {
    createButton.addEventListener('click', event => {
        fetch('/api/articles', {
            method: 'POST',
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                title: document.getElementById('title').value,
                content: document.getElementById('content').value,
                imageUrl: document.getElementById('image-url').value
            })
        })
            .then(() => {
                alert('등록 완료되었습니다.');
                location.replace('/articles');
            });
    });
}

// [8장 · 글쓰기 도우미] "AI 도움받기" 버튼을 누르면 모달 창(글쓰기 도우미)을 엽니다.
// 창을 열 때마다 이전 제안 영역과 질문 칸을 비워서 깨끗한 상태로 보여 줍니다.
const aiAssistButton = document.getElementById('ai-assist-btn');

if (aiAssistButton) {
    aiAssistButton.addEventListener('click', event => {
        // $('#aiAssistModal').modal('show')는 부트스트랩(jQuery) 기능으로 모달 창을 띄웁니다.
        $('#aiAssistModal').modal('show');
        document.getElementById('ai-suggestion').style.display = 'none';
        document.getElementById('ai-question').value = '';
    });
}

// [8장 · 글쓰기 도우미] 모달 안의 "제안 받기" 버튼: 지금 쓰는 제목·본문·질문을 모아 AI에게 보냅니다.
const getSuggestionButton = document.getElementById('get-suggestion-btn');

if (getSuggestionButton) {
    getSuggestionButton.addEventListener('click', event => {
        const title = document.getElementById('title').value;
        const content = document.getElementById('content').value;
        const question = document.getElementById('ai-question').value;

        // 질문이 비어 있으면 AI가 무엇을 도와줄지 알 수 없으므로 막습니다.
        if (!question.trim()) {
            alert('고민되는 내용을 입력해주세요.');
            return;
        }

        // 로딩 표시
        document.getElementById('ai-loading').style.display = 'block';
        document.getElementById('ai-suggestion').style.display = 'none';

        // 서버(WritingSuggestionRequest record)의 필드 이름(title, content, question)과 똑같은 키로 JSON을 만듭니다.
        const body = JSON.stringify({
            title: title,
            content: content,
            question: question
        });

        // POST /api/ai-suggestions 호출 → 서버의 WritingAssistantService가 AI에게 물어본 결과를 받습니다.
        fetch('/api/ai-suggestions', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: body
        })
            .then(response => {
                return response.json();
            })
            .then(data => {
                // 응답이 도착했으니 로딩 문구를 숨깁니다.
                document.getElementById('ai-loading').style.display = 'none';

                const suggestionContent = document.getElementById('ai-suggestion-content');

                // 제안 목록(data.suggestions)을 클릭 가능한 목록 HTML로 만듭니다.
                let html = '';
                if (data.suggestions && data.suggestions.length > 0) {
                    html += '<ul class="list-group">';
                    data.suggestions.forEach((suggestion, index) => {
                        // data-suggestion: 클릭했을 때 본문에 넣을 글을 담아 둡니다. 큰따옴표는 &quot;로 바꿔 깨짐을 막습니다.
                        html += `<li class="list-group-item suggestion-item" style="cursor: pointer;" data-suggestion="${suggestion.replace(/"/g, '&quot;')}" title="클릭하면 본문에 추가됩니다">
                                ${suggestion}
                                <small class="text-muted float-right">클릭하여 추가</small>
                             </li>`;
                    });
                    html += '</ul>';
                }

                suggestionContent.innerHTML = html;
                document.getElementById('ai-suggestion').style.display = 'block';
            })
    });
}

// [8장 · 글쓰기 도우미] 제안 목록 중 하나를 클릭하면 본문 끝에 그 문장을 붙이고 모달을 닫습니다.
// 이벤트 위임: 목록 항목이 나중에 만들어지므로 부모(suggestionContent)에 클릭 이벤트를 한 번만 답니다.
const suggestionContent = document.getElementById('ai-suggestion-content');
if (suggestionContent) {
    suggestionContent.addEventListener('click', function(e) {
        // 클릭한 곳에서 가장 가까운 제안 항목(.suggestion-item)을 찾습니다.
        const suggestionItem = e.target.closest('.suggestion-item');
        if (suggestionItem) {
            const suggestion = suggestionItem.getAttribute('data-suggestion');
            const contentTextarea = document.getElementById('content');

            // 본문 끝에 줄바꿈 두 번을 넣고 제안을 붙입니다. (이미 줄바꿈으로 끝나면 줄바꿈을 더 넣지 않습니다)
            const currentContent = contentTextarea.value;
            const separator = currentContent && !currentContent.endsWith('\n') ? '\n\n' : '';
            contentTextarea.value = currentContent + separator + suggestion;

            $('#aiAssistModal').modal('hide');

            contentTextarea.focus();
        }
    });
}