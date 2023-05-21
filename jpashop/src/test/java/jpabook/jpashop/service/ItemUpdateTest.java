package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Book;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ItemUpdateTest {

    @Autowired
    EntityManager em;

    @Test
    public void updateTest() throws Exception {
        Book book = em.find(Book.class, 1L);

        //TX
        book.setName("azazz");

        //TX commit => JPA가 변경 부분에 대해서 찾아서 update 쿼리를 자동으로 생성해서 데이터베이스에 반영함
        //dirty checking == 변경감지
        //=> 이 메커니즘으로 기본적으로 JPA의 엔티티를 바꿀 수 있다.
        /**
         * Order의 cancel()을 호출해서 OrderStatus를 CANCEL로 바꾸고 DB에 업데이트 따로 날려주는 명령어가 없었음
         * => 엔티티의 값을 바꿔놓으면, JPA가 바꼈네 하고 트랜잭션 커밋시점에 바뀐 애 찾아서 DB에 업데이트문 날리고 커밋함
         * (flush 할 때 더티 체킹이 일어남)
         *
         * 준영속 엔티티 (영속성 컨테이너가 더는 관리하지 않는 엔티티)
         * [itemService.saveItem(book)]
         * ex) 상품 수정시, new Book()을 통해 만든 book이 새로운 book이 아니고  `id`가 세팅이 되어 있다.
         *      => JPA에 한번 들어갔다 나온 애라는 것 (데이터베이스에 한번 갔다 온 상태 - 이미 DB에 한번 저장되어서 식별자가 존재함)
         *              => JPA가 식별할 수 있는 id를 가지고 있다.
         *              => 임의로 만들어낸 엔티티도 기존 식별자를 가지고 있으면 준영속 엔티티다!
         *              (내가 new Book()으로 생성하긴 했지만, 이미 DB에 저장되고 불러온 애기 때문에 준영속 엔티티다)
         *
         * 준영속 엔티티는 JPA가 관리하지 않는다!!
         *  <-> JPA가 관리하는 영속 상태의 엔티티는 변경감지가 일어난다. (뭐가 변경됐는지 JPA가 다 눈으로 보고 있음)
         *      -> 커밋 시점에 업데이트문 날림
         * 따라서, 준영속 엔티티는 아무리 바꿔치기해도 DB에서 업데이트가 안일어난다.!
         *
         * 준영속 엔티티 수정하는 2가지 방법
         *  1. 변경 감지 기능 사용
         *
         *  2. 병합(merge) 사용
         *
         */
    }
}
