package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {
    /**
     * ItemService는 ItemRepository에 단순하게 위임만 하는 클래스
     */
    private final ItemRepository itemRepository;

    @Transactional
    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    /**
     * 변경 감지 기능 사용
     * 영속성 컨텍스트에서 엔티티를 다시 조회한 후에 데이터를 수정하는 방법
     * 트랜잭션 안에서 엔티티를 다시 조회, 변경할 값 선택 -> 트랜잭션 시점에 변경 감지 동작해서 업데이트 쿼리 날림
     *
     * <-> 병합 사용
     * 머지해서 넘어온 파라미터 값으로 다 찾아온 값을 다 바꿔치기 해버림
     * => 바꿔치기 헀기 때문에 트랜잭션 커밋될 때 반영이 됨! => (updateItem() 한 것을 한줄로 해주는 것)
     * 이 때, 파라미터로 넘긴 객체는 안바뀌고 `반환된 값`이 영속성 컨텍스트에서 관리된 객체다.
     *
     * 주의점!!
     * 변경 감지 기능을 사용하면 `원하는 속성`만 선택해서 변경할 수 있지만, 병합을 사용하면 `모든 속성`이 변경된다!
     * => 병합시 값이 없으면 `null`로 업데이트 할 위험이 있다.(모든 필드를 교체)
     * 실무에서는 복잡하기 때문에 merge를 통해 깔끔하게 처리할 수 없다. -> 가급적 merge 쓰지말자!!!!
     */
    @Transactional
    public void updateItem(Long itemId, String name, int price, int stockQuantity) {   // param : 파라미터로 넘어온 준영속 상태의 엔티티

        //id를 기반으로 실제 db에 있는 영속상의 엔티티를 찾아옴
        Item findItem = itemRepository.findOne(itemId); // 같은 엔티티 조회

        /**
         * price,name, stockQuantity만 넘기는 메서드나 addStock()처럼 의미있는 메서드를 만들어야지
         * set을 통해 깔면 안된다!! -> 이렇게 해야 변경 지점이 엔티티로 간다!!
         * 조금만 복잡해도 setPrice라고 하면, 도대체 어디서 바꾸는건지 한참 뒤져야 한다!! (역추적 하기 힘듬)
         */
//        findItem.change(price, name, stockQuantitiy)
//        findItem.addStock()
        findItem.setName(name);
        findItem.setPrice(price);
        findItem.setStockQuantity(stockQuantity);
//        return findItem;
        /**
         * 이후에, itemRepository에서 save, EntityManager의 persist, merge 다 호출할 필요가 없다!
         * findItem으로 찾아온 Item은 영속상태다 -> 값을 세팅하면 @Transactional에 의해서 커밋이 된다.
         *  -> JPA에서 flush 날림(영속성 엔티티 중에 변경된 애들을 다 찾음) -> 바뀐 값을 업데이트 쿼리 날림
         */
    }

    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    public Item findOne(Long id) {
        return itemRepository.findOne(id);
    }
}
