package group1.intern.bean;

import group1.intern.model.Account;
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
    private int role;
    private String fullName;
    private LocalDate dateOfBirth;
    private String address;
    private String phoneNumber;
    private String gender;
    private String avatarUrl;
    private Boolean isActivated;

    public static AccountInfo fromAccount(Account account) {
        return AccountInfo.builder()
            .id(account.getId())
            .email(account.getEmail())
            .role(account.getRole().getIndex())
            .address(account.getAddress())
            .phoneNumber(account.getPhoneNumber())
            .dateOfBirth(account.getDateOfBirth())
            .avatarUrl(account.getAvatarUrl())
            .gender(account.getGender() ? "Male" : "Female")
            .fullName(account.getFullName())
            .displayName(account.getDisplayName())
            .isActivated(account.getIsActivated())
            .build();
    }
}
