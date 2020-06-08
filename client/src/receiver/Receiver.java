package receiver;

import communication.ClientPackage;
import communication.Report;
import dataSection.enumSection.Markers;
import communication.Mediating;
import communication.Segment;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;

/**
 * Класс получатель, в метод принимает посылку от сервера и десериализует в пригодный для обработки вид.
 * @author Come_1LL_F00 aka Lenar Khannanov
 * @author Leargy aka Anton Sushkevich
 * @see AReceiver
 */
public class Receiver extends AReceiver{
    private ByteArrayInputStream byteArrayInputStream;
    private ObjectInputStream objectInputStream;
    private final ByteBuffer byteBuffer = ByteBuffer.allocate(3*1024);

    public Receiver(Mediating mediator){
        super(mediator);
    }

    /**
     * метод receive() - метод класса родителя, ответственене за десериализацию.
     * @param parcel
     */
    @Override
    public void receive(Segment parcel) {
        byteBuffer.clear();
        try {
            if(parcel.getSocketChannel().read(byteBuffer) == -1) {
                throw new EOFException();
            }
            byteBuffer.flip();
            byteArrayInputStream = new ByteArrayInputStream(byteBuffer.array());
            objectInputStream = new ObjectInputStream(byteArrayInputStream);
            Report query = (Report) objectInputStream.readObject();
            parcel.setClientPackage(new ClientPackage(null,query));
            // Block of checking if user's login and pass was confirmed.
            if (query.getIsConfirmed()) {
                parcel.setMarker(Markers.CONFIRMING);
                mediator.notify(this, parcel);
            }
            parcel.setMarker(Markers.WRITE);
            mediator.notify(this, parcel);
        }catch (IOException ex) {
            parcel.setMarker(Markers.INTERRUPTED);
            System.err.println("────>Connection interrupted< <─w─");
            mediator.notify(this, parcel);
        }catch (ClassNotFoundException ex) {
            System.out.println(ex.getException());
            //TODO:write an handling to this type of error
        }
    }
}
