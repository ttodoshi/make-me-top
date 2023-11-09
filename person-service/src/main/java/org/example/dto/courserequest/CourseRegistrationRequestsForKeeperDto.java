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
public class CourseRegistrationRequestsForKeeperDto {
    private Integer courseId;
    private String courseTitle;
    private List<CourseRegistrationRequestForKeeperDto> requests;

    @Data
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor
    public static class CourseRegistrationRequestForKeeperDto extends ExplorerRequestDto implements Comparable<CourseRegistrationRequestForKeeperDto> {
        private Integer requestId;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
        private LocalDateTime requestDate;
        private Integer keeperId;
        private Double rating;

        public CourseRegistrationRequestForKeeperDto(Integer personId, String firstName, String lastName, String patronymic, Integer courseId, String courseTitle, Integer requestId, LocalDateTime requestDate, Integer keeperId, Double rating) {
            super(personId, firstName, lastName, patronymic, courseId, courseTitle);
            this.requestId = requestId;
            this.requestDate = requestDate;
            this.keeperId = keeperId;
            this.rating = rating;
        }

        @Override
        public int compareTo(CourseRegistrationRequestForKeeperDto request) {
            return this.requestDate.compareTo(request.getRequestDate());
        }
    }
}
