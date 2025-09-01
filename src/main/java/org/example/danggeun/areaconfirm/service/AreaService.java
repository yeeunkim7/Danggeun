package org.example.danggeun.areaconfirm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.danggeun.areaconfirm.dto.AreaRequestDto;
import org.example.danggeun.areaconfirm.dto.AreaResponseDto;
import org.example.danggeun.areaconfirm.entity.Area;
import org.example.danggeun.areaconfirm.repository.AreaRepository;
import org.example.danggeun.user.entity.User;
import org.example.danggeun.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AreaService {

    private final AreaRepository areaRepository;
    private final UserRepository userRepository;

    @Transactional
    public AreaResponseDto saveArea(AreaRequestDto dto) {
        // 입력 검증
        dto.validate();

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

        // 기존 동네 인증이 있는지 확인
        areaRepository.findTopByUserIdOrderByConfirmedAtDesc(user.getId())
                .ifPresent(existingArea -> {
                    log.info("기존 동네 인증을 업데이트합니다. UserId: {}", user.getId());
                });

        Area area = Area.builder()
                .address(dto.getAddress())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .userId(user.getId())
                .build();

        Area savedArea = areaRepository.save(area);
        log.info("동네 인증이 완료되었습니다. UserId: {}, Address: {}", user.getId(), dto.getAddress());

        return AreaResponseDto.from(savedArea);
    }

    public AreaResponseDto findAreaByUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("사용자 ID는 필수입니다.");
        }

        Area area = areaRepository.findTopByUserIdOrderByConfirmedAtDesc(userId)
                .orElseThrow(() -> new IllegalArgumentException("인증된 위치가 없습니다."));

        return AreaResponseDto.from(area);
    }

    public boolean hasAreaConfirmation(Long userId) {
        if (userId == null) {
            return false;
        }
        return areaRepository.findTopByUserIdOrderByConfirmedAtDesc(userId).isPresent();
    }
}
