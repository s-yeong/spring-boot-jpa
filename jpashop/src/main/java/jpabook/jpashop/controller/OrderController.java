package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.service.ItemService;
import jpabook.jpashop.service.MemberService;
import jpabook.jpashop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class OrderController {

    private final OrderService orderService;
    private final MemberService memberService;
    private final ItemService itemService;

    @GetMapping("/order")
    public String createForm(Model model) {

        List<Member> members = memberService.findMembers();
        List<Item> items = itemService.findItems();

        model.addAttribute("members", members);
        model.addAttribute("items", items);

        return "order/orderForm";
    }

    @PostMapping("/order")
    public String order(@RequestParam Long memberId, @RequestParam Long itemId, @RequestParam int count) {

        orderService.order(memberId, itemId, count);
        return "redirect:/orders";
    }

    /**
     * 커멘드성 ex)주문은 컨트롤러 레벨에서는 식별자만 넘기고,
     * 서비스에서 엔티티를 찾는 것 부터 한다.
     * => 트랜잭션 안에서 엔티티를 조회해야 영속 상태로 진행이 된다.
     * (조회는 상관없음)
     * <p>
     * 바깥에서 해버리면 트랜잭션 없이 조회했기 때문에 영속성이 없기 때문에 그 상태로 서비스에 넘기면
     * JPA와 관계없는 애가 넘어옴
     */

    @GetMapping("/orders")
    public String orderList(OrderSearch orderSearch, Model model) {

        List<Order> orders = orderService.findOrders(orderSearch);
        model.addAttribute("orders", orders);
//        model.addAttribute("orderSearch", orderSearch);   // 생략

        return "order/orderList";
    }

    @PostMapping("/orders/{orderId}/cancel")
    public String cancelOrder(@PathVariable Long orderId) {

        orderService.cancelOrder(orderId);
        return "redirect:/orders";
    }
}
