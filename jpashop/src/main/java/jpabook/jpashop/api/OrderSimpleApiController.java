package jpabook.jpashop.api;

import static java.util.stream.Collectors.toList;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.Data;
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

    /**
     * 2. 엔티티를 조회해서 DTO로 변환 (feth join 사용X)
     * - 단점 : 지연로딩으로 쿼리 N번 호출
     * - 엔티티가 바뀌어도 API 스펙이 바뀌지 않는다.
     * - 3개의 테이블을 조회해야 하는 상황
     * - 두 개의 주문서가 있다고 했을 때, 첫번째 주문서는 쿼리 3번으로 완성되고(주문, 멤버, 딜리버리), 두번째 주문서는 쿼리 2번으로 완성된다. (멤버, 딜리버리)
     *      - ORDER - SQL 1번 실행 -> 결과 주문 수2개
     */
    @GetMapping("/api/v2/simpmle-orders")
    public List<SimpleOrderDto> orderV2() {

        // [ORDER 2개]
        // 이 것이 'N+1' 문제다. (1+N 문제라고 해야할 것 같은 느낌)
        // 1 + 회원 N + 배송 N (1+2+2)
         // 1. 처음에 'N개'의 orders를 가져오기 위해 쿼리를 날림
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        
        // [2번 돈다.]
        // 2. 첫번째 쿼리의 결과로 'N번' 만큼 추가 쿼리가 실행된다.
        List<SimpleOrderDto> result = orders.stream()
            .map(o -> new SimpleOrderDto(o))
            .collect(toList());
        return result;

//        return orderRepository.findAllByString(new OrderSearch()).stream()
//            .map(SimpleOrderDto::new)
//            .collect(toList());
    }

    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address; // Address는 엔티티가 아닌 Value Object다. 단순 Address라는 타입을 정했다고 생각하면 된다.

        // DTO가 이렇게 entity를 parameter로 받는 것은 크게 문제가 되지 않는다.
        // 별로 중요하지 않는데서 중요한 entity에 의존하는 것이기 때문
        public SimpleOrderDto(Order order) {
            this.orderId = order.getId();
            this.name = order.getMember().getName();    // LAZY 초기화
            // => 영속성 컨텍스트가 memberId를 가지고 영속성 컨텍스트에 찾아보고 없으면 DB 쿼리를 날린다.
            this.orderDate = order.getOrderDate();
            this.orderStatus = order.getStatus();
            this.address =  order.getDelivery().getAddress();   // LAZY 초기화
        }
    }





}
