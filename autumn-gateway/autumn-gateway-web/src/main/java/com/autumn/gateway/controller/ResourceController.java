package com.autumn.gateway.controller;

/**
 * @program: airlook-dev
 * @description: 资源
 * @author: qius
 * @create: 2021-01-12:19:50
 */
import com.autumn.gateway.dto.ResourceDTO;
import com.autumn.gateway.enums.BaseStatus;
import com.autumn.gateway.enums.BaseWhether;
import com.autumn.gateway.service.IDynamicRouteService;
import com.autumn.gateway.service.IResourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/gateway/resource")
public class ResourceController {

  @Resource private IResourceService resourceService;

  @Resource private IDynamicRouteService dynamicRouteService;

  @GetMapping("/save")
  public String saveRoute(ResourceDTO dto) {

    dto.setResourceId("1348554251311255554");
    com.autumn.gateway.entity.Resource resource = resourceService.getById(dto.getResourceId());

    Map<String, String> stripPrefixParams = new HashMap<>();
    stripPrefixParams.put("configCode", "StripPrefix");
    stripPrefixParams.put("configType", "PREDICATE");
    stripPrefixParams.put("parts", "0");

    Map<String, String> hystrixParams = new HashMap<>();

    hystrixParams.put("configCode", "Hystrix");
    hystrixParams.put("configType", "FILTER");
    hystrixParams.put("name", "fallbackcmd");
    hystrixParams.put("fallbackUri", "forward:/fallback");

    Map<String, String> retryParams = new HashMap<>();
    hystrixParams.put("configCode", "Retry");
    hystrixParams.put("configType", "FILTER");
    hystrixParams.put("retries", "3");
    hystrixParams.put("series", "SUCCESSFUL,CLIENT_ERROR");
    hystrixParams.put("methods", "get");

    dto.setParams(new ArrayList<>());
    dto.getParams().add(stripPrefixParams);
    dto.getParams().add(hystrixParams);
    dto.getParams().add(retryParams);

    resourceService.saveConfig(resource, dto.getParams());

    return "success";
  }

  @GetMapping(value = "/reload/{resourceId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public String reload(@NotBlank @PathVariable("resourceId") String resourceId) {

    com.autumn.gateway.entity.Resource resource = resourceService.getById(resourceId);

    if (resource.getDeleted() == BaseWhether.YES || resource.getStatus() == BaseStatus.INVALID) {
      return "error";
    }

    dynamicRouteService.updateResource(resource);

    return "success";
  }

  @GetMapping(value = "/add/{resourceId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public String add(@NotBlank @PathVariable("resourceId") String resourceId) {

    com.autumn.gateway.entity.Resource resource = resourceService.getById(resourceId);

    if (resource.getDeleted() == BaseWhether.YES || resource.getStatus() == BaseStatus.INVALID) {
      return "error";
    }

    dynamicRouteService.addResource(resource);

    return "success";
  }

  @GetMapping(value = "/del/{resourceId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public String del(@NotBlank @PathVariable("resourceId") String resourceId) {

    com.autumn.gateway.entity.Resource resource = resourceService.getById(resourceId);

    if (resource.getDeleted() == BaseWhether.YES || resource.getStatus() == BaseStatus.INVALID) {
      return "error";
    }

    dynamicRouteService.delete(resource.getId());

    return "success";
  }
}
