package cn.nefu.ccec.graduaterecommendationevaluation.dto;

import cn.nefu.ccec.graduaterecommendationevaluation.dox.StudentItem;
import cn.nefu.ccec.graduaterecommendationevaluation.dox.StudentItemLog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentItemReq {
    private StudentItem studentItem;
    private StudentItemLog log;
}
