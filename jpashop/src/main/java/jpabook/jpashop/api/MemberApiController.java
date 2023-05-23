package jpabook.jpashop.api;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @GetMapping("/api/v1/members")
    public List<Member> membersV1() {
        return memberService.findMembers();
        /**
         * 응답값으로 엔티티를 직접 외부에 노출함
         * 만약에, 회원에 대한 정보만 달라고 했는데, 실제 회원이 주문한게 있으면 orders 정보가 포함되어 있을 것이다.
         * => 이렇게 엔티티를 직접적으로 노출하게 되면 엔티티에 있는 정보들이 다 외부에 노출된다.
         * => @JsonIgnore를 사용하면 orders 정보가 빠진다. => 이렇게 해도 클라이언트에서 다양한 API 스타일을 요구하기 떄문에
         * 그 때마다 case가 다 다양하기 때문에 답이 없다. (+ 응답 스펙을 맞추기 위해 로직이 추가됨)
         *
         * 컬렉션을 직접 반환하면 API 스펙을 변경하기 어렵다 => 별도의 `Result 클래스` 생성으로 해결
         */
    }

    @GetMapping("/api/v2/members")
    public Result memberV2() {

        List<Member> findMembers = memberService.findMembers();
        List<MemberDto> collect = findMembers.stream()
                .map(m -> new MemberDto((m.getName())))
                .collect(Collectors.toList());

        return new Result(collect);
        /**
         * 엔티티를 DTO로 변환해서 반환
         * Result 클래스로 컬렉션을 감싸서 향후 필요한 필드를 추가할 수 있다.
         */
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String name;
        // 노출할 것만 API 스펙에 노출함
    }

    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);

        /**
         * 문제점
         * Presentation Layer를 위한 검증 로직이 `엔티티`에 있다 (@NotEmpty 등)
         * => 어떤 API에서는 NotEmpty가 필요한데, 다른 API에서는 NotEmpty가 없을 수도 있다..
         *
         * 엔티티의 스펙을 username으로 바뀌게 되면 -> API 스펙 자체가 usrename으로 바뀌게 된다.
         * => 큰 문제!! - 엔티티라는 것은 굉장히 여러 곳에서 쓰기 때문에 바뀔 확률이 높다. => 이것이 바뀐다고 API 스펙 자체가 바뀌게 된다면 문제가 된다..
         * (각각의 API를 위한 모든 요청 요구사항을 담기는 힘듬)
         *      => API 스펙을 위한 별도의 `DTO`를 만들어야 한다!!
         *      === 엔티티를 이렇게 외부에서 json 오는 것을 바인딩 받는데 쓰면 안된다!! ===
         *      (엔티티를 파라미터로 받지도 말고, 외부에 노출해서도 안된다!)
         */
    }

    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {

        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
        /**
         * 요청 값으로 Member 엔티티 대신 별도의 DTO를 받는다.
         * 장점
         * API 스펙이 안바뀐다. (name -> username) => `엔티티와 API 스펙을 명확하게 분리`
         *      Member 엔티티를 누가 바뀌었을 때 -> 컴파일 오류 -> setUsername으로 바꾸면 API는 전혀 영향을 받지 않는다.
         * 필요한 validation을 API 스펙에 맞게 넣으면 된다.
         * 유지보수가 편리해진다.
         *
         * <-> Member로 넘기면 파라미터가 뭐가 넘어올지 모른다!
         */
    }

    /**
     * PUT은 멱등성을 가진다. (같은 것을 여러번 호출해도 결과가 똑같다.)
     * PUT은 전체 업데이트를 할 때 사용한다. 부분 업데이트를 하려면 PATCH를 사용하거나 POST를 사욯아는 것이 맞다.
     */
    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberResponseV2(@PathVariable Long id,
                                                       @RequestBody @Valid UpdateMemberRequest request) {

        /**
         * CreateMemberRequest를 그대로 가져가도 될 듯한데?
         * => 등록이랑 수정은 API 스펙이 거의 다 다름 -> 수정은 제한적임 -> 별도의 Reuqest, Response를 가져가는게 맞다
         *
         * Controller 안에서만 쓸거면 static class로 만들어도 된다.
         *
         * 엔티티에는 롬복을 최대한 자제함, Getter 정도
         * DTO는 그냥 막쓴다.
         */

        memberService.update(id, request.getName());
        Member findMember = memberService.findOne(id);
        /**
         * PK하나 찝어서 조회하는 정도는 특별하게 트래픽 많은 API가 많은게 아니면 이슈가 안된다.
         * => 유지보수성 증대
         */
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }

    @Data
    static class UpdateMemberRequest {
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse {
        private Long id;
        private String name;
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
