package patterns.command;

import organization.Mappable;
import parsing.customer.Indicator;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Часть паттерна "Команда", классы которого реализуют
 * всю бизнес-логику обработки коллекции.
 * Каждый новенький Receiver, вылезая из пеленок, должен
 * мучиться с коллекцией и уметь в ней почти все:
 * <ol>
 *   <li>Добавлять элементы по ключу и признаку</li>
 *   <li>Удалять элементы по ключу и признаку</li>
 *   <li>Искать элементы по ключу и признаку</li>
 *   <li>Давать информацию об элементах, прошедших проверку</li>
 *   <li>Опустошать коллекцию</li>
 *   <li>Сохранять текущее состояние коллекции</li>
 * </ol>
 * Остальные развращающие методы типа replace легко реализуются посредством
 * уже существующего функционала и создания лишь дополнительной обертки не требует.
 * Хотя ясно, что производительность от этого пострадает, однако автор данной архитектуры
 * очень ценит такие моменты, когда из говна и палок можно собрать тоже говно,
 * но уже с расширенным функционалом.
 * @author Leargy aka Anton Sushkevich
 * @author Come_1LL_F00 aka Lenar Khannanov
 * @param <K> тип используемого в коллекции ключа
 * @param <V> тип элемента коллекции
 */
public interface Receiver<K, V extends Mappable<K>> {
    /**
     * Типичный about me на сайтах,
     * должен выдавать как можно больше
     * читабельной информации о хранимой коллекции
     *
     * @return текстовая информация о коллекции
     */
    String review();

    /**
     * Обший метод для добавления элемента в коллекцию
     *
     * @param key    ключ элемента, на который пишется элемент
     * @param value  записываемый элемент
     * @param menace признак, по которому данный элемент должен добавится
     */
    void add(K[] key, V[] value, Indicator menace);

    /**
     * Общий метод удаления элемента из коллекции
     *
     * @param key    ключ, по которому происходит удаление
     * @param value  удаляемый элемент
     * @param menace признак того, нужно ли удалять данный элемент
     */
    void remove(K[] key, V[] value, Indicator menace);

    /**
     * Общий метод поиска элемента
     *
     * @param key    ключ, по которому ищется элемент
     * @param value  элемент, который мы ищем
     * @param menace признак того, нужен ли нам данный элемент
     */
    void search(K[] key, V[] value, Indicator menace);

    /**
     * Делаем обзор на коллекцию, фильтруя базар
     *
     * @param menace this very фильтр
     * @return текстовая информация об элементах
     */
    String survey(Indicator menace);

    /**
     * Убирать за собой - тоже нужно уметь
     * Возвращает кол-во убранных элементов
     */
    int clear(String userLogin);

    /**
     * Метод получения коллекции полей
     * элемента и ключей, с ним связанных
     */
    <R> Map<K, R> getBy(Function<V, R> keyExtractor);

    void DataRebase(List<V> loaded);

    void save();
}
