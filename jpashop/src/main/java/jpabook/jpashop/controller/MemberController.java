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
     *      => 화면에서 넘어올 때 validation이랑 실제 도메인이 원하는 validation이 다를 수 있다
     *      => 화면에 fit한 폼 데이터를 만들고 그걸 데이터 받는게 낫다.
     *  => 폼 객체를 이용해 화면 계층과 서비스 계층을 명확하게 분리함
     */
}
