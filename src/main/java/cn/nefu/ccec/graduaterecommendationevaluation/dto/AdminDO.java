package cn.nefu.ccec.graduaterecommendationevaluation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminDO {
    private String categoryName;
    private String userName;
    private Long catId;
    private Long userId;
}
