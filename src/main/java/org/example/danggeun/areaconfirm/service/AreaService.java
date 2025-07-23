package org.example.danggeun.areaconfirm.service;

import lombok.RequiredArgsConstructor;
import org.example.danggeun.areaconfirm.dto.AreaRequestDto;
import org.example.danggeun.areaconfirm.entity.Area;
import org.example.danggeun.areaconfirm.repository.AreaRepository;
import org.example.danggeun.user.entity.User;
import org.example.danggeun.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AreaService {

    private final AreaRepository areaRepository;
    private final UserRepository userRepository;

    public String saveArea(AreaRequestDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

        Area area = Area.builder()
                .address(dto.getAddress())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .userId(user.getId())
                .build();
        areaRepository.save(area);
        return "인증 완료";
    }

    public AreaRequestDto findAreaByUserId(Long userId) {
        Area area = areaRepository.findTopByUserIdOrderByConfirmedAtDesc(userId)
                .orElseThrow(() -> new IllegalArgumentException("인증된 위치가 없습니다."));

        return AreaRequestDto.builder()
                .userId(area.getUserId())
                .address(area.getAddress())
                .latitude(area.getLatitude())
                .longitude(area.getLongitude())
                .build();
    }
}