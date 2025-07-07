package org.example.danggeun.write.service;

import lombok.RequiredArgsConstructor;
import org.example.danggeun.write.entity.Write;
import org.example.danggeun.write.repository.WriteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WriteService {
    private final WriteRepository writeRepository;

    public List<Write> getAllWrite() {
        return writeRepository.findAll();
    }
}
