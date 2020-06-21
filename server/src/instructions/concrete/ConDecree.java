package instructions.concrete;

import communication_tools.Valuable;
import parsing.customer.Receiver;
import instructions.Command;
import instructions.Decree;

/**
 * Абстракция всех комманд, использующихся на сервере.
 * Помимо информации о себе, должны ОБЯЗАТЕЛЬНО иметь ссылку на
 * {@link parsing.customer.Receiver}, в котором они исполняют комманды
 * @author Come_1LL_F00 aka Lenar Khannanov
 * @author Leargy aka Anton Sushkevich
 * @see Decree
 * @see Command
 */
public abstract class ConDecree extends Decree implements Command, Valuable {
  // ссылка на текущий контроллер коллекции
  protected final Receiver SIEVE;

  /**
   * Конструктор, устанавливающий ссылку на
   * управленца коллекцией
   * @param sieve текущий управленец коллекцией
   */
  public ConDecree(Receiver sieve) { SIEVE = sieve; }
}
