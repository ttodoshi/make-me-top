package org.example.courseregistration.service;

import lombok.RequiredArgsConstructor;
import org.example.courseregistration.dto.courserequest.RejectionReasonDto;
import org.example.courseregistration.repository.RejectionReasonRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KeeperRejectionService {
    private final RejectionReasonRepository rejectionReasonRepository;

    private final ModelMapper mapper;

    @Transactional(readOnly = true)
    public List<RejectionReasonDto> getRejectionReasons() {
        return rejectionReasonRepository.findAll()
                .stream()
                .map(r -> mapper.map(r, RejectionReasonDto.class))
                .collect(Collectors.toList());
    }
}
