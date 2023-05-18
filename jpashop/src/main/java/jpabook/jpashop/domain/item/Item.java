package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.Category;
import jpabook.jpashop.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@Getter @Setter
public abstract class Item {

    /**
     * 상속관계 전략을 지정 - 부모 클래스에서 잡아줘야함
     * => 싱글테이블 전략 사용
     */

    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

    //==비즈니스 로직==
    /**
     * 보통 도메인 주도 설계일 때, 엔티티 자체에 해결할 수 있는 것들을 주로 엔티티 안에 비즈니스 로직을 넣는게 좋다.
     *
     * => stockQuantity를 변경해야 할 일이 있으면, 핵심 비즈니스 메서드르 가지고 변경해야한다!! (Setter가 아닌!)
     */

    /**
     * stock 증가 (재고 수량 증가)
     */
    public void addStock(int quantity) {

        this.stockQuantity += quantity;
    }

    /**
     * stock 감소
     */
    public void removeStock(int quantity) {

        int restStock = this.stockQuantity - quantity;
        if(restStock < 0) {
            throw new NotEnoughStockException("need more stock");
        }
        /**
         * 보통 개발할 때, ItemService에서 StockQuantity를 가져와서
         * Stock을 더해서 넣고 값을 만든 다음에 마지막에 setItem.setStockQuantity 해서 그 결과를 넣는 방식으로 코딩을 했을 거임
         * => 객체 지향적으로 생각해보면, "데이터를 가지고 있는 쪽에 비즈니스 메서드가 있는게 가장 좋다!!" => 그래야 응집력이 있음
         *      => 핵심 비즈니스 로직을 엔티티에 직접 넣음!
         */
        this.stockQuantity -= quantity;
    }
}
