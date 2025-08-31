// ===== 전역 변수 =====
let map, marker, circle, geocoder;
let autocomplete;
let currentAddress = null;
let currentLat = null;
let currentLng = null;

/**
 * Google Maps 스크립트가 로드되면 호출되는 콜백
 * - 기본 지도/마커/원/지오코더/오토컴플릿 초기화
 * - 현재 위치 자동 시도
 */
function initMap() {
  console.log('initMap 함수 호출됨');

  const mapContainer = document.getElementById('map');
  if (!mapContainer) {
    console.error('map DOM이 없습니다.');
    return;
  }

  // 서울 시청 근처를 기본 중심으로
  const defaultCenter = { lat: 37.5665, lng: 126.9780 };

  try {
    map = new google.maps.Map(mapContainer, {
      center: defaultCenter,
      zoom: 16,
      mapTypeControl: false
    });

    marker = new google.maps.Marker({
      position: defaultCenter,
      map: map
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

    // 지도 클릭으로 위치 선택 가능
    map.addListener('click', (event) => {
      const clickLat = event.latLng.lat();
      const clickLng = event.latLng.lng();
      updateMarkerAndCircle(event.latLng);
      geocodeLatLng(clickLat, clickLng);
    });

    // Places Autocomplete
    initAutocomplete();

    // 페이지 진입 시 현재 위치 자동 시도 (권한 거부 시 기본값 유지)
    setLocation();

    console.log('Google Maps 초기화 완료');
  } catch (error) {
    console.error('Google Maps 초기화 실패:', error);
  }
}

/** Autocomplete 초기화 */
function initAutocomplete() {
  const input = document.getElementById('address');
  if (!input) {
    console.error('address input 요소를 찾을 수 없습니다.');
    return;
  }

  try {
    // 최신 방식: fields 옵션은 생성자에 전달
    autocomplete = new google.maps.places.Autocomplete(input, {
      fields: ['formatted_address', 'geometry'],
      componentRestrictions: { country: 'kr' }
    });

    autocomplete.addListener('place_changed', () => {
      const place = autocomplete.getPlace();
      if (!place || !place.geometry) {
        alert('선택한 장소에 위치 정보가 없습니다.');
        return;
      }

      const loc = place.geometry.location; // LatLng
      updateMarkerAndCircle(loc);

      const lat = loc.lat();
      const lng = loc.lng();
      const addr = place.formatted_address || input.value;

      setCurrent(addr, lat, lng);
      writeLocationText(`현재 위치 주소: ${addr}`);
    });

    console.log('Autocomplete 초기화 완료');
  } catch (error) {
    console.error('Autocomplete 초기화 실패:', error);
  }
}

/** 마커/원/센터 갱신 공통 유틸 */
function updateMarkerAndCircle(latLng) {
  if (map && marker && circle) {
    map.setCenter(latLng);
    marker.setPosition(latLng);
    circle.setCenter(latLng);
  }
}

/** 역지오코딩 (lat, lng → 주소) */
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
        writeLocationText(`현재 위치 주소: ${addr}`);
        setCurrent(addr, lat, lng);
      } else {
        alert('주소를 찾을 수 없습니다.');
      }
    } else {
      console.error('지오코딩 실패:', status);
      alert('지오코딩 실패: ' + status);
    }
  });
}

/** 순방향 지오코딩 (주소 → 좌표) 및 맵 갱신 */
function searchAddress() {
  const address = document.getElementById('address').value.trim();
  if (!address) {
    alert('주소를 입력해주세요.');
    return;
  }

  if (!geocoder) {
    console.error('geocoder가 초기화되지 않았습니다.');
    alert('지도 서비스가 준비되지 않았습니다. 잠시 후 다시 시도해주세요.');
    return;
  }

  geocoder.geocode({ address }, (results, status) => {
    if (status === 'OK' && results[0]) {
      const location = results[0].geometry.location;
      updateMarkerAndCircle(location);

      const lat = location.lat();
      const lng = location.lng();
      const addr = results[0].formatted_address;

      writeLocationText(`현재 위치 주소: ${addr}`);
      setCurrent(addr, lat, lng);
    } else {
      console.error('주소 검색 실패:', status);
      alert('주소 검색에 실패했습니다: ' + status);
    }
  });
}

/** 현재 위치 요청 */
function setLocation() {
  if (!navigator.geolocation) {
    alert('브라우저가 위치 정보를 지원하지 않습니다.');
    return;
  }

  navigator.geolocation.getCurrentPosition(
    (pos) => {
      const lat = pos.coords.latitude;
      const lng = pos.coords.longitude;
      const latLng = new google.maps.LatLng(lat, lng);

      updateMarkerAndCircle(latLng);
      geocodeLatLng(lat, lng);
    },
    (err) => {
      console.warn('위치 정보를 가져올 수 없습니다:', err);
      alert('위치 권한을 허용해주세요.');
    },
    { enableHighAccuracy: true, timeout: 8000 }
  );
}

/** 서버로 동네 인증 전송 */
async function confirmArea() {
  // window.currentUserId 대신 전역 변수 currentUserId 사용
  const userId = window.currentUserId || currentUserId;

  if (!userId) {
    alert('로그인이 필요합니다.');
    console.error('currentUserId가 설정되지 않았습니다.');
    return;
  }

  if (!currentAddress || currentLat == null || currentLng == null) {
    alert('주소 또는 좌표가 설정되지 않았습니다. 현재 위치 설정 또는 주소 검색을 해주세요.');
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

  try {
    const headers = { 'Content-Type': 'application/json' };
    if (csrfToken && csrfHeader) {
      headers[csrfHeader] = csrfToken;
    }

    const res = await fetch('/api/area/confirm', {
      method: 'POST',
      headers: headers,
      body: JSON.stringify(dto)
    });

    if (!res.ok) {
      const errorText = await res.text();
      console.error('HTTP 에러:', res.status, errorText);
      throw new Error('HTTP ' + res.status + ': ' + errorText);
    }

    const msg = await res.text();
    alert(msg || '동네 인증이 완료되었습니다.');
    // 필요 시 이동
    // window.location.href = '/';
  } catch (e) {
    console.error('인증 실패:', e);
    alert('동네 인증 중 문제가 발생했습니다: ' + e.message);
  }
}

/** 상태 쓰기 유틸 */
function setCurrent(addr, lat, lng) {
  currentAddress = addr;
  currentLat = lat;
  currentLng = lng;
  console.log('현재 위치 설정:', { addr, lat, lng });
}

function writeLocationText(text) {
  const el = document.getElementById('locationText');
  if (el) {
    el.innerText = text;
  } else {
    console.warn('locationText 요소를 찾을 수 없습니다.');
  }
}

// 전역 노출 (HTML onclick에서 호출)
window.searchAddress = searchAddress;
window.setLocation = setLocation;
window.confirmArea = confirmArea;
window.initMap = initMap;