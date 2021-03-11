package com.autumn.gateway.loadbalancer;

import com.autumn.gateway.enums.LoadBalancerEnum;
import com.autumn.gateway.loadbalancer.model.Strategy;
import com.autumn.gateway.loadbalancer.model.WeightMeta;
import com.autumn.gateway.loadbalancer.utils.WeightRandomUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.reactive.DefaultResponse;
import org.springframework.cloud.client.loadbalancer.reactive.EmptyResponse;
import org.springframework.cloud.client.loadbalancer.reactive.Request;
import org.springframework.cloud.client.loadbalancer.reactive.Response;
import org.springframework.cloud.loadbalancer.core.NoopServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: autumn
 * @description:
 * @author: qiushi
 * @create: 2021-03-08:15:53
 */
@Slf4j
public class GrayLoadBalancer implements ReactorServiceInstanceLoadBalancer {

  private ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider;
  private String serviceId;

  /** 负载均衡策略 */
  private Strategy strategy;

  public GrayLoadBalancer(
      ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider,
      String serviceId,
      Strategy strategy) {
    this.serviceId = serviceId;
    this.serviceInstanceListSupplierProvider = serviceInstanceListSupplierProvider;
    this.strategy = strategy;
  }

  @Override
  public Mono<Response<ServiceInstance>> choose(Request request) {
    HttpHeaders headers = (HttpHeaders) request.getContext();
    if (this.serviceInstanceListSupplierProvider != null) {
      ServiceInstanceListSupplier supplier =
          this.serviceInstanceListSupplierProvider.getIfAvailable(
              NoopServiceInstanceListSupplier::new);
      return supplier
          .get()
          .next()
          .map(list -> getInstanceResponse((List<ServiceInstance>) list, headers));
    }

    return null;
  }

  private Response<ServiceInstance> getInstanceResponse(
      List<ServiceInstance> instances, HttpHeaders headers) {
    // 如果没有实例
    if (instances.isEmpty()) {
      return getServiceInstanceEmptyResponse();
    } else {
      if (strategy.getLoadBalancerEnum() == LoadBalancerEnum.VERSION) {
        return getServiceInstanceResponseByVersion(instances, headers);
      } else if (strategy.getLoadBalancerEnum() == LoadBalancerEnum.WEIGHT) {
        return getServiceInstanceResponseWithWeight(instances);
      } else {
        return getServiceInstanceEmptyResponse();
      }
    }
  }

  /**
   * 根据版本进行分发
   *
   * @param instances
   * @param headers
   * @return
   */
  private Response<ServiceInstance> getServiceInstanceResponseByVersion(
      List<ServiceInstance> instances, HttpHeaders headers) {
    String versionNo = headers.getFirst("version");
    log.info("当前请求的版本是[{}]", versionNo);

    if (StringUtils.isEmpty(versionNo)) {
      // 如果版本为空 返回
      return getServiceInstanceEmptyResponse();
    }

    String serverUrl = strategy.getParams().get(versionNo);

    if (StringUtils.isEmpty(serverUrl)) {
      // 找不到对应版本对应的实例ip
      return getServiceInstanceEmptyResponse();
    }

    ServiceInstance serviceInstance = null;

    // 通过ip和端口获得实例
    for (ServiceInstance instance : instances) {
      if (StringUtils.equals(instance.getHost() + ":" + instance.getPort(), serverUrl)) {
        serviceInstance = instance;
        break;
      }
    }

    if (ObjectUtils.isEmpty(serviceInstance)) {
      return getServiceInstanceEmptyResponse();
    }
    return new DefaultResponse(serviceInstance);
  }

  /**
   * 根据在nacos中配置的权重值，进行分发
   *
   * @param instances
   * @return
   */
  private Response<ServiceInstance> getServiceInstanceResponseWithWeight(
      List<ServiceInstance> instances) {

    // 计算个个实例所占有的权重
    Map<ServiceInstance, Integer> weightMap = new HashMap<>();
    for (ServiceInstance instance : instances) {
      if (StringUtils.isNotEmpty(
          strategy.getParams().get(instance.getHost() + ":" + instance.getPort()))) {
        weightMap.put(
            instance,
            Integer.valueOf(
                strategy.getParams().get(instance.getHost() + ":" + instance.getPort())));
      }
    }
    // 计算权重
    WeightMeta<ServiceInstance> weightMeta = WeightRandomUtils.buildWeightMeta(weightMap);
    if (ObjectUtils.isEmpty(weightMeta)) {
      return getServiceInstanceEmptyResponse();
    }
    // 通过权重获取实例
    ServiceInstance serviceInstance = weightMeta.random();
    if (ObjectUtils.isEmpty(serviceInstance)) {
      return getServiceInstanceEmptyResponse();
    }
    return new DefaultResponse(serviceInstance);
  }

  private Response<ServiceInstance> getServiceInstanceEmptyResponse() {
    log.warn("No servers available for service: " + this.serviceId);
    return new EmptyResponse();
  }
}
