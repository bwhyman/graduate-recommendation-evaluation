package cn.nefu.ccec.graduaterecommendationevaluation.dto;

import cn.nefu.ccec.graduaterecommendationevaluation.dox.College;
import cn.nefu.ccec.graduaterecommendationevaluation.dox.Major;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CollegeDTO {
    private College college;
    private List<Major> majors;
}
