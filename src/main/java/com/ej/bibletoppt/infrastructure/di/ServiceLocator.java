/**
 * 서비스 로케이터 패턴을 구현한 클래스입니다.
 * 이 클래스는 애플리케이션에서 사용되는 모든 서비스의 인스턴스를 관리하고 제공합니다.
 * 싱글톤 패턴 대신 인터페이스 기반의 설계를 사용하여 테스트 용이성과 유연성을 높입니다.
 */
package com.ej.bibletoppt.infrastructure.di;

import java.util.HashMap;
import java.util.Map;

public class ServiceLocator {
    private final Map<Class<?>, Object> services = new HashMap<>();
    private final Map<Class<?>, ServiceFactory<?>> factories = new HashMap<>();

    /**
     * 서비스 인스턴스를 등록합니다.
     * 
     * @param <T> 서비스 타입
     * @param serviceType 서비스 인터페이스 클래스
     * @param implementation 서비스 구현체 인스턴스
     */
    public <T> void register(Class<T> serviceType, T implementation) {
        services.put(serviceType, implementation);
    }

    /**
     * 서비스 팩토리를 등록합니다.
     * 
     * @param <T> 서비스 타입
     * @param serviceType 서비스 인터페이스 클래스
     * @param factory 서비스 인스턴스를 생성하는 팩토리
     */
    public <T> void registerFactory(Class<T> serviceType, ServiceFactory<T> factory) {
        factories.put(serviceType, factory);
    }

    /**
     * 등록된 서비스 인스턴스를 가져옵니다.
     * 
     * @param <T> 서비스 타입
     * @param serviceType 서비스 인터페이스 클래스
     * @return 서비스 인스턴스
     * @throws IllegalArgumentException 요청한 서비스가 등록되어 있지 않은 경우
     */
    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> serviceType) {
        // 이미 생성된 인스턴스가 있는지 확인
        Object service = services.get(serviceType);
        
        // 인스턴스가 없고 팩토리가 등록되어 있으면 팩토리를 통해 생성
        if (service == null && factories.containsKey(serviceType)) {
            ServiceFactory<T> factory = (ServiceFactory<T>) factories.get(serviceType);
            service = factory.create(this);
            services.put(serviceType, service); // 생성된 인스턴스 캐싱
        }
        
        if (service == null) {
            throw new IllegalArgumentException("요청한 서비스가 등록되어 있지 않습니다: " + serviceType.getName());
        }
        
        return (T) service;
    }

    /**
     * 모든 서비스 등록을 초기화합니다.
     */
    public void clear() {
        services.clear();
        factories.clear();
    }
}