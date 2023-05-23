package jpabook.jpashop.domain;

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

    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>(); // 컬렉션은 필드에서 바로 초기화 하는 것이 가장 좋다.

    /**
     * 나는 주인이 아니라 연관관계 거울이다 => 읽기 전용
     * => 값을 넣는다고 해서 FK값이 변경되지 않는다.
     * Order 테이블에 있는 member 필드에 의해 매핑된것
     */


    /**
     * Presentation Layer를 위한 검증 로직이 `엔티티`에 있다
     * => 어떤 API에서는 NotEmpty가 필요한데, 다른 API에서는 NotEmpty가 없을 수도 있다..
     *
     * 엔티티의 스펙을 username으로 바뀌게 되면 -> API 스펙 자체가 usrename으로 바뀌게 된다.
     * => 큰 문제!! - 엔티티라는 것은 굉장히 여러 곳에서 쓰기 때문에 바뀔 확률이 높다. => 이것이 바뀐다고 API 스펙 자체가 바뀌게 된다면 문제가 된다..
     *      => API 스펙을 위한 별도의 DTO를 만들어야 한다!!
     *      === 엔티티를 이렇게 외부에서 json 오는 것을 바인딩 받는데 쓰면 안된다!! ===
     *      (엔티티를 파라미터로 받지도 말고, 외부에 노출해서도 안된다!)
     */
}
