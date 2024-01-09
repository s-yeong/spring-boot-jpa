package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jpabook.jpashop.domain.item.Item;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA에는 protected까지 기본생성자를 만들도록 허용해줌
public class OrderItem {

    @Id
    @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;


    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    private int orderPrice;     //주문 가격

    private int count;      // 주문 수량

//    protected OrderItem() {
//    }

    //== 생성 메서드==//
    public static OrderItem createOrderItem(Item item, int orderPrice, int count) {

        OrderItem orderItem = new OrderItem();
        // item에 가격이 있으니까 그 가격을 orderPrice 하면 되지 않나요? -> orderPrice가 쿠폰이나 할인 때문에 바뀔 수 있다.
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);

        item.removeStock(count);    // 넘어온 만큼 아이템의 재고를 까줌

        return orderItem;
    }


    //==비즈니스 로직==/
    public void cancel() {
        // Item의 재고 수량을 원복해준다.

        getItem().addStock(count);  // 재고를 주문 수량만큼 늘려준다.
    }

    //== 조회 로직==//

    /**
     * 주문상품 전체 가격 조회
     */
    public int  getTotalPrice() {

        // 주문 할 때 주문 가격과 수량이 있다. => 곱해야함
        return getOrderPrice() * getCount();
    }
}
