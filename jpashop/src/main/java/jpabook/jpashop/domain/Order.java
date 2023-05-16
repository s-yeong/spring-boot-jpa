package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Entity
@Table(name = "orders")
@Getter @Setter
public class Order {

    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
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

    /**
     * @XtoOne(OneToOne, ManyToOne) 관계는 기본이 즉시로딩(EAGER)
     * <-> @XtoMany(OneToMany, ManyToMany) 관계는 기본이 지연로딩(LAZY)
     *
     * Order를 조회할 때 Member를 join을 해서 같이 가져온다! -> em.find()
     *
     * JPQL을 통해  `select o From Order o;` 이렇게 가져오면, SQL 그대로 번역된다 (EAGER 다 무시하고)
     * -> SQL `select * from order`
     * => Order 100개를 조회했을 때, SQL 날라갈 떄는 Order만 가져왔는데, Member가 EAGER로 되어있기 때문에
     * 100개가 100번 member를 가져오기 위해서 한방 쿼리가 100개 날라간다. => `N+1 문제`
     *      => 첫번쨰 Order 날리는 쿼리가 한번 날라가서, 결과가 100개면, 그 만큼 Member를 가져오기 위해 한방 쿼리를 날림
     *
     * `Eager`의 뜻은 Join을 해서 한번에 가져온다는게 아니라, Order를 조회하는 시점에 Member를 꼭 같이 조회하겠다는 의미
     *
     *  => 모든 연관관계는 지연로딩(LAZY)으로 설정하자!!!
     */

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();
    /**
     * cascade = CascadeType.ALL
     *
     * 원래는 persist(orderItemA); persist(orderItemB); persist(orderItemC) 한다음
     * persist(order) 해줘야 하는데,
     * cascade를 두면 persist(order)만 하면된다!
     * => Order를 persist하면 cascade는 persist를 전파해서, orderItems도 persist 같이 해줌
     * ALL이기 때문에 delete할 때도 같이 지워버림
     */

    @OneToOne(fetch = LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;
    /**
     * Order 저장할 때 delivery에 객체만 세팅해 두면, Order를 저장할 떄 Delivery 엔티티도 같이 Persist해준다
     * 원래라면 Order, Delivery 각각 persist 해줘야함
     */

    private LocalDateTime orderDate;    // 주문 시간

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // 주문 상태 [ORDER, CANCEL]

    /**
     * 1대1 관계에서는 FK를 어디에 두냐 장단점이 있는데, 주로 액세스를 많이 하는 곳에 FK를 둔다!
     */


    // ==연관관계 편의 메서드==
    // 양방향일 때 쓰면 좋다!!
    // 메서드의 위치는 핵심적으로 컨트롤 하는쪽이 들고있는게 좋다!
    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }

    /*
    Member member = new Member();
    Order order = new Order();

    member.getOrders().add(order);
    order.setMember(member);

    => 여기에서 두 개를 원자적으로 묶어서
    order.setMember(member); 하나로 줄어든다 (원자적으로 한 코드로 해결)
     */

    // ==연관관계 편의 메서드==
    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    // ==연관관계 편의 메서드==
    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }
}
