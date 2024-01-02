package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@Entity
public class Member {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @NotEmpty
    private String name;

    @Embedded
    private Address address;

    @JsonIgnore
    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>(); // 컬렉션은 필드에서 바로 초기화 하는 것이 가장 좋다.

    /**
     * - mappedBy (Order 테이블에 있는 member 필드에 의해 매핑된것)
     * 나는 주인이 아니라 연관관계 거울이다 => 읽기 전용 (매핑 되는 '거울'일 뿐이다)
     * => 값을 넣는다고 해서 FK값이 변경되지 않는다.
     *      <-> Order는 변경하면 FK값이 변경이 된다.
     */

}
