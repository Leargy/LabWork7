package receiver;

import communication_tools.Component;
import communication_tools.Mediating;
import communication_tools.Segment;

import java.io.IOException;

/**
 * Абстрактный класс модуля получения сообщений от сервера.
 * @author Come_1LL_F00 aka Lenar Khannanov
 * @author Leargy aka Anton Sushkevich
 */
public abstract class AReceiver implements Component {
    protected Mediating mediator;

    /**
     * Конструктор принимающий ссылку на посредника.
     * @param mediator
     */
    public AReceiver(Mediating mediator) {
        this.mediator = mediator;
    }

    /**
     * метод, ответственене за десериализацию.
     * @param parcel
     * @throws IOException
     */
    public void receive(Segment parcel) { }
}
