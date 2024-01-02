package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter @Setter
public class Delivery {

    @Id
    @GeneratedValue
    @Column(name = "delivery_id")
    private Long id;

    @OneToOne(mappedBy = "delivery")
    private Order order;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;  // READY, COMP
    /**
     * Enum 타입은 @Enumerated를 넣어야 한다.
     *
     * Enum 타입을 넣을 때, ORDINAL이랑 STRING을 넣을 수 있는데
     * ORDINAL은 숫자로 들어간다. 이 경우, "READY, XXX, COMP" 처럼 중간에 다른 상태가 생기면 망한다. 1,2에서 -> 1,2,3으로 밀려버리기 때문에
     *
     * STRING으로 쓰자!!
     */
}
