package org.example.danggeun.areaConfirm.Controller;

import lombok.RequiredArgsConstructor;
import org.example.danggeun.areaConfirm.Dto.AreaDto;
import org.example.danggeun.areaConfirm.service.AreaService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/area")
public class AreaController {

    private final AreaService areaService;

    @PostMapping("/confirm")
    public String confirmArea(@RequestBody AreaDto dto) {
        return areaService.saveArea(dto);
    }
}