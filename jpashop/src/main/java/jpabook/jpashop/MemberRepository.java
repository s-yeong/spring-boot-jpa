package jpabook.jpashop;

import jpabook.jpashop.domain.Member;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class MemberRepository {

    /**
     * JPA를 쓰기 위해 Entity Manager가 있어야함
     */

    @PersistenceContext // 스프링 부트가 이 어노테이션이 있으면, 엔티티메니저를 주입해줌
    private EntityManager em;

    public Long save(Member member) {
        em.persist(member);
        return member.getId();
    }

    public Member find(Long id) {
        return em.find(Member.class, id);
    }
}
