package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter @Setter
public class Order {

    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id") // FK 이름이 member_id
    private Member member;

    /**
     * 현재 Member와 Order는 양방향 관계인데,
     * 데이터베이스에는 orders에만 FK를 가지고 있다.
     *
     * Member와 Order 관계를 바꾸고 싶으면, FK를 변경해야 하는데,
     * Member에도 orders 필드가 있고 Order에도 member라는 필드가 있어서, JPA에서는 어디에서 값이 변경됐을 떄
     * FK를 바꿔야 하지? 혼란이 온다.
     * => FK를 업데이트 치는 것은 둘 중하나만 선택하게 JPA에서 약속을 함
     * 객체는 변경 포인트가 두 군데인데, 테이블은 FK 하나만 변경하면 된다..
     * => 연관관계 주인이라는 개념으로 얘 값이 변경됐을 때 FK를 바꿀꺼야라고 지정한 거!
     * => FK가 가까운 것을 연관관계 주인으로 -> Order
     *
     * member 값을 변경하면 FK 값이 다른 멤버로 변경된다.
     */

    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    private LocalDateTime orderDate;    // 주문 시간

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // 주문 상태 [ORDER, CANCEL]

    /**
     * 1대1 관계에서는 FK를 어디에 두냐 장단점이 있는데, 주로 액세스를 많이 하는 곳에 FK를 둔다!
     */
}
