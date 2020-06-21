package receiver;

import communication_tools.Component;
import communication_tools.ServerCustomer;
import communication_tools.Valuable;
import communication_tools.wrappers.HandShakeBag;
import communication_tools.wrappers.PassBag;
import czerkaloggers.HawkPDroid;
import czerkaloggers.receiver.S_0D3_GE3;
import systemcore.ServerController;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.nio.channels.*;

/**
 * Та самая девушка на ресепшене,
 * что распределяет гостей, и
 * записывает их в базу
 * @author Come_1LL_F00 aka Lenar Khannanov
 * @author Leargy aka Anton Sushkevich
 */
public final class Hostess extends Receptionist {
  private final HawkPDroid<Hostess> TETRODE; // логгер
  /**
   * Стандартный конструктор,
   * с установкой контроллера над модулем
   * и созданием экземпляра логгера
   * @param core контроллер системы модулей
   */
  public Hostess(ServerController core) {
    super(core);
    TETRODE = (HawkPDroid<Hostess>) S_0D3_GE3.assemble(this, S_0D3_GE3::new);
  }

  /**
   * Реализация метода прослушки,
   * подключенного клиента. Начальные шаги
   * установления прослушки:
   * <ul>
   *   <li>Попытка подключить клиента:</li>
   *   <ul>
   *     <li>Попытка получить клиентский канал (и анал) серверу</li>
   *     <li>Попытка конфигурации клиентского канала</li>
   *     <li>Попытка регистрации клиента</li>
   *   </ul>
   *   <li>Попытка получения входного пакета</li>
   *   <li>Парсинг пакета и создание клиентского аккаунта</li>
   *   <li>Попытка записи информации клиента в список</li>
   * </ul>
   * @param packet входной сетевой пакет
   */
  public void listen(PassBag packet) {
    // достать серверный канал
    ServerSocketChannel server = packet.Channel();
    // достать селектор сервера
    Selector selector = packet.Select();

    // попытка получить сконфигурированный клиентский канал
    SocketChannel connected = signup(selector, server);
    // проверка на валидность канала
    if (connected == null) return;

    // взять у клиента имя и название переменной окружения
    // попытка прочитать по каналу входной пакет
    //HandShakeBag received = receive(connected);
    // проверили, отправили нам что-то
    //if (received == null) return;

    // достаем нужные параметры как имя и название
    // ServerCustomer newbie = parse(received, connected);
    // проверили смогли ли мы создать запись
    // if (newbie == null) return; // TODO: логировать успешную потерю информации о клиенте

    //CUSTOMERS.put(connected, newbie);
    // TODO: логировать успешное добавление клиента
  }

  /**
   * Скрытый метод регистрации и
   * конфигурации клиентского анала;
   * просто принимаем на сервер, конфигурируем
   * в неблокирующий режим и добавляем в
   * селектор.
   * @param elector серверный селектор каналов
   * @param server канал сервера
   * @return сконфигурированный клиентский канал
   */
  private SocketChannel signup(Selector elector, ServerSocketChannel server) {
    // определить канал клиента
    SocketChannel connected = null;
    // попробовать подключить клиента к серверу
    try { connected = server.accept(); } catch (IOException e) {
      TETRODE.logboard(10, "Не удалось приянть клиента на сервер");
      return null;
    }

    //добавил чтобы избежать написание второго try
    if (connected == null) {
      TETRODE.logboard(12, "Потеря связи с клиентом (возможно подключение прервано");
      return null;
    }
    // TODO: правильно обработать прерванное подключение

    // просто оповещение о подключении
    String message = "Установлено соединение с данным клиентом:" +
        " IP: " + connected.socket().getInetAddress().getHostAddress() +
        " PORT: " + connected.socket().getPort();
    TETRODE.logboard(0, message);
    // попытаться сконфигурировать канал в неблокирующий режим
    try { connected.configureBlocking(false); } catch (IOException e) {
      TETRODE.logboard(10, "Ошибка конфигурации клиентского канала");
      return null;
    }
    TETRODE.logboard(0, "Настройка клиентского канала прошла успешно");
    // попытка добавить клиента в выборку
    try { connected.register(elector, SelectionKey.OP_READ);
    } catch (ClosedChannelException e) {
      TETRODE.logboard(0xCCE, "Не удалось зарегистрировать клиента: произошло непредвиденное закрытие канала");
      return null;
    }
    TETRODE.logboard(0, "Регистрация клиента успешно завершена");
    return connected;
  }

