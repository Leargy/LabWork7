package parsing;


import communication.Component;
import communication.Mediator;
import communication.wrappers.AlertBag;
import communication.wrappers.QueryBag;
import entities.Organization;
import parsing.customer.bootstrapper.LoaferLoader;
import parsing.customer.distro.LimboKeeper;
import parsing.plants.Factory;
import parsing.plants.InstructionBuilder;
import systemcore.ServerController;

import java.nio.channels.SocketChannel;

/**
 * Шаблон класса модуля, вытаскивающего пользовательский запрос
 * и делегирующий свою работу по обработке готовым классам.
 *  @author Come_1LL_F00 aka Lenar Khannanov
 *  @author Leargy aka Anton Sushkevich
 */
public abstract class Resolver implements Mediator, Component {
  protected ServerController CONTROLLER; // контроллер модуля
  protected FondleEmulator kael; // сутенер комманд
  protected LimboKeeper fate; // сутенер коллекции
  protected InstructionBuilder wizard; // фабрика вызываемых комманд
  protected Factory plant; // фабрика элементов коллекции
  protected LoaferLoader<Organization> breadLoader; // загрузчик коллекции
  protected SocketChannel client;
  protected Thread serverListener;

  // основной метод обработки клиентского запроса
  /**
   * Основной метод обработки запросов
   * клиента. Клиентский пакет содержит
   * данные о клиенте (имя, адрес, порт, НАЗВАНИЕ
   * ПЕРЕМЕННОЙ ОКРУЖЕНИЯ), канал клиента (дабы
   * послать модулю отправки ответов)
   * @param query пакет с клиентским запросом
   */
  public abstract void parse(QueryBag query);

  // конструктор по умолчанию
  // в терминах лабы
  public Resolver() {}
  /**
   * Конструктор, устанавливающий хозяина
   * @param controller контроллер подсистемы
   */
  public Resolver(ServerController controller) { CONTROLLER = controller; }

  public SocketChannel ClientChannel() { return client; }

  public void ImmediateStop(AlertBag alert) {
    AlertBag clientAlert = new AlertBag(client, alert.Notify());
    CONTROLLER.ImmediateStop(clientAlert);
  }

  /**
   * Геттер для получения
   * фабрики иструкций, нужен шелу
   * для построения инструкций
   * Бооооооооооооооооб, строитель --
   * все построит,
   * Бооооооооооооооооб, строитель --
   * без проблем
   * @return ссылка на (Боба) строителя инструкций
   */
  public InstructionBuilder getInstBuilder() { return wizard; }

  /**
   * Обычный геттер для получения ресивера,
   * нужен фабрике инструкций и шелу,
   * который всеми этими геттерами и пользуется
   * @return ссылка на ресивер
   */
  public LimboKeeper getFate() { return fate; }
}
