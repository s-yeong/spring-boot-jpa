package jpabook.jpashop.api;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {

        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
        /**
         * 장점
         * API 스펙이 안바뀐다. (name -> username) => `엔티티와 API 스펙을 명확하게 분리`
         *      Member 엔티티를 누가 바뀌었을 때 -> 컴파일 오류 -> setUsername으로 바꾸면 API는 전혀 영향을 받지 않는다.
         * 필요한 validation을 API 스펙에 맞게 넣으면 된다.
         * 유지보수가 편리해진다.
         *
         * <-> Member로 넘기면 파라미터가 뭐가 넘어올지 모른다!
         */
    }

    @Data
    static class CreateMemberRequest {
        private String name;
    }

    @Data
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }
}
