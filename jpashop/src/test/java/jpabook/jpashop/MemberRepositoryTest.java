//package jpabook.jpashop;
//
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.annotation.Rollback;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.transaction.annotation.Transactional;
//
//import static org.assertj.core.api.Assertions.*;
//
//@RunWith(SpringRunner.class)    // 스프링 관련된 것을 테스트 할 거야!
//@SpringBootTest
//public class MemberRepositoryTest {
//
//    @Autowired
//    MemberRepository memberRepository;
//
    /**
     * 엔티티 매니저를 통한 모든 데이터 변경은 항상 트랜잭션 안에서 이루어져야 한다
     */
//
//    @Test
//    @Transactional  // @Transactional이 Test에 있으면 끝나고 Rollback 한다.
//    @Rollback(false)
//    public void testMember() throws Exception {
//        //given
//        Member member = new Member();
//        member.setUsername("memberA");
//
//        //when
//        Long saveId = memberRepository.save(member);
//        Member findMember = memberRepository.find(saveId);
//
//        //then
//        assertThat(findMember.getId()).isEqualTo(member.getId());
//        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
//
//        assertThat(findMember).isEqualTo(member);   // JPA 엔티티 동일성 보장
        /**
         * 같은 트랜잭션 안에서, 저장을 하고 조회를 하면 영속성 컨텍스트가 똑같다
         * => 같은 영속성 컨택스트 안에서는 아이디 값이 같으면 같은 엔티티로 식별한다.
         * => 그렇기 때문에 SELECT 쿼리 조차 안나감 (영속성 컨텍스트에 있네)
         */
//    }
//}