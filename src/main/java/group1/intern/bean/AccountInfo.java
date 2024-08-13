package group1.intern.bean;

import group1.intern.model.Enum.AccountRole;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountInfo {
    private Integer id;
    private String email;
    private String displayName;
    private AccountRole role;
    private String fullName;
    private LocalDate dateOfBirth;
    private String address;
    private String phoneNumber;
    private Boolean gender;
    private String avatarUrl;
    private Boolean isActivated;
}
