package org.example.person.dto.courserequest;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.example.person.dto.explorer.ExplorerRequestDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetApprovedCourseRegistrationRequestsForKeeperDto {
    private Long courseId;
    private String courseTitle;
    private List<ApprovedCourseRegistrationRequestDto> requests;

    @Data
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor
    public static class ApprovedCourseRegistrationRequestDto extends ExplorerRequestDto implements Comparable<ApprovedCourseRegistrationRequestDto> {
        private Long requestId;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
        private LocalDateTime responseDate;
        private Long keeperId;
        private Double rating;

        public ApprovedCourseRegistrationRequestDto(Long personId, String firstName, String lastName, String patronymic, Long courseId, String courseTitle, Long requestId, LocalDateTime responseDate, Long keeperId, Double rating) {
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
