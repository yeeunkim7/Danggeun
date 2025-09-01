// areaConfirm.js

// ===== 전역 변수 =====
let map, marker, circle, geocoder;
let autocomplete;
let currentAddress = null;
let currentLat = null;
let currentLng = null;
let isMapInitialized = false;

/**
 * Google Maps 스크립트가 로드되면 호출되는 콜백
 */
function initMap() {
  console.log('initMap 함수 호출됨');

  const mapContainer = document.getElementById('map');
  if (!mapContainer) {
    showError('지도를 표시할 영역을 찾을 수 없습니다.');
    return;
  }

  // 서울 시청 근처를 기본 중심으로
  const defaultCenter = { lat: 37.5665, lng: 126.9780 };

  try {
    map = new google.maps.Map(mapContainer, {
      center: defaultCenter,
      zoom: 16,
      mapTypeControl: false,
      streetViewControl: false,
      fullscreenControl: false
    });

    marker = new google.maps.Marker({
      position: defaultCenter,
      map: map,
      draggable: true,
      title: '선택된 위치'
    });

    circle = new google.maps.Circle({
      map: map,
      center: defaultCenter,
      radius: 300,
      strokeColor: '#00a0e9',
      strokeOpacity: 0.8,
      strokeWeight: 2,
      fillColor: '#cceeff',
      fillOpacity: 0.6
    });

    geocoder = new google.maps.Geocoder();

    // 지도 클릭 이벤트
    map.addListener('click', (event) => {
      const clickLat = event.latLng.lat();
      const clickLng = event.latLng.lng();
      updateMarkerAndCircle(event.latLng);
      geocodeLatLng(clickLat, clickLng);
    });

    // 마커 드래그 이벤트
    marker.addListener('dragend', (event) => {
      const lat = event.latLng.lat();
      const lng = event.latLng.lng();
      updateCircleCenter(event.latLng);
      geocodeLatLng(lat, lng);
    });

    // Places Autocomplete 초기화
    initAutocomplete();

    isMapInitialized = true;

    // 기존 동네 인증 정보가 있으면 표시
    if (window.existingArea) {
      loadExistingArea();
    } else {
      // 페이지 진입 시 현재 위치 자동 시도
      setLocation();
    }

    console.log('Google Maps 초기화 완료');
  } catch (error) {
    console.error('Google Maps 초기화 실패:', error);
    showError('지도 초기화에 실패했습니다: ' + error.message);
  }
}

/**
 * Autocomplete 초기화
 */
function initAutocomplete() {
  const input = document.getElementById('address');
  if (!input) {
    console.error('address input 요소를 찾을 수 없습니다.');
    return;
  }

  try {
    autocomplete = new google.maps.places.Autocomplete(input, {
      fields: ['formatted_address', 'geometry'],
      componentRestrictions: { country: 'kr' }
    });

    autocomplete.addListener('place_changed', () => {
      const place = autocomplete.getPlace();
      if (!place || !place.geometry) {
        showError('선택한 장소에 위치 정보가 없습니다.');
        return;
      }

      const loc = place.geometry.location;
      updateMarkerAndCircle(loc);

      const lat = loc.lat();
      const lng = loc.lng();
      const addr = place.formatted_address || input.value;

      setCurrent(addr, lat, lng);
      writeLocationText(`선택된 위치: ${addr}`);
      enableConfirmButton();
      hideError();
    });

    console.log('Autocomplete 초기화 완료');
  } catch (error) {
    console.error('Autocomplete 초기화 실패:', error);
  }
}

/**
 * 기존 동네 인증 정보 로드
 */
function loadExistingArea() {
  if (!window.existingArea) return;

  const { address, latitude, longitude } = window.existingArea;
  const latLng = new google.maps.LatLng(latitude, longitude);

  updateMarkerAndCircle(latLng);
  setCurrent(address, latitude, longitude);

  const addressInput = document.getElementById('address');
  if (addressInput) {
    addressInput.value = address;
  }

  writeLocationText(`기존 인증 위치: ${address}`);
  enableConfirmButton();

  console.log('기존 동네 인증 정보 로드 완료');
}

