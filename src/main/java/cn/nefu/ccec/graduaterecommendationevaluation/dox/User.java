package cn.nefu.ccec.graduaterecommendationevaluation.dox;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    public static final String ADMIN = "Oi7yT";
    public static final String COLLAGE_ADMIN = "a2Pvd";
    public static final  String CATEGORY_ADMIN = "Rcz9N";
    public static final  String STUDENT = "lM53x";

    @Id
    @CreatedBy
    private Long id;
    private String account;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private String name;
    private String role;
    private String mobile;
    private Long collId;
    private Long majorId;
    private Long catId;

    @ReadOnlyProperty
    private LocalDateTime createTime;
    @ReadOnlyProperty
    private LocalDateTime updateTime;
}
