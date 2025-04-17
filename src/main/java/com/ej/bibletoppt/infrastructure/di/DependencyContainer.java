/**
 * 의존성 주입을 위한 컨테이너 클래스입니다.
 * 이 클래스는 애플리케이션에서 사용되는 모든 서비스의 인스턴스를 생성하고 관리합니다.
 */
package com.ej.bibletoppt.infrastructure.di;

import com.ej.bibletoppt.BibleVerseValidator;
import com.ej.bibletoppt.infrastructure.ISettingsManager;
import com.ej.bibletoppt.infrastructure.SettingsManager;
import com.ej.bibletoppt.infrastructure.database.ISQLiteConnector;
import com.ej.bibletoppt.infrastructure.database.SQLiteConnector;
import com.ej.bibletoppt.service.IBibleVerseValidator;
import com.ej.bibletoppt.service.command.IPPTGenerator;
import com.ej.bibletoppt.service.command.PPTGenerator;
import com.ej.bibletoppt.service.query.ISearchBible;
import com.ej.bibletoppt.service.query.SearchBible;

/**
 * 의존성 주입을 위한 컨테이너 클래스입니다.
 * 싱글톤 패턴을 사용하여 애플리케이션 전체에서 하나의 인스턴스만 사용합니다.
 */
public class DependencyContainer {
    private static DependencyContainer instance;

    // 서비스 인스턴스
    private final ISQLiteConnector sqliteConnector;
    private final IBibleVerseValidator bibleVerseValidator;
    private final ISearchBible searchBible;
    private final IPPTGenerator pptGenerator;
    private final ISettingsManager settingsManager;

    private DependencyContainer() {
        // 의존성 생성 및 주입
        sqliteConnector = new SQLiteConnector();
        searchBible = new SearchBible(sqliteConnector);
        bibleVerseValidator = new BibleVerseValidator(searchBible);
        settingsManager = new SettingsManager(sqliteConnector);
        pptGenerator = new PPTGenerator(searchBible);
    }

    /**
     * DependencyContainer의 인스턴스를 가져옵니다.
     * 인스턴스가 없는 경우 새로 생성합니다.
     * 
     * @return DependencyContainer 인스턴스
     */
    public static synchronized DependencyContainer getInstance() {
        if (instance == null) {
            instance = new DependencyContainer();
        }
        return instance;
    }

    /**
     * SQLiteConnector 인스턴스를 가져옵니다.
     * 
     * @return SQLiteConnector 인스턴스
     */
    public ISQLiteConnector getSQLiteConnector() {
        return sqliteConnector;
    }

    /**
     * BibleVerseValidator 인스턴스를 가져옵니다.
     * 
     * @return BibleVerseValidator 인스턴스
     */
    public IBibleVerseValidator getBibleVerseValidator() {
        return bibleVerseValidator;
    }

    /**
     * SearchBible 인스턴스를 가져옵니다.
     * 
     * @return SearchBible 인스턴스
     */
    public ISearchBible getSearchBible() {
        return searchBible;
    }

    /**
     * PPTGenerator 인스턴스를 가져옵니다.
     * 
     * @return PPTGenerator 인스턴스
     */
    public IPPTGenerator getPPTGenerator() {
        return pptGenerator;
    }

    /**
     * SettingsManager 인스턴스를 가져옵니다.
     * 
     * @return SettingsManager 인스턴스
     */
    public ISettingsManager getSettingsManager() {
        return settingsManager;
    }
}
