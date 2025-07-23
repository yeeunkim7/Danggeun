package org.example.danggeun.areaconfirm.controller;

import lombok.RequiredArgsConstructor;
import org.example.danggeun.areaconfirm.dto.AreaRequestDto;
import org.example.danggeun.areaconfirm.service.AreaService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/area")
public class AreaController {

    private final AreaService areaService;

    @PostMapping("/confirm")
    public String confirmArea(@RequestBody AreaRequestDto dto) {
        return areaService.saveArea(dto);
    }

    @GetMapping("/me")
    public AreaRequestDto getMyArea(@RequestParam("userId") Long userId) {
        return areaService.findAreaByUserId(userId);
    }
}