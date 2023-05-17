package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
/**
 * JPA에 모든 데이터 변경이나 로직들은 트랜잭션 안에서 실행되어야 한다. => @Transactional
 * => public으로 된 메서드는 다 트랜잭션에 걸려 들어감
 * readOnly => JPA가 "조회"하는 곳에서는 성능을 좀 더 최적화함 (영속성 컨텍스트를 플러시 하지 않으므로)
 * 데이터베이스 드라이버가 지원하면 DB에서 성능 향상
 */
public class MemberService {

    private final MemberRepository memberRepository;
    //final 키워드를 추가하면 컴파일 시점에 memberRepository를 설정하지 않는 오류를 체크할 수 있다.

    /**
     * 회원 가입
     */
    @Transactional // 쓰기에는 readOnly=true를 넣으면 안된다!! => 데이터 변경이 안됨!!
    public Long join(Member member) {

        // 중복 회원 검증 로직
        validateDuplicateMember(member);

        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {

        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
        /**
         * 이렇게 검증 로직이 있어도 문제가 발생할 수 있다
         * ex) WAS가 동시에 여러개가 뜨기 떄문에, 똑같은 이름으로 동시에 DB에 insert를 하게되면 동시에 validateDuplicateMember 통과하게 되고,
         * 동시에 save를 호출하게 된다. => 동시에 2명이 가입하게 될 수 있기 때문에
         * => 멀티 쓰레드 상황을 고려하여 DB에 name을 UNIQUE 제약 조건을 걸어줘야 안전함
         */
    }

    /**
     * 회원 전체 조회
     */
    //@Transactional(readOnly = true)
    public List<Member> findMembers() {

        return memberRepository.findAll();
    }

    //@Transactional(readOnly = true)
    public Member findOne(Long memberId) {

        return memberRepository.findOne(memberId);
    }
}

