/**
 * 서비스 인스턴스를 생성하는 팩토리 인터페이스입니다.
 * 이 인터페이스는 서비스 로케이터에서 서비스 인스턴스를 지연 생성할 때 사용됩니다.
 * 
 * @param <T> 생성할 서비스의 타입
 */
package com.ej.bibletoppt.infrastructure.di;

@FunctionalInterface
public interface ServiceFactory<T> {
    /**
     * 서비스 인스턴스를 생성합니다.
     * 
     * @param locator 다른 서비스를 참조하기 위한 서비스 로케이터
     * @return 생성된 서비스 인스턴스
     */
    T create(ServiceLocator locator);
}