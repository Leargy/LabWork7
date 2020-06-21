package instructions.concrete.extended;

import communication_tools.Report;
import entities.Organization;
import entities.comparators.OrganizationTitleComparator;
import parsing.customer.Indicator;
import parsing.customer.Receiver;

import java.util.Map;

public final class RemoveLower extends RemoveThan {
  protected final Indicator MENACE;
  /**
   * Конструктор, устанавливающий ссылку на
   * управленца коллекцией, а также объект
   * добавляемого элемента
   *
   * @param sieve текущий управленец коллекцией
   * @param added добавляемый элемент
   */
  public RemoveLower(Receiver sieve, Organization added) {
    super(sieve, added);
    OrganizationTitleComparator cmp = new OrganizationTitleComparator();
    MENACE = (org)->(cmp.compare((Organization) org, EMBEDDED) < 0);
  }

  /**
   * Метод исполнения, для этих команд общее
   * @return отчет выполнения
   */
  @Override
  public final Report execute() {
        int[] deletedNumber = new int[]{0};
        Receiver<Integer, Organization> REAL = (Receiver<Integer, Organization>) SIEVE;
        StringBuilder result = new StringBuilder();
        Map<Integer, Integer> keys = REAL.getBy(Organization::Key);
        keys
            .entrySet()
            .stream()
            .forEach((Map.Entry<Integer, Integer> e)-> {
              Organization taken = null;
              Organization[] takens = new Organization[]{taken};
              REAL.search(new Integer[]{e.getKey()}, takens, (org)->(true));
              OrganizationTitleComparator cmp = new OrganizationTitleComparator();
              REAL.remove(new Integer[]{e.getKey()}, new Organization[]{takens[0]}, MENACE);
                  if (MENACE.verify(takens[0])) {
                      result.append("Элемент " + takens[0] + " по ключу " + e.getKey() + " удален\n");
                      deletedNumber[0]++;
                  }
            });
        result.append("Элементов удалено: " + deletedNumber[0]);
        return new Report(0, result.toString());
  }

  public static final String NAME = "remove_lower";
  public static final String BRIEF = "удаляет из коллекции элементы, меньшие чем заданный";
  public static final String SYNTAX = NAME + " {element}";
  public static final int ARGNUM = 1;

    @Override
    public String toString() { return NAME; }
}
