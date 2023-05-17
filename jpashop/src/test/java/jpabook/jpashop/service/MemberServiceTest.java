package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;

/**
 * 순수한 단위 테스트를 만들게 아니라,
 * JPA가 실제 DB까지 도는 것을 보여주기 위해서 메모리모드로 DB까지 엮어서 테스트함
 * 스프링이랑 Integeration해서 테스트함
 */
@RunWith(SpringRunner.class)    // junit 실행할 때, Spring이랑 같이 엮어서 실행할래!
@SpringBootTest // 스프링을 띄운 상태에서 테스트 하기 위해(없으면 @Autowirde 다 실패)
@Transactional
/**
 * Transactional이 Rollbakc을 하는 이유는 이 테스트가 반복헤서 해야 하기 때문에 DB에 데이터가 남으면 안된다!
 * 이 어노테이션이 `테스트 케이스`에서 사용될 때만 롤백한다!
 */
public class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    EntityManager em;

    @Test
    public void 회원가입() throws Exception {
        //given
        Member member = new Member();
        member.setName("kim");

        //when
        Long savedId = memberService.join(member);

        //then
        em.flush(); // -> 영속성 컨텍스트가 멤버 객체에 들어간 후 플러시 해주면, 쿼리로 DB에 반영이 된다.
        assertEquals(member, memberRepository.findOne(savedId));

        /**
         * 쿼리문에서 insert문이 없다!
         * => memberRepository에서 join을 하면, EntityManager에 persist함
         *      => persist를 한다고 해서 DB에 insert 쿼리문이 나가지 않는다.
         *      => 데이터베이스 트랜잭션이 커밋이 될 때 플러시가 되면서 DB에 insert 쿼리가 나간다!
         *
         * 스프링에서 Transactional은 기본적으로 트랜잭션 커밋을 아니고 롤백을 한다
         * 스프링에서 롤백을 해버리면 JPA 입장에서는, insert 쿼리를 DB에 날릴 필요가 없다. => 영속성 컨텍스트가 플러시를 안한다!
         *      DB에 쿼리 날리는 것을 보고 싶으면 => Rollback(false)로 두면 커밋한다.
         *
         *
         * `플러시`란, 영속성 컨텍스트에 있는 어떤 변경이나 등록 내용을 데이터베이스에 반영하는 것!!
         */
    }

    @Test(expected = IllegalStateException.class)   // 예외가 터져서 나간 애가 IllegalStateException
    public void 중복_회원_예외() throws Exception {
        //given
        Member member1 = new Member();
        member1.setName("kim");
        Member member2 = new Member();
        member2.setName("kim");

        //when
        memberService.join(member1);
        memberService.join(member2);    // -> 예외 발생

        //then
        fail("예외가 발생해야 한다.");   // 코드가 돌다 여기로 오면 안된다!
    }

}