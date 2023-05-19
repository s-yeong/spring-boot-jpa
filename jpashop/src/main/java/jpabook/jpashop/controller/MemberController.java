package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members/new")
    public String createForm(Model model) {

        model.addAttribute("memberForm", new MemberForm());
        return "members/createMemberForm";
    }

    /**
     * MemberForm을 넘겼기 때문에 화면에서 이 객체에 접근할 수 있다.
     */

    @PostMapping("/members/new")
    public String create(@Valid MemberForm form, BindingResult result) {

        if (result.hasErrors()) {
            return "members/createMemberForm";
        }

        Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());
        Member member = new Member();
        member.setName(form.getName());
        member.setAddress(address);

        memberService.join(member);

        return "redirect:/";
    }

    /**
     * Q. Member 엔티티를 그대로 넣으면 안되나요? 왜 form을 굳이 넣나요?
     * => 화면에서 넘어올 때 validation이랑 실제 도메인이 원하는 validation이 다를 수 있다
     * => 화면에 fit한 폼 데이터를 만들고 그걸 데이터 받는게 낫다.
     * => 폼 객체를 이용해 화면 계층과 서비스 계층을 명확하게 분리함
     */

    @GetMapping("/members")
    public String list(Model model) {
        List<Member> members = memberService.findMembers();
        // -> 이것도 엔티티(Member)를 그대로 쓰기 보다는 Dto로 변환해서 화면에 꼭 필요한 데이터만 출력하도록 하자
        model.addAttribute("members", members);
        return "members/memberList";
    }

    /**
     * 폼 객체 vs 엔티티 직접 사용
     * 요구사항이 정말 '단순'할 때는 폼 객체(MemberForm)없이 엔티티(Member)를 직접 등록과 수정화면에서 사용해도 됨
     *      -> 하지만 화면 `요구사항이 복잡`해지기 시작하면, 엔티티에 화면을 처리하기 위한 기능이 점점 증가함!!
     *      -> 결과적으로 엔티티는 점점 화면에 종속적으로 변하고, 화면 기능 때문에 지저분해진 엔티티는 유지보수 어려워짐
     *
     * => 엔티티는 `핵심 비즈니스 로직`만 가지고 있고, 화면을 위한 로직은 없어야 한다.
     *    화면이나 API에 맞는 폼 객체나 DTO를 사용하자! 그래야 화면이나 API 요구사항을 이것들로 처리하고, 엔티티는 최대한 순수하게 유지하자!
     *
     * 템플릿 엔진을 렌더링할 때는 서버 안에서 기능이 돌기 때문에 엔티티를 화면에 전달해도 괜찮다.
     * (서버에서 내가 원하는 데이터만 찍어서 출력하기 때문에)
     *
     * !!!!! API를 넘길 때는 이유를 불문하고 절대 엔티티를 넘기면 안된다 !!!!!
     * 2가지 문제가 있다.
     * 1. 패스워드가 그대로 노출된다. 2. API 스펙이 변해버린다.
     */
}