/**
 * 마커/원/센터 갱신 공통 유틸
 */
function updateMarkerAndCircle(latLng) {
  if (map && marker && circle) {
    map.setCenter(latLng);
    marker.setPosition(latLng);
    circle.setCenter(latLng);
  }
}

/**
 * 원의 중심만 업데이트
 */
function updateCircleCenter(latLng) {
  if (circle) {
    circle.setCenter(latLng);
  }
}

/**
 * 역지오코딩 (lat, lng → 주소)
 */
function geocodeLatLng(lat, lng) {
  if (!geocoder) {
    console.error('geocoder가 초기화되지 않았습니다.');
    return;
  }

  const latlng = { lat, lng };
  geocoder.geocode({ location: latlng }, (results, status) => {
    if (status === 'OK') {
      if (results && results[0]) {
        const addr = results[0].formatted_address;
        const addressInput = document.getElementById('address');
        if (addressInput) {
          addressInput.value = addr;
        }
        writeLocationText(`선택된 위치: ${addr}`);
        setCurrent(addr, lat, lng);
        enableConfirmButton();
        hideError();
      } else {
        showError('주소를 찾을 수 없습니다.');
      }
    } else {
      console.error('지오코딩 실패:', status);
      showError('주소 변환에 실패했습니다: ' + status);
    }
  });
}

/**
 * 순방향 지오코딩 (주소 → 좌표) 및 맵 갱신
 */
function searchAddress() {
  const address = document.getElementById('address').value.trim();
  if (!address) {
    showError('주소를 입력해주세요.');
    return;
  }

  if (!geocoder) {
    showError('지도 서비스가 준비되지 않았습니다. 잠시 후 다시 시도해주세요.');
    return;
  }

  setButtonLoading('searchBtn', true);

  geocoder.geocode({ address }, (results, status) => {
    setButtonLoading('searchBtn', false);

    if (status === 'OK' && results[0]) {
      const location = results[0].geometry.location;
      updateMarkerAndCircle(location);

      const lat = location.lat();
      const lng = location.lng();
      const addr = results[0].formatted_address;

      writeLocationText(`검색된 위치: ${addr}`);
      setCurrent(addr, lat, lng);
      enableConfirmButton();
      hideError();
    } else {
      console.error('주소 검색 실패:', status);
      showError('주소 검색에 실패했습니다: ' + status);
    }
  });
}

/**
 * 현재 위치 요청
 */
function setLocation() {
  if (!navigator.geolocation) {
    showError('브라우저가 위치 정보를 지원하지 않습니다.');
    return;
  }

  setButtonLoading('locationBtn', true);

  navigator.geolocation.getCurrentPosition(
    (pos) => {
      setButtonLoading('locationBtn', false);
      const lat = pos.coords.latitude;
      const lng = pos.coords.longitude;
      const latLng = new google.maps.LatLng(lat, lng);

      updateMarkerAndCircle(latLng);
      geocodeLatLng(lat, lng);
      hideError();
    },
    (err) => {
      setButtonLoading('locationBtn', false);
      console.warn('위치 정보를 가져올 수 없습니다:', err);

      let errorMsg = '위치 정보를 가져올 수 없습니다. ';
      switch(err.code) {
        case err.PERMISSION_DENIED:
          errorMsg += '위치 권한을 허용해주세요.';
          break;
        case err.POSITION_UNAVAILABLE:
          errorMsg += '위치 정보를 사용할 수 없습니다.';
          break;
        case err.TIMEOUT:
          errorMsg += '위치 정보 요청 시간이 초과되었습니다.';
          break;
        default:
          errorMsg += '알 수 없는 오류가 발생했습니다.';
      }
      showError(errorMsg);
    },
    { enableHighAccuracy: true, timeout: 8000, maximumAge: 600000 }
  );
}

