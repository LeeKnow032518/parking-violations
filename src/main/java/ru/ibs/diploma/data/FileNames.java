package ru.ibs.diploma.data;

import org.springframework.stereotype.Component;

/**
 * Компонент Spring, содержащий имена файлов, используемых в приложении для загрузки данных.
 *
 * <p><strong>Используемые файлы:</strong></p>
 * <ul>
 *   <li><b>parkingType</b> — тип файла с описанием типов парковок</li>
 *   <li><b>logFile</b> — файл для записи логов обработки данных</li>
 *   <li><b>populationFile</b> — файл с данными о численности населения</li>
 *   <li><b>parkingFile</b> — файл с информацией о парковочных нарушениях</li>
 *   <li><b>propertiesFile</b> — файл с данными о недвижимости</li>
 * </ul>
 *
 */
@Component
public class FileNames {
    /** Поле, содержащее расширение файла с данными о парковочных нарушениях.*/
    private String parkingType;
    /** Название файла для логгирования действий пользователя и запуска приложения.*/
    private String logFile;
    /** Название файла с данными о населении.*/
    private String populationFile;
    /** Название файла с данными о парковочных нарушениях.*/
    private String parkingFile;
    /** Название файла с данными о недвижимости.*/
    private String propertiesFile;

    /**
     * Возвращает расширение файла с данными о типах парковок.
     *
     * @return строка с расширением файла;
     */
    public String getParkingType() {
        return parkingType;
    }

    /**
     * Устанавливает расширение файла с данными о парковочных нарушениях.
     *
     * @param parkingType расширение файла
     */
    public void setParkingType(String parkingType) {
        this.parkingType = parkingType;
    }

    /**
     * Возвращает имя или путь к лог-файлу приложения.
     *
     * @return строка с путём к файлу журнала; может быть {@code null}
     */
    public String getLogFile() {
        return logFile;
    }

    /**
     * Устанавливает имя или путь к лог-файлу.
     *
     * @param logFile путь к файлу для логирования
     */
    public void setLogFile(String logFile) {
        this.logFile = logFile;
    }

    /**
     * Возвращает имя или путь к файлу с данными о населении.
     *
     * @return строка с путём к файлу населения; может быть {@code null}
     */
    public String getPopulationFile() {
        return populationFile;
    }

    /**
     * Устанавливает имя или путь к файлу с данными о населении.
     *
     * @param populationFile путь к файлу с информацией о населении
     */
    public void setPopulationFile(String populationFile) {
        this.populationFile = populationFile;
    }

    /**
     * Возвращает имя или путь к файлу с информацией о парковках.
     *
     * @return строка с путём к файлу парковок; может быть {@code null}
     */
    public String getParkingFile() {
        return parkingFile;
    }

    /**
     * Устанавливает имя или путь к файлу с информацией о парковках.
     *
     * @param parkingFile путь к файлу с данными о парковочных местах
     */
    public void setParkingFile(String parkingFile) {
        this.parkingFile = parkingFile;
    }

    /**
     * Возвращает имя или путь к файлу с данными о недвижимости.
     *
     * @return строка с путём к основному файлу свойств; может быть {@code null}
     */
    public String getPropertiesFile() {
        return propertiesFile;
    }

    /**
     * Устанавливает имя или путь к файлу с данными о недвижимости.
     *
     * @param propertiesFile путь к файлу с данными о объектах недвижимости
     */
    public void setPropertiesFile(String propertiesFile) {
        this.propertiesFile = propertiesFile;
    }
}
