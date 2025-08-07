package cn.nefu.ccec.graduaterecommendationevaluation.dto;

import cn.nefu.ccec.graduaterecommendationevaluation.dox.Category;
import cn.nefu.ccec.graduaterecommendationevaluation.dox.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminResp {
    private Category category;
    private List<User> users;
}