/**
 * 서버로 동네 인증 전송
 */
async function confirmArea() {
  const userId = window.currentUserId;

  if (!userId) {
    showError('로그인이 필요합니다.');
    return;
  }

  if (!currentAddress || currentLat == null || currentLng == null) {
    showError('주소 또는 좌표가 설정되지 않았습니다. 위치를 선택해주세요.');
    return;
  }

  const dto = {
    userId: userId,
    address: currentAddress,
    latitude: currentLat,
    longitude: currentLng
  };

  console.log('동네 인증 요청 데이터:', dto);

  const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
  const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;

  setButtonLoading('confirmBtn', true);

  try {
    const headers = { 'Content-Type': 'application/json' };
    if (csrfToken && csrfHeader) {
      headers[csrfHeader] = csrfToken;
    }

    const res = await fetch('/area/confirm', {
      method: 'POST',
      headers: headers,
      body: JSON.stringify(dto)
    });

    const responseData = await res.json();

    if (res.ok && responseData.success) {
      showSuccess(responseData.message || '동네 인증이 완료되었습니다.');
      // 필요시 페이지 이동
      setTimeout(() => {
        window.location.href = '/';
      }, 2000);
    } else {
      throw new Error(responseData.message || '알 수 없는 오류가 발생했습니다.');
    }
  } catch (e) {
    console.error('인증 실패:', e);
    showError('동네 인증 중 문제가 발생했습니다: ' + e.message);
  } finally {
    setButtonLoading('confirmBtn', false);
  }
}

// ===== 유틸리티 함수들 =====

/**
 * 현재 선택된 위치 정보 설정
 */
function setCurrent(addr, lat, lng) {
  currentAddress = addr;
  currentLat = lat;
  currentLng = lng;
  console.log('현재 위치 설정:', { addr, lat, lng });
}

/**
 * 위치 텍스트 표시
 */
function writeLocationText(text) {
  const el = document.getElementById('locationText');
  if (el) {
    el.innerText = text;
  }
}

/**
 * 인증 완료 버튼 활성화
 */
function enableConfirmButton() {
  const btn = document.getElementById('confirmBtn');
  if (btn) {
    btn.disabled = false;
  }
}

/**
 * 버튼 로딩 상태 설정
 */
function setButtonLoading(buttonId, isLoading) {
  const btn = document.getElementById(buttonId);
  if (!btn) return;

  const originalText = btn.getAttribute('data-original-text') || btn.textContent;

  if (isLoading) {
    btn.setAttribute('data-original-text', originalText);
    btn.disabled = true;
    btn.textContent = originalText + '...';
  } else {
    btn.disabled = false;
    btn.textContent = originalText;
    btn.removeAttribute('data-original-text');
  }
}

/**
 * 에러 메시지 표시
 */
function showError(message) {
  const errorEl = document.getElementById('errorMessage');
  const successEl = document.getElementById('successMessage');

  if (errorEl) {
    errorEl.textContent = message;
    errorEl.style.display = 'block';
  }

  if (successEl) {
    successEl.style.display = 'none';
  }

  // 자동으로 위로 스크롤
  errorEl?.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
}

/**
 * 성공 메시지 표시
 */
function showSuccess(message) {
  const errorEl = document.getElementById('errorMessage');
  const successEl = document.getElementById('successMessage');

  if (successEl) {
    successEl.textContent = message;
    successEl.style.display = 'block';
  }

  if (errorEl) {
    errorEl.style.display = 'none';
  }

  // 자동으로 위로 스크롤
  successEl?.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
}

/**
 * 에러 메시지 숨기기
 */
function hideError() {
  const errorEl = document.getElementById('errorMessage');
  if (errorEl) {
    errorEl.style.display = 'none';
  }
}

// ===== 전역 노출 (HTML에서 호출할 함수들) =====
window.searchAddress = searchAddress;
window.setLocation = setLocation;
window.confirmArea = confirmArea;
window.initMap = initMap;