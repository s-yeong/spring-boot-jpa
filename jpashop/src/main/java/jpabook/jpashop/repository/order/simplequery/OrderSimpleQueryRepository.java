package jpabook.jpashop.repository.order.simplequery;

import java.util.List;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryRepository {

    /**
     * Repository는 가급적 순수한 엔티티만 조회하는 데 쓰인다.
     * - 이 경우는 화면에 박힌 거다.
     * - 이런 식으로 복잡한 조인 쿼리를 가지고 Dto를 뽑아야 하는 경우 QueryService, QueryRepository 이렇게 해서 보통 별도로 뽑아 낸다.
     */

    private final EntityManager em;

    public List<OrderSimpleQueryDto> findOrderDtos() {

        // 엔티티를 바로 넘기는 게 안된다. (new operation 해서 엔티티를 넘기면, 엔티티가 식별자로 넘어간다.)
        // 결과적으로 API 스펙이 여기에 들어와 있는 형식이다.
        // repository는 엔티티의 객체 그래프를 조회하고 이럴 때 사용한다. (물리적으로는 계층이 나눠져 있지만, 논리적으로 계층이 깨져있음)
        // => repository가 화면을 의존하고 있다. API 스펙이 바뀌면 이 reposiotry 계층을 고쳐야 한다.
        return em.createQuery(
                "select new jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" +
                    " from Order o" +
                    " join o.member m" +
                    " join o.delivery d", OrderSimpleQueryDto.class)
            .getResultList();
    }

}
