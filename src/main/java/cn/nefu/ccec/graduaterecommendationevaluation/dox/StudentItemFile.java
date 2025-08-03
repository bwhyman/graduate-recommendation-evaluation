package cn.nefu.ccec.graduaterecommendationevaluation.dox;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentItemFile {
    @Id
    @CreatedBy
    private Long id;
    private Long studentItemId;
    // 相对目录，不返回客户端
    @JsonIgnore
    private String path;
    private String filename;
}
