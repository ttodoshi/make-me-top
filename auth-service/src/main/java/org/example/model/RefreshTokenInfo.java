package org.example.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "refresh_token_info")
@Data
@NoArgsConstructor
public class RefreshTokenInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer tokenId;
    private String refreshToken;
    private Integer personId;
    private String role;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private Date expirationTime;

    public RefreshTokenInfo(String refreshToken, Integer personId, String role, Date expirationTime) {
        this.refreshToken = refreshToken;
        this.personId = personId;
        this.role = role;
        this.expirationTime = expirationTime;
    }
}
