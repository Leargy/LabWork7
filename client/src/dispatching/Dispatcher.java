package dispatching;

import dataSection.Commands;
import dataSection.enumSection.Markers;
import communication.Mediating;
import communication.Segment;
import dataSection.CommandList;
import exceptions.CommandSyntaxException;
import dispatching.validators.ArgumentHandler;
import dispatching.validators.CommandHandler;
import dispatching.validators.Handler;
import instructions.rotten.RawDecree;
import instructions.rotten.base.RawExit;

import java.io.*;
import java.nio.ByteBuffer;

/**
 * Модуль отправки пакета данных на сервер.
 * @author Come_1LL_F00 aka Lenar Khannanov
 * @author Leargy aka Anton Sushkevich
 */
public class Dispatcher extends ADispatcher {
    private final Handler dataHandler;
    private Mediating mediator;
    private Commands commandList = new CommandList();
    private ByteArrayOutputStream byteArrayOutputStream ;
    private ObjectOutputStream objectOutputStream;

    /**
     * конструктор, пренимающий ссылку на посредника.
     * @param mediator посредник
     */
    public Dispatcher(Mediating mediator){
        this.mediator = mediator;
        //Initialising the handling chain.
        CommandHandler commandHandler = new CommandHandler(commandList);
        ArgumentHandler argumentHandler = new ArgumentHandler(commandList);
        commandHandler.setNext(argumentHandler);

        dataHandler = commandHandler;
    }

    /**
     * метод в котором полученное сообщение от клиента отправляется на проверку
     * в случии валидности возвращается объект сырой команды,
     * заполненный необходимый для ее иполнения информацией.
     * @param parcel объект необходимый для пересылки информации между модулями.
     * @throws IOException
     */
    public void giveOrder(Segment parcel) {
        RawDecree tempCommand = null;
        try {
            tempCommand = dataHandler.handle(parcel);
        }catch(CommandSyntaxException e) {
            //exception will be thrown if entered command doesn't pass the verification.
            e.getMessage();
            System.out.println("For more information use \"help\" command.");
            return;
        }
        if(tempCommand instanceof RawExit) {
            try {
                byteArrayOutputStream.close();
                objectOutputStream.close();
            }catch (IOException ex) {
                new IOException("Dropped an exception during closing streams.", ex);
            }catch (NullPointerException ex) {/*NOP*/}
            parcel.setMarker(Markers.STOP);
            mediator.notify(this,parcel);
        }
        parcel.setCommandData(tempCommand);
        send(parcel);
    }

    /**
     * Метод производящий сериализацию объекта и последующую его отправку.
     * @param parcel
     * @throws IOException
     */
    public void send(Segment parcel) {
        byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(parcel.prepareDataObject());
        }catch (IOException e) {/*NOP*/}
        try {
            parcel.getSocketChannel().write(ByteBuffer.wrap(byteArrayOutputStream.toByteArray()));
        }catch (IOException e) {
            System.err.println("─────Server connection is interrupted─────");
            parcel.setMarker(Markers.INTERRUPTED);
            mediator.notify(this,parcel);
        }finally {
            try {
                byteArrayOutputStream.close();
                objectOutputStream.close();
            }catch (IOException e) { /*NOP*/};
        }
        // TODO: Обработка разрыва подключения
    }
}

class PassCheck {
    private boolean isConfirmed;
    private String login;
    private String password;

    public void setIsConfirmed(boolean confirmed) {
        isConfirmed = confirmed;
    }
    public boolean isConfirmed() {
        return isConfirmed;
    }
    public String getLogin() {
        return login;
    }
    public String getPassword() {
        return password;
    }
}