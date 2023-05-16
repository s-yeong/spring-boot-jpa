package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@Entity
public class Member {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String name;

    @Embedded
    private Address address;

    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();
    /**
     * 나는 주인이 아니라 연관관계 거울이다 => 읽기 전용
     *         => 값을 넣는다고 해서 FK값이 변경되지 않는다.
     * Order 테이블에 있는 member 필드에 의해 매핑된것
     */
}
