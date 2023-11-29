package org.example.auth.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "refresh_token_info")
@Data
@NoArgsConstructor
public class RefreshTokenInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tokenId;
    @Column(nullable = false, unique = true)
    private String refreshToken;
    @Column(nullable = false)
    private Long personId;
    @Column(nullable = false)
    private String role;
    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private Date expirationTime;

    public RefreshTokenInfo(String refreshToken, Long personId, String role, Date expirationTime) {
        this.refreshToken = refreshToken;
        this.personId = personId;
        this.role = role;
        this.expirationTime = expirationTime;
    }
}
