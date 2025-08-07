package cn.nefu.ccec.graduaterecommendationevaluation.dox;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentItemLog {
    @Id
    @CreatedBy
    private Long id;
    private Long studentItemId;
    private Long userId;
    private String comment;

    private LocalDateTime createTime;
}
