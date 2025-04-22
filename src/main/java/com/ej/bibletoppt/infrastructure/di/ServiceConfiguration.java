/**
 * 서비스 구성을 담당하는 클래스입니다.
 * 이 클래스는 애플리케이션에서 사용되는 모든 서비스의 인스턴스를 생성하고 등록합니다.
 */
package com.ej.bibletoppt.infrastructure.di;

import com.ej.bibletoppt.BibleVerseValidator;
import com.ej.bibletoppt.infrastructure.ISettingsManager;
import com.ej.bibletoppt.infrastructure.SettingsManager;
import com.ej.bibletoppt.infrastructure.database.ISQLiteConnector;
import com.ej.bibletoppt.infrastructure.database.SQLiteConnector;
import com.ej.bibletoppt.service.IBibleVerseValidator;
import com.ej.bibletoppt.service.IPreviewService;
import com.ej.bibletoppt.service.IVerseManagementService;
import com.ej.bibletoppt.service.PreviewService;
import com.ej.bibletoppt.service.VerseManagementService;
import com.ej.bibletoppt.service.command.IPPTGenerator;
import com.ej.bibletoppt.service.command.PPTGenerator;
import com.ej.bibletoppt.service.query.ISearchBible;
import com.ej.bibletoppt.service.query.SearchBible;

public class ServiceConfiguration {
    private static ServiceLocator instance;

    /**
     * 서비스 로케이터 인스턴스를 가져옵니다.
     * 인스턴스가 없는 경우 새로 생성하고 구성합니다.
     * 
     * @return 구성된 ServiceLocator 인스턴스
     */
    public static synchronized ServiceLocator getServiceLocator() {
        if (instance == null) {
            instance = new ServiceLocator();
            configureServices(instance);
        }
        return instance;
    }

    /**
     * 서비스 로케이터에 모든 서비스를 등록합니다.
     * 
     * @param locator 서비스를 등록할 ServiceLocator 인스턴스
     */
    private static void configureServices(ServiceLocator locator) {
        // 데이터베이스 연결
        locator.registerFactory(ISQLiteConnector.class, serviceLocator -> new SQLiteConnector());

        // 검색 서비스
        locator.registerFactory(ISearchBible.class, serviceLocator -> 
            new SearchBible(serviceLocator.get(ISQLiteConnector.class)));

        // 성경 구절 검증 서비스
        locator.registerFactory(IBibleVerseValidator.class, serviceLocator -> 
            new BibleVerseValidator(serviceLocator.get(ISearchBible.class)));

        // 설정 관리 서비스
        locator.registerFactory(ISettingsManager.class, serviceLocator -> 
            new SettingsManager(serviceLocator.get(ISQLiteConnector.class)));

        // PPT 생성 서비스
        locator.registerFactory(IPPTGenerator.class, serviceLocator -> 
            new PPTGenerator(serviceLocator.get(ISearchBible.class)));

        // 구절 관리 서비스
        locator.registerFactory(IVerseManagementService.class, serviceLocator -> 
            new VerseManagementService(serviceLocator.get(IBibleVerseValidator.class)));

        // 미리보기 서비스
        locator.registerFactory(IPreviewService.class, serviceLocator -> 
            new PreviewService(serviceLocator.get(ISearchBible.class)));
    }

    /**
     * 테스트를 위해 서비스 로케이터를 초기화합니다.
     */
    public static void reset() {
        if (instance != null) {
            instance.clear();
            instance = null;
        }
    }
}
