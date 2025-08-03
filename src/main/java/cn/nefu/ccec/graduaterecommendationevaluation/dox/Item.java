package cn.nefu.ccec.graduaterecommendationevaluation.dox;

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
public class Item {
    @Id
    @CreatedBy
    private Long id;
    private String name;
    private Long catId;
    private Float maxPoints;
    private Integer maxItems;
    private Long parentId;
    private String comment;

}
