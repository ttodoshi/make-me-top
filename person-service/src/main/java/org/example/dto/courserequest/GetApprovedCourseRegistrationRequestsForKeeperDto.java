package org.example.dto.courserequest;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.example.dto.explorer.ExplorerRequestDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetApprovedCourseRegistrationRequestsForKeeperDto {
    private Integer courseId;
    private String courseTitle;
    private List<ApprovedCourseRegistrationRequestDto> requests;

    @Data
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor
    public static class ApprovedCourseRegistrationRequestDto extends ExplorerRequestDto implements Comparable<ApprovedCourseRegistrationRequestDto> {
        private Integer requestId;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
        private LocalDateTime responseDate;
        private Integer keeperId;
        private Double rating;

        public ApprovedCourseRegistrationRequestDto(Integer personId, String firstName, String lastName, String patronymic, Integer courseId, String courseTitle, Integer requestId, LocalDateTime responseDate, Integer keeperId, Double rating) {
            super(personId, firstName, lastName, patronymic, courseId, courseTitle);
            this.requestId = requestId;
            this.responseDate = responseDate;
            this.keeperId = keeperId;
            this.rating = rating;
        }

        @Override
        public int compareTo(ApprovedCourseRegistrationRequestDto request) {
            return this.responseDate.compareTo(request.getResponseDate());
        }
    }
}
