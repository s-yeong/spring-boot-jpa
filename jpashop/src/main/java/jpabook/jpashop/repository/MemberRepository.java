package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository // + JPA 예외를 스프링 기반 예외로 예외 변환
public class MemberRepository {

    // JPA가 제공하는 표준 어노테이션
    @PersistenceContext
    private EntityManager em;

    /**
     * 스프링이 엔티티메니저를 만들어서 주입해줌
     */

    public void save(Member member) {

        em.persist(member);
        /**
         * JPA가 member를 저장함
         * 영속성 컨텍스트에 멤버 엔티티를 넣음
         *  -> 트랜잭션이 커밋되는 시점에 DB에 반영됨 (DB에 insert 쿼리가 날라감)
         */
    }

    public Member findOne(Long id) {

        return em.find(Member.class, id);   // (타입, PK)
        /**
         * Member를 찾아서 반환해줌
         * 단건 조회
         */
    }

    public List<Member> findAll() {

        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
        /**
         * JPQL을 작성해아함
         * JPQL + 반환타입
         */
    }

    /**
     * JPQL과 SQL의 차이
     * SQL은 테이블을 대상으로 쿼리를 하는데, JPQL은 엔티티 객체를 대상으로 쿼리를 함
     */

    public List<Member> findByName(String name) {
        return em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();
                // 파라미터를 바인딩
    }
}
