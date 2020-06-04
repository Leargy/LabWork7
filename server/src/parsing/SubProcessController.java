package parsing;

import communication.*;
import communication.wrappers.AlertBag;
import communication.wrappers.ExecuteBag;
import communication.wrappers.QueryBag;
import entities.Organization;
import czerkaloggers.customer.B_4D4_GE3;
import czerkaloggers.HawkPDroid;
import parsing.customer.bootstrapper.NakedCrateLoader;
import parsing.customer.distro.ShedBlock;
import parsing.plants.InstructionBuilder;
import parsing.plants.OrganizationBuilder;
import parsing.supplying.LilyInvoker;
import systemcore.ServerController;
import systemcore.ServerListener;

import java.nio.channels.SocketChannel;
import java.util.List;

/**
 * Контроллер, исполнения запросов клиента.
 * Если весь проект - это ОС, то это подсистема
 * управления процессами.
 * @author Leargy aka Anton Sushkevich
 * @author Come_1LL_F00 aka Lenar Khannanov
 */
public final class SubProcessController extends Resolver {
  // название переменной окружения - общая для всех загрузчиков
  private final static String VAR_NAME = "DBPATH";
  private HawkPDroid<SubProcessController> RADIOMAN; // ссылка на логгер

  /**
   * Конструктор, отвечающий за инициалезацию: логгера, загрузчика коллекции, менеджера коллекции, вызывателя команд,
   * фабрики команд, фабрики элементов коллекции.
   */
  public SubProcessController() {
    serverListener = new ServerListener(this); //the point of conversation with server_console.
    serverListener.start();
    // определяем логгер
    RADIOMAN = (HawkPDroid<SubProcessController>) B_4D4_GE3.assemble(this, B_4D4_GE3::new);
    RADIOMAN.logboard(0, "Успешно собран логгер модуля обработки запросов");
    // определяем загрузчик коллекции
    RADIOMAN.logboard(0, "Успешно создан загрузчик коллекции");
    // определяем ресивер коллекции
    breadLoader = new NakedCrateLoader();
    fate = new ShedBlock(breadLoader, RADIOMAN);
    RADIOMAN.logboard(0, "Успешно создан менеджер коллекции");
    // определяем вызывателя комманд
    kael = new LilyInvoker(this);
    RADIOMAN.logboard(0, "Успешно создан вызыватель комманд");
    // определяем фабрику элементов коллекции
    plant = new OrganizationBuilder(this);
    RADIOMAN.logboard(0, "Успешно создана фабрика элементов коллекции");
    // определяем преобразователя комманд
    wizard = new InstructionBuilder(this, plant);
    RADIOMAN.logboard(0, "Успешно создана фабрика вызываемых комманд");
  }
  /**
   * Конструктор, инициализирующий
   * подсистему полностью. Она состоит из
   * <ul>
   *   <li>Контроллера подсистемы</li>
   *   <li>Сутенера комманд или Invoker'a</li>
   *   <li>Сутенера коллекции или Receiver'a</li>
   *   <li>Фабрики (мороженого) комманд</li>
   *   <li>Шоколадной фабрики</li>
   *   <li>Логирующий элемент</li>
   * </ul>
   * @param m контроллер подсистемы
   */
  public SubProcessController setSubProcessController(ServerController m, SocketChannel client) {
    super.CONTROLLER = m;

    this.client = client;
    return this;
  }

  /**
   * Единственный метод, отправляющий
   * команды на исполнение и пробрасывающий
   * результат их исполнения дальше по цепи.
   * @param query присланные данные о клиенте и команде на исполнение
   */
  @Override
  public void parse(QueryBag query) {
    RADIOMAN.logboard(0, "Начинаем обработку запроса");
    // достаем название переменной окружения
    // String var_name = query.Customer().$VAR();
    List<Organization> loaded = null;
    // проверили была ли загружена коллекция
  if (!breadLoader.Loaded()) {
      RADIOMAN.logboard(0, "В первый раз? Загружаем Вашу коллекцию");
      // установили переменную окружения
      breadLoader.Environment(VAR_NAME);
      RADIOMAN.logboard(0, "Строим окружение");
      // загрузили коллекцию
      loaded = breadLoader.load();

      RADIOMAN.logboard(0, "Коллекция загружена");
      // изменили состояние пустой коллекции на полную
      fate.DataRebase(loaded);
      RADIOMAN.logboard(0, "Менеджер поставлен на загруженную коллекцию");
    }
    // взяли клиентский запрос
    // отправляем фабрике, чтобы
    // построить уже исполняемую команду
    RADIOMAN.logboard(0, "Отправляем запрос на настройку");
    notify(this, query);
    // а затем исполнить
  }

  /**
   * Основной метод перессылки данных между
   * компонентами модуля разбора запросов.
   * @param sender отправитель
   * @param data отправляемые данные
   */
  @Override
  public void notify(Component sender, Valuable data) {
    // если отправляет логирующий элемент,
    // то отправляем клиенту
    if (sender == RADIOMAN ) {
      CONTROLLER.notify(this, (AlertBag) data); // отправили господину распорядителю
    } else if (sender == this || sender == serverListener) {
      QueryBag bag = (QueryBag) data;
      RADIOMAN.logboard(0, "Начинаем конфигурировать запрос");
      wizard.make(bag, fate); // отправили на фабрику (мороженого шутка) комманд
    } else if (sender == wizard) {
      ExecuteBag bag = (ExecuteBag) data;
      //kael.signup(bag.Exec());
      RADIOMAN.logboard(0, "Пытаемся вызвать команду: " + bag.Exec().getClass().getSimpleName());
      kael.invoke((ExecuteBag) data); // отправили Invoker'у, чтобы исполнить
    } else if (sender == kael) {
      RADIOMAN.logboard(0, "Отправляем результаты клиенту");
      CONTROLLER.notify(this, (AlertBag) data);
    }
    // разрастается по мере увеличения числа компонент модуля
  }
}
