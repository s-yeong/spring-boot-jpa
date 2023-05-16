package jpabook.jpashop.domain;

import jpabook.jpashop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter @Setter
public class Category {

    @Id
    @GeneratedValue
    @Column(name = "category_id")
    private Long id;

    private String name;

    @ManyToMany
    @JoinTable(name = "category_item",
            joinColumns = @JoinColumn(name = "category_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id"))
    private List<Item> items = new ArrayList<>();

    /**
     * ManyToMany를 위해 JoinTable이 필요!
     * 실제 데이터베이스에서는 중간 테이블이 존재하기 때문에
     * 객체에서는 다대다 관계가 가능한데, 관계형 DB는 컬렉션 관계를 양쪽에서 가질 수 있는게 아니기 때문에
     * 일대다 다대일로 풀어내는 중간 테이블이 있어야 가능함
     *
     * 실무에서는 거의 안씀!!!!
     * 중간 테이블(category_item)에 컬럼을 추가할 수 없고, 세밀하게 쿼리를 실행하기 어렵기 때문에
     * 실무에서 사용하기에는 한계가 있다 => 중간 엔티티를 만들고 @ManyToOne, @OneToMany로 매핑해서 사용하자!
     */

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private List<Category> child = new ArrayList<>();

    /**
     * 셀프로 양방향 연관관계를 검
     */

    // ==연관관계 편의 메서드==
    public void addChildCategory(Category child) {
        this.child.add(child);
        child.setParent(this);
    }
}
