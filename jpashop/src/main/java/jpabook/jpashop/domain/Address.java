package jpabook.jpashop.domain;

import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable // 어딘가에 내장될 수 있다
@Getter
public class Address {

    private String city;
    private String street;
    private String zipcode;

    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }

    /**
     * 값 타입은 기본적으로 변경이 불가하게 설계 해야함
     * => 생성할 때만 값이 세팅되도록 Setter를 제공X => 변경이 불가능하게 생성자에서 값을 모두 초기화
     */

    protected Address() {
    }

    /**
     * JPA 구현 라이브러리가 객체를 생성할 때 리플랙션 같은 기술을 지원하기 위해 기본 생성자가 필요 (JPA가 제약을 둠)
     * => 보통 JPA 스펙에서는 protected까지 허용해줌
     */
}
