package cn.nefu.ccec.graduaterecommendationevaluation.dto;

import cn.nefu.ccec.graduaterecommendationevaluation.dox.WeightedScore;
import cn.nefu.ccec.graduaterecommendationevaluation.dox.WeightedScoreLog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ComfirmWeightedScoreReq {
    private WeightedScore weightedScore;
    private WeightedScoreLog log;
}
