package cn.nefu.ccec.graduaterecommendationevaluation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserDTO {
    private String account;
    private String name;
    private String mobile;
    private Long collId;
    private Long majorId;
    // cat_admin
    private List<Long> catIds;
    private Long catId;
}
