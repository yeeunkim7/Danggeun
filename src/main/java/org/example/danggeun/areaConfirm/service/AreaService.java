package org.example.danggeun.areaConfirm.service;

import lombok.RequiredArgsConstructor;
import org.example.danggeun.areaConfirm.Dto.AreaDto;
import org.example.danggeun.areaConfirm.entity.Area;
import org.example.danggeun.areaConfirm.repository.AreaRepository;
import org.example.danggeun.user.entity.User;
import org.example.danggeun.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AreaService {

    private final AreaRepository areaRepository;
    private final UserRepository userRepository;

    public String saveArea(AreaDto dto) {
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

    public AreaDto findAreaByUserId(Long userId) {
        Area area = areaRepository.findTopByUserIdOrderByConfirmedAtDesc(userId)
                .orElseThrow(() -> new IllegalArgumentException("인증된 위치가 없습니다."));

        return AreaDto.builder()
                .userId(area.getUserId())
                .address(area.getAddress())
                .latitude(area.getLatitude())
                .longitude(area.getLongitude())
                .build();
    }
}