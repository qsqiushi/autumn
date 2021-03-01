package com.autumn.gateway.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @program: airlook-dev
 * @description:
 * @author: qius
 * @create: 2021-01-12:19:51
 **/
@Data
public class ResourceDTO {

    private String resourceId;


    private List<Map<String, String>> params;
}
