package jpabook.jpashop.repository;

import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager em;

    public void save(Item item) {

        if (item.getId() == null) {
            em.persist(item);
        } else {
            em.merge(item);
        }

        /**
         * item은 JPA에 저장하기 전까지 id값이 없다 => id 값이 없다는 것은 완전히 새로 생성하는 객체
         * => em.persist를 통해 신규로 등록
         * id 값이 있다는 것은 이미 DB에 있는 것을 가져온 것
         * => em.merge (업데이트랑 비슷함)
         */
    }

    public Item findOne(Long id) {
        return em.find(Item.class, id);
    }

    public List<Item> findAll() {
        // 여러개 찾는 것은 JPQL 작성해야함
        return em.createQuery("select i from Item i", Item.class)
                .getResultList();
    }

}
