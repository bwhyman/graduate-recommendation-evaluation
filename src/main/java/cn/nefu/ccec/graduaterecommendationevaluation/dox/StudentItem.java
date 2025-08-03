package cn.nefu.ccec.graduaterecommendationevaluation.dox;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentItem {

    public interface Status {
        String SUBMITTED = "av8c";
        String REJECTED = "ciG1";
        String PENDING = "EmBq";
        String CONFIRMED = "yJ3C";
    }

    @Id
    @CreatedBy
    private Long id;
    private Long userId;
    private Long rootItemId;
    private Long itemId;
    private String name;
    private Float point;
    private String comment;
    private String status;
    @Transient
    private List<StudentItemFile> files;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
