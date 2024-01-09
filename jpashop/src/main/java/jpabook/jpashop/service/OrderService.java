package jpabook.jpashop.service;

import jpabook.jpashop.domain.Delivery;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    /**
     * 주문
     * - @Transactional => 데이터를 변경하기 때문에
     */
    @Transactional
    public Long order(Long memberId, Long itemId, int count) {

        //엔티티 조회
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        //배송정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        //주문상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);  // 단순화를 위해 item 하나만 넘김
//        OrderItem orderItem = new OrderItem();
        /**
         * 누군가 별도로 이렇게 생성해서 set해서 값을 채우는 방식으로 개발할 수 있다.
         * 생성 로직을 변경할 때 유지보수가 어려워 진다.
         *      => 이거외에 다른 스타일의 생성을 막아야 한다
         *      => protected OrderItem() {}
         *      '쓰지 말라는거구나' 알 수 있음
         */


        //주문 생성
        Order order = Order.createOrder(member, delivery, orderItem);

        //주문 저장
        orderRepository.save(order);
        return order.getId();
    }
    /**
     * 원래대로 라면 DeliveryRepository, OrderItemRepository 에서 save해서 넣어준 다음에 주문 생성을 해야 한다.
     * => 그런데 우리는 OrderRepository에서만 save했다!
     *          => Cascade 옵션 때문에 그렇다
     *          => Order를 persist 하면, OrderItem, Delviery 모두 강제로 persist 날려준다.
     * => 하나만 저장해줘도 Delivery랑 OrderItem이 자동으로 persist 된거다!
     *
     *      Q. Cascade의 범위??
     *      A. Order가 Delivery를 관리하고, OrderItem을 관리한다.
     *         -> Delivery와 OrderItem은 Order에서만 참조해서 쓴다. 다른 곳에서 참조하는 경우가 없다.
     *         -> 다른 곳에서 참조할 수 없는 private owner인 경우에 쓰면 좋다.
     *         만약 Delivery나 OrderItem이 중요해서 다른 곳에서 가져다 쓰면 Cascade 쓰면 안되고 별도의 Repostiory에서 persist 하는게 맞다.
     */

    /**
     * 주문 취소
     */
    @Transactional
    public void cancelOrder(Long orderId) {

        //주문 엔티티 조회
        Order order = orderRepository.findOne(orderId);

        //주문 취소
        order.cancel();
    }
    /**
     * JPA의 강점
     * Mybatis의 경우, 데이터를 변경했을 때 바깥에서 update 쿼리를 직접짜서 Repository에 날려야함
     * 취소 한다음 Item의 재고가 올라가야함 Item의 재고를 플러스 하는 쿼리 짜서 날려야함
     *      => 데이터를 끄집어 내서 다 쿼리에 파라미터 넣어서 해야함
     *      => Mybatis는 서비스 계층에서 비즈니스 로직을 쓸 수 밖에 없음!!!!!
     *
     * --> JPA는 엔티티 안에 있는 데이터만 바꿔주면, JPA가 알아서 바뀐 변경된 내역들을 찾아서 데이터베이스에서 업데이트 쿼리가 날라감 (더티 체킹)
     */

    /**
     * 검색
     */
    public List<Order> findOrders(OrderSearch orderSearch) {

        return orderRepository.findAllByString(orderSearch);
    }
}
