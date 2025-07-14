package org.example.danggeun.write.controller;

import jakarta.websocket.Session;
import lombok.RequiredArgsConstructor;
import org.example.danggeun.write.Dto.WriteUserDto;
import org.example.danggeun.write.service.WriteService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@RequiredArgsConstructor
public class WriteController {
    private final WriteService writeService;

    @GetMapping("/write")
    public String writePage(Session session){
        return "write/write";
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PutMapping("/write")
    public String write(WriteUserDto writeUserDto){
        return "tradePost";
    }
}