  /**
   * Скрытый метод взятия приветственного
   * пакета от клиента с данными о нем,
   * а именно именем и названием его
   * переменной окружения
   * @param client клиентский канал
   * @return полученный пакет
   */
  private HandShakeBag receive(SocketChannel client) {
    BYTE_BUFFER.clear(); // очистка буфера
    // читаем с клиента байты
    try {
      if (client.read(BYTE_BUFFER) == -1)
        throw new EOFException("Достигнут конец потока");
    } catch (EOFException e) {
      // TODO: логировать, то что клиент нас на*бал и ничего не отправил
      return null;
    } catch (IOException e) {
      // TODO: логировать неуспешность приветственной передачи
      return null;
    }
    // TODO: логировать успешность входной передачи
    BYTE_BUFFER.flip(); // Смотри, ща салтуху *бану
    // закидываем, полученные данные с поток байтов
    ByteArrayInputStream bistream = new ByteArrayInputStream(BYTE_BUFFER.array());
    // определяем поток сериализации
    ObjectInputStream objstream = null;
    try {
      objstream = new ObjectInputStream(bistream);
    } catch (IOException e) {
      // TODO: логировать неуспешность создания потока
      return null;
    }
    // TODO: логировать успешность создания потока

    // определяем входной пакет
    HandShakeBag received = null;
    // пытаемся прочитать
    try { received = (HandShakeBag) objstream.readObject(); }
    catch (ClassNotFoundException | ClassCastException e) {
      // TODO: логировать ошибку клиента по отправке несуществующего класса
      return null;
    } catch (IOException e) {
      // TODO: логировать
      return null;
    } finally {
      // в любом случе нужно их закрыть
      try {
        objstream.close();
        bistream.close();
      } catch (IOException e) {
        // TODO: логировать
        System.exit(1);
      }
    }
    // TODO: логировать успешность получения пакета
    return received;
  }

  /**
   * Скрытый метод парсинга (разбора)
   * принятого от клиента пакета
   * @param parcel входная посылка
   * @param client клиентский канал
   * @return аккаунт клиента
   */
  private ServerCustomer parse(HandShakeBag parcel, SocketChannel client) {
    String name = parcel.Name();
    String varName = parcel.$VAR();
    // достаем сокет, чтобы получить всю нужную информацию
    Socket cstemp = client.socket();
    // создаем экземпляр досье на клиента
    // с именем или без
    ServerCustomer newbie = name.isEmpty()?
        new ServerCustomer(cstemp.getInetAddress(), cstemp.getPort(), varName) :
        new ServerCustomer(name, cstemp.getInetAddress(), cstemp.getPort(), varName);
    // TODO: логировать успешное создание аккаунта клиента
    return newbie;
  }

  /**
   * Метод перенаправляющий данные
   * с логирующего элемента на отправляющий
   * модуль, дабы прислать клиенту, какие-то
   * уведомления, о том, что он где-то накосячил
   * @param sender компонента нашей системы
   * @param data данные
   */
  @Override
  public void notify(Component sender, Valuable data) {
    // если какой-то отчет пришел от логгера,
    // то перенаправляем на контроллер,
    // дабы тот отправил клиенту
    if (sender == TETRODE)
      CORE.notify(this, data);
    // по мере увеличения сложности увеличивается
  }
}
