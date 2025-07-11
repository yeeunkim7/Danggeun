let map, marker, circle, geocoder;
let autocomplete;

function showPosition(position) {
    const lat = position.coords.latitude;
    const lng = position.coords.longitude;

    const mapContainer = document.getElementById('map');

    // 지도 초기화
    map = new google.maps.Map(mapContainer, {
        center: { lat: lat, lng: lng },
        zoom: 16
    });

    // 마커 생성
    marker = new google.maps.Marker({
        position: { lat: lat, lng: lng },
        map: map
    });

    // 원 생성
    circle = new google.maps.Circle({
        map: map,
        center: { lat: lat, lng: lng },
        radius: 300,
        strokeColor: '#00a0e9',
        strokeOpacity: 0.8,
        strokeWeight: 2,
        fillColor: '#cceeff',
        fillOpacity: 0.6
    });
    circle.setMap(map);

    // 지오코더 준비
    geocoder = new google.maps.Geocoder();

    const presetAddress = document.getElementById("address").value;
    if (presetAddress) {
        geocoder.geocode({ address: presetAddress }, function (results, status) {
            if (status === "OK" && results[0]) {
                const location = results[0].geometry.location;
                map.setCenter(location);
                marker.setPosition(location);
                circle.setCenter(location);

                document.getElementById("locationText").innerText = "초기 위치는 " + results[0].formatted_address + "입니다.";

                window.currentAddress = results[0].formatted_address;
                window.currentLat = location.lat();
                window.currentLng = location.lng();
            }
        });
    } else {
        // 초기 위치 주소 변환
        geocodeLatLng(lat, lng);
    }

    // 지도 클릭 이벤트
    map.addListener('click', function (event) {
        const clickLat = event.latLng.lat();
        const clickLng = event.latLng.lng();

        marker.setPosition(event.latLng);
        circle.setCenter(event.latLng);
        geocodeLatLng(clickLat, clickLng);
    });

    initAutocomplete();
}

function initAutocomplete() {
    const input = document.getElementById("address");
    autocomplete = new google.maps.places.Autocomplete(input);
    autocomplete.setFields(["formatted_address", "geometry"]);

    autocomplete.addListener("place_changed", function () {
        const place = autocomplete.getPlace();

        if (!place.geometry) {
            alert("선택한 장소에 위치 정보가 없습니다.");
            return;
        }

        const lat = place.geometry.location.lat();
        const lng = place.geometry.location.lng();

        map.setCenter(place.geometry.location);
        marker.setPosition(place.geometry.location);
        circle.setCenter(place.geometry.location);

        document.getElementById("locationText").innerText = "선택한 위치는 " + place.formatted_address + "입니다.";

        window.currentAddress = place.formatted_address;
        window.currentLat = lat;
        window.currentLng = lng;
    });
}

function geocodeLatLng(lat, lng) {
    const latlng = { lat: lat, lng: lng };

    geocoder.geocode({ location: latlng }, (results, status) => {
        if (status === "OK") {
            if (results[0]) {
                const address = results[0].formatted_address;
                document.getElementById("address").value = address;
                document.getElementById("locationText").innerText = "선택한 위치는 " + address + "입니다.";

                // 전역 변수로 저장
                window.currentAddress = address;
                window.currentLat = lat;
                window.currentLng = lng;
            } else {
                alert("주소를 찾을 수 없습니다.");
            }
        } else {
            alert("지오코딩 실패: " + status);
        }
    });
}
function confirmArea() {
    const data = {
        userId: currentUserId,
        address: window.currentAddress,
        latitude: window.currentLat,
        longitude: window.currentLng
    };

    fetch('/api/area/confirm', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    })
    .then(res => res.text())
    .then(result => {
        alert(result);
    })
    .catch(err => {
        console.error("인증 실패:", err);
        alert("동네 인증 중 문제가 발생했습니다.");
    });
}

// 위치 요청 함수
function setLocation() {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(showPosition, () => {
            alert("위치 정보를 가져올 수 없습니다.");
        });
    } else {
        alert("이 브라우저는 위치 정보를 지원하지 않습니다.");
    }
}

function searchAddress() {
    const address = document.getElementById("address").value;

    if (!address) {
        alert("주소를 입력해주세요.");
        return;
    }

    geocoder.geocode({ address: address }, function(results, status) {
        if (status === "OK") {
            const location = results[0].geometry.location;
            const lat = location.lat();
            const lng = location.lng();

            map.setCenter(location);
            marker.setPosition(location);
            circle.setCenter(location);

            document.getElementById("locationText").innerText = "입력한 위치는 " + results[0].formatted_address + "입니다.";

            window.currentAddress = results[0].formatted_address;
            window.currentLat = lat;
            window.currentLng = lng;
        } else {
            alert("주소 검색에 실패했습니다: " + status);
        }
    });
}