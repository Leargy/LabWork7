package perusal;

import communication_tools.Component;
import communication_tools.Mediator;
import communication_tools.wrappers.TunnelBag;
import systemcore.ServerController;

import java.nio.channels.SocketChannel;

/**
 * Шаблон модуля чтения запросов от клиента,
 * преобразующий полученные байты во внятный клиентский запрос.
 * @author Come_1LL_F00 aka Lenar Khannanov
 * @author Leargy aka Anton Sushkevich
 */
public abstract class QueryReader implements Component, Mediator {
  protected SocketChannel client;
  protected final ServerController KAPELLMEISTER; // контроллер модуля

  /**
   * Этот метод, как плазменный резак --
   * вычленяет нужное из клиента,
   * а именно его запрос и передает
   * его следующему модулю
   * @param parcel копромат на клиента
   */
  public abstract void retrieve(TunnelBag parcel);

  // главный конструктор модуля
  /**
   * Конструктор по стандарту,
   * в котором устанавливается
   * хозяин модуля
   * @param core ядро системы, т.е. контроллер сервера
   */
  public QueryReader(ServerController core) { KAPELLMEISTER = core; }

  public SocketChannel ClientChannel() { return client; }
  public void SetClientChannel(SocketChannel client) { this.client = client; }
}
