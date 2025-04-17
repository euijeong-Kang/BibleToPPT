/**
 * PowerPoint 프레젠테이션을 생성하는 인터페이스입니다.
 * 이 인터페이스는 성경 구절을 기반으로 PowerPoint 프레젠테이션을 생성합니다.
 */
package com.ej.bibletoppt.service.command;

import com.ej.bibletoppt.controller.dto.PresentationRequest;

public interface IPPTGenerator {
    /**
     * 성경 구절을 기반으로 PowerPoint 프레젠테이션을 생성합니다.
     * 
     * @param request 프레젠테이션 생성 요청 정보
     */
    void createPresentation(PresentationRequest request);
}