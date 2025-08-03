package cn.nefu.ccec.graduaterecommendationevaluation.dto;

import cn.nefu.ccec.graduaterecommendationevaluation.dox.Item;
import cn.nefu.ccec.graduaterecommendationevaluation.dox.StudentItem;
import cn.nefu.ccec.graduaterecommendationevaluation.dox.StudentItemFile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentItemResp {

    private Long id;
    private Long userId;
    private Long rootItemId;
    private Long itemId;
    private String name;
    private Float point;
    private String comment;
    private String status;

    private String itemName;
    private Float maxPoints;
    private Integer maxItems;
    private Long itemParentId;
    private String itemComment;

    private List<StudentItemFile> files;
    private Item item;
    private List<StudentItem>  studentItems;
}
