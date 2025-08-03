package cn.nefu.ccec.graduaterecommendationevaluation.dox;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeightedScore implements Persistable<Long> {
    public static final int VERIFIED = 1;
    public static final int UNVERIFIED = 0;
    @Id
    private Long id;
    private Float score;
    private Integer ranking;
    private String comment;
    private Integer verified;
    private String logs;

    @Transient
    @JsonIgnore
    private boolean isNew;

    public void setNew() {
        isNew = true;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }
}
