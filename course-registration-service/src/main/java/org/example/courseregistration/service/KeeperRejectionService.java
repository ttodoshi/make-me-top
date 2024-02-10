package org.example.courseregistration.service;

import org.example.courseregistration.dto.courserequest.RejectionReasonDto;

import java.util.List;

public interface KeeperRejectionService {
    List<RejectionReasonDto> getRejectionReasons();
}
