package jpabook.jpashop.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UpdateItemDto {

    private String name;
    private int price;
    private int stockQuantity;

}
