package jpabook.jpashop.api;

import java.util.List;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * "xToOne" 관계 (ManyToOne, OneToOne)
 * Order
 * Order -> Member
 * Order -> Delivery
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;

    /**
     * 1. 엔티티를 직접 노출
     * - 엔티티를 그대로 노출하면 나중에 엔티티가 바뀌면 API 스펙이 다 바뀌어 버린다!
     * - 필요 없는 데이터도 가져오기 때문에 성능 상 문제도 있음
     */
    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        // 1. 무한 루프에 빠진다!!
        // Order에 가서도 Member가 있고, Member에 가서도 Order가 있고, Order에 가서도 ...
        // -> '양방향 연관관계'가 있으면 둘 중 하나를 '@JsonIgnore' 해줘야 한다.
        // (Member, OrderItem, Delivery - @JsonIgnore) => 그래야 JSON 생성할 때 Ignore니까 반대 쪽은 생성 안해야지 판단한다.

        // 2. '지연 로딩'이기 때문에, 얘가 진짜 new 해서 Member 객체를 안가져 온다.
        // ('지연 로딩'은 DB에서 안 끌고 오고 Order의 데이터만 가져온다.)
        // => Member에 'null'을 넣어들 수는 없으니 하이버네이트에서 new Member()해서 가짜 proxy 멤버 객체를 생성해서 넣어 둔다.
            // 프록시 기술 - (ByteBuddy - ByteBuddyInterceptor)
        // 프록시 객체를 넣어 놓고 뭔가 멤버 객체 값을 꺼내거나 딱 손대면 그때 DB에 멤버 객체 SQL을 날려서 이 멤버 객체 값을 가져와서 채워준다. (=프록시를 초기화한다)
        // 2-1. 즉, 자바 객체가 아닌 프록시 객체이기 때문에 잭슨 라이브러리가 순수 자바 객체가 아니라서 오류가 발생
        // 방법1. => build.gradle 'Jackson Datatype Hibernate5' 모듈 등록 + bean 등록 (JpashopApplication)

        // 방법2. => LAZY 강제 초기화
        for(Order order : all) {
            // order에서 멤버를 가져오는 것 까지는 프록시 객체다.
            // 여기서 getName하면 실제 name을 끌고 와야 하기 때문에, Lazy가 강제 초기화 된다.
            // 그래서 Member 쿼리를 날려서 JPA가 데이터를 끌고 온다.
            order.getMember().getName();
            order.getDelivery().getAddress();
        }
        return all;
    }





}
