package ru.ibs.diploma.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import ru.ibs.diploma.cache.Answer;
import ru.ibs.diploma.data.Field;
import ru.ibs.diploma.logging.WriteLogService;
import ru.ibs.diploma.service.AnalyseService;

import java.io.IOException;
import java.util.Scanner;

/**
 * Консольный UI-компонент, реализующий интерактивное меню для работы
 * с системой анализа данных о парковочных штрафах и недвижимости.
 *
 * <p>После инициализации выводит приветственное сообщение и бесконечно
 * опрашивает пользователя о желаемом действии. Каждый выбор
 * логируется и передаётся на выполнение в {@link AnalyseService}.
 *
 * <p>Доступные опции:
 * <ul>
 *   <li>0 – выход из приложения</li>
 *   <li>1 – общее население</li>
 *   <li>2 – штрафы на душу населения</li>
 *   <li>3 – средняя рыночная стоимость по ZIP-коду</li>
 *   <li>4 – средняя жилая площадь по ZIP-коду</li>
 *   <li>5 – рыночная стоимость на душу населения по ZIP-коду</li>
 *   <li>6 – «сюрприз» (статистика штрафов, отсортированная по рыночной стоимости)</li>
 * </ul>
 *
 */
@Component
public class UserChoice {

    @Autowired
    private ApplicationContext context;
    /**
     * Сервис для записи пользовательских действий в лог.
     */
    @Autowired
    private WriteLogService writeLogService;

    /**
     * Сервис, содержащий бизнес-логику расчётов и агрегации данных.
     */
    @Autowired
    private AnalyseService analyseService;

    /**
     * Выводит в консоль приветственное сообщение и запускает цикл
     * обработки пользовательского ввода.
     *
     * <p>Метод блокирует текущий поток, опрашивая {@link Scanner}
     * до тех пор, пока пользователь не выберет пункт «0 – Exit».
     * Каждый ввод фиксируется в логе и передаётся в
     * {@link #handleChoice(String)}.
     */
    public void createMessage(){
        System.out.println("Welcome to our parking analysis app. Please, choose the action: ");
        while(true){
            System.out.println("0 - Exit the app");
            System.out.println("1 - Print Total Population");
            System.out.println("2 - Print Total Parking Fines per Capita");
            System.out.println("3 - Print Average Market Value");
            System.out.println("4 - Print Average Total Livable Area");
            System.out.println("5 - Print Total Market Value per Capita");
            System.out.println("6 - Surprise action");

            Scanner scanner = new Scanner(System.in);
            String answer = scanner.nextLine();

            writeLogService.logChoice(answer);

            handleChoice(answer);
        }
    }

    /**
     * Выполняет выбранное пользователем действие.
     *
     * <p>В зависимости от значения {@code action}:
     * <ul>
     *   <li>завершает приложение;</li>
     *   <li>обращается к соответствующему методу {@link AnalyseService}
     *       и выводит результат через {@link Answer#printAnswer()};</li>
     *   <li>запрашивает дополнительные данные (ZIP-код) при необходимости;</li>
     *   <li>обрабатывает ошибки ввода-вывода, выводя сообщение пользователю.</li>
     * </ul>
     *
     * @param action строка-код выбранного пункта меню
     */
    public void handleChoice(String action){
        switch (action) {
            case "0":
                System.out.println("Press any key to exit...");
                Scanner scanner = new Scanner(System.in);
                scanner.nextLine();
                System.exit(SpringApplication.exit(context, () -> 0));
                break;
            case "1":
                try {
                    Answer result1 = analyseService.totalPopulation();
                    result1.printAnswer();
                }catch (IOException ie){
                    System.out.println("The problem occurred: " + ie.getMessage());
                }
                break;
            case "2":
                try {
                    Answer result2 = analyseService.totalParkingFinesPerCapita();
                    result2.printAnswer();
                }catch (IOException ie){
                    System.out.println("The problem occurred: " + ie.getMessage());
                }
                break;
            case "3":
                System.out.println("Enter ZIP-code, please: ");
                Scanner scanner1 = new Scanner(System.in);
                String code3 = scanner1.nextLine();
                writeLogService.logChoice(code3);
                try {
                    Answer result3 = analyseService.averageProperties(code3, Field.MARKET_VALUE);
                    result3.printAnswer();
                }catch(IOException e){
                    System.out.println("The problem occurred: " + e.getMessage());
                }
                break;
            case "4":
                System.out.println("Enter ZIP-code, please: ");
                Scanner scanner4 = new Scanner(System.in);
                String code4 = scanner4.nextLine();
                writeLogService.logChoice(code4);
                try {
                    Answer result4 = analyseService.averageProperties(code4, Field.LIVABLE_AREA);
                    result4.printAnswer();
                }catch(IOException e){
                    System.out.println("The problem occurred: " + e.getMessage());
                }
                break;
            case "5":
                System.out.println("Enter ZIP-code, please: ");
                Scanner scanner5 = new Scanner(System.in);
                String code5 = scanner5.nextLine();
                writeLogService.logChoice(code5);
                try {
                    Answer result5 = analyseService.totalMarketValuePerCapita(code5);
                    result5.printAnswer();
                }catch(IOException e){
                    System.out.println("The problem occurred: " + e.getMessage());
                }
                break;
            case "6":
                System.out.println("Here is the statistics of amount of fines per person sorted by the average market value:");
                System.out.println("Press any key to see the result: ");
                Scanner scanner6 = new Scanner(System.in);
                scanner6.nextLine();
                try{
                    Answer result6 = analyseService.surpriseOption();
                    result6.printAnswer();
                    break;
                }catch (IOException ie){
                    System.out.println("The problem occurred: " + ie.getMessage());
                }
            default:
                System.out.println("Unknown answer, try choosing action once again:\n");
                break;
        }

    }
}
