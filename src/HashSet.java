import java.util.*;

/**
 * Это класс-обертка для типов, которые
 * хранятся в хэш-сете.
 */
class Entry<V> {
    final V value;

    Entry(V value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this || value.equals(obj);
    }
}

/**
 * Это класс-синглтон (паттерн Singleton).
 * Класс, который может иметь один единственный
 * экземпляр. Он является меткой для
 * удаленных элементов.
 */
final class DeletedEntry extends Entry {
    /**
     * Поле статическое, значит оно ОДНО на класс.
     */
    private static DeletedEntry value;

    /**
     * Конструктор приватный, так как нам не нужно, чтобы
     * кто-то мог создавать лишние объекты.
     * Здесь super значит вызов конструктора класса Entry, от которого
     * наследуется данный класс, то есть конструктор класса Entry.
     */
    private DeletedEntry() {
        super(null);
    }

    /**
     * Метод, после первого вызова которого, создается экземпляр
     * данного класса, если метод уже вызывался единожды, то будет
     * возвращен экземпляр, созданный при первом вызове.
     */
    public static Entry getInstance() {
        if (value == null) value = new DeletedEntry();
        return value;
    }

    /**
     * Нам необходимо переопределить метод equals, потому что
     * наш класс не должен быть равен ничему, кроме самого себя.
     * Поэтому возвращаем true, только если ссылки совпадают
     */
    @Override
    public boolean equals(Object obj) {
        return obj == this;
    }
}

public class HashSet<T> implements Set<T> {
    /**
     * Максимально допустимая заполненность массива
     */
    private static final double MAX_FULLNESS = 0.7;
    /**
     * Выбран размер, равный степени 2, чтобы можно было
     * увеличивать размер массива до Integer.MAX_VALUE - 1
     */
    private static final int INITIAL_SIZE = 128;
    private Entry<T>[] array;
    private int realSize;
    /**
     * Экземпляр маркера "удален". Инициализируется в конструкторе.
     */
    private Entry deleted;
    private int arraySize = INITIAL_SIZE;
    private boolean reachedMaxSize;

    @SuppressWarnings("unchecked")
    HashSet() {
        array = (Entry<T>[]) new Entry[INITIAL_SIZE];
        deleted = DeletedEntry.getInstance();
    }

    @Override
    public int size() {
        return realSize;
    }

    @Override
    public boolean isEmpty() {
        return realSize == 0;
    }

    @Override
    public boolean contains(Object o) {
        if (o == null) return false;
        int index = hash(o);
        for (int i = 0; i < array.length; i++) {
            int newIndex = (index + i) % array.length;
            if (array[newIndex] == null) return false;
            if (o.equals(array[newIndex].value)) return true;
        }
        return false;
    }

    @Override
    public Iterator<T> iterator() {
        return new HashIterator();
    }

    private final class HashIterator implements Iterator<T> {
        T current;
        int size = realSize;
        int from = 0;

        @Override
        public boolean hasNext() {
            return size > 0;
        }

        @Override
        @SuppressWarnings("unchecked")
        public T next() {
            if (!hasNext()) throw new NoSuchElementException();
            Entry<T> it = null;
            size--;
            for (int i = from; i < array.length; i++) {
                if ((it = array[i]) != null && it != deleted) {
                    from = i + 1;
                    break;
                }
            }
            current = it.value;
            return current;
        }

        @Override
        public void remove() {
            HashSet.this.remove(current);
        }
    }

    @Override
    public Object[] toArray() {
        Object[] newArray = new Object[realSize];
        Iterator<T> iterator = iterator();
        for (int i = 0; i < realSize; i++) {
            newArray[i] = iterator.next();
        }
        return newArray;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T1> T1[] toArray(T1[] a) {
        return null;
    }

    /**
     * В самом начале проверяем достиг ли наш массив максимальной заполненности
     * Если да, то пытаемся увеличить массив. Если это удается, то идем дальше, если нет
     * возвращаем false. Далее проверяем имеется ли у нас уже такой элемент, если
     * да, то не добавляем, если нет, то добавляем.
     * Если не нашли место куда добавить (whereToAdd == -1), чего
     * быть просто не может, то возращаем false.
     */
    @Override
    public boolean add(T t) {
        if (t == null) return false;
        if ((double) realSize / array.length > MAX_FULLNESS) {
            if (!resize()) return false;
        }
        if (contains(t)) return false;
        int whereAdd = whereToAdd(t);
        if (whereAdd == -1) return false;
        array[whereAdd] = new Entry<>(t);
        realSize++;
        return true;
    }

    /**
     * В самом начале проверяем, достиг ли наш массив максимально возможного размера
     * Если нет, то увеличиваем размер в 2 раза. Если после увеличения, размер стал равен
     * Integer.MIN_VALUE именно минимальное значение, так как Integer.MAX_VALUE + 1, которого
     * достигнет размер массива, при последнем увеличении, переполяет int и уходит в его
     * минимальное значение, мы должны уменьшить это число на единицу, что даст нам
     * Integer.MAX_VALUE (строчка newSize -= 1)
     * При достижении максимального размера указываем, что дальше расти некуда
     * (записываем true в reachedMaxSize)
     * Если увеличение произошло, то нужно распределить элементы в массиве с новым размером,
     * поэтому обновлем размер (arraySize) и выполняем rehash()
     * [-2147483648, 2147483647]
     */
    private boolean resize() {
        if (reachedMaxSize) return false;
        int newSize = array.length * 2;
        if (newSize == Integer.MIN_VALUE) {
            reachedMaxSize = true;
            newSize -= 1;
        }
        if (newSize < 0) return false;
        arraySize = newSize;
        rehash();
        return true;
    }

    /**
     * Здесь заново считаем хэш-функцию, чтобы распределить элементы с учетом
     * нового размера. Идет следующий порядок вызовов -
     * rehash() -> copyFromTo() -> whereToAdd() -> hash()
     * Хэш-функция в самом конце этого списка.
     */
    @SuppressWarnings("unchecked")
    private void rehash() {
        Entry<T>[] oldArray = array;
        Entry<T>[] newArray = (Entry<T>[]) new Entry[arraySize];
        array = newArray;
        copyFromTo(oldArray, newArray);
    }

    /**
     * Копируем элементы из старого массива (from) в новый (to),
     * размер которой в 2 раза больше
     */
    private void copyFromTo(Entry<T>[] from, Entry<T>[] to) {
        for (Entry<T> o : from) {
            if (o != null) {
                int index = whereToAdd(o);
                if (index == -1) {
                    throw new Error("It's wasn't supposed to happen!");
                }
                to[index] = o;
            }
        }
    }

    /**
     * Простая хэш-функция, которая берет модуль от хэш-кода по размеру массива
     */
    private int hash(Object o) {
        int hash = Math.abs(o.hashCode());
        return hash % arraySize;
    }

    /**
     * Ищем куда можно добавить элемент. Определяем начальный, куда необходимо
     * попробывать добавить (hash). Затем, если в ячейке null или deleted (Singleton),
     * то возращаем этот индекс. Если ячейка занята, то просто пытаемся добавиь в следующую.
     * Идем так по кругу, пока не найдем свободную ячейку.
     *
     * Ход по кругу обеспечивается этой строчкой (index + i) % arraySize
     * То есть когда сумма index + i даст нам значение больше arraySize, то будем
     * взят остаток от делания на arraySize, что кинет нас в начало массива.
     *
     * Необходимо учесть что при больших размерах массива index + i может уйти в
     * отрицательные значения из-за переполнения int. Поэтому мы завели переменную
     * sum, которую проверяем на отрицательность.
     *
     * Пример. Допустим int определен в таком диапазоне от -8  до 7, то есть
     * Integer.MAX_VALUE = 7. Наш массив достиг этого максимального значение и получилась
     * такая ситуация index + i = 7 + 5 = -4 из переполнения.
     * Нам нужно чтобы в таком случае счет шел с 0, в нашем случае переполнение
     * произошло на 5 единиц, с учетом того, что счет идет с нуля, мы должны попасть
     * в 4. Для этого мы просто вычтем из полученного значения минимальное, то есть
     * -4 - (-8) = -4+8, что даст нам 4
     */
    private int whereToAdd(Object o) {
        int index = hash(o);
        for (int i = 0; i < arraySize; i++) {
            int sum = (index + i);
            if (sum < 0) sum -= Integer.MIN_VALUE;
            int newIndex = sum % arraySize;
            if (array[newIndex] == null || array[newIndex] == deleted) return newIndex;
        }
        return -1;
    }

    @Override
    public boolean remove(Object o) {
        if (o == null) return false;
        int hash = hash(o);
        for (int i = 0; i < arraySize; i++) {
            int newIndex = (hash + i) % arraySize;
            if (array[newIndex] == null) return false;
            else if (array[newIndex].equals(o)) {
                array[newIndex] = deleted;
                realSize--;
                break;
            }
        }
        return true;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c) {
            if (!contains(o)) return false;
        }
        return true;
    }

    /**
     * true - если массив вообще изменился, даже если добавился
     * только один элемент из всех.
     * Objects.requireNonNull(c) - возращает объект, если он не null
     * Если null, то выкидывает NPE.
     */
    @Override
    public boolean addAll(Collection<? extends T> c) {
        Objects.requireNonNull(c);
        boolean result = false;
        for (T t : c) {
            if (add(t)) result = true;
        }
        return result;
    }

    /**
     * Логическое "И". Оставляем то, что находится в
     * нашем массиве и в том, который получен в качестве аргумента.
     * true - если хоть что-то изменилось.
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean retainAll(Collection<?> c) {
        Objects.requireNonNull(c);
        boolean modified = false;
        Iterator<T> iterator = iterator();
        while (iterator.hasNext()) {
            if (!c.contains(iterator.next())) {
                iterator.remove();
                modified = true;
            }
        }
        return modified;
    }

    /**
     * true - если массив вообще изменился, даже если удалился
     * только один элемент из всех.
     */
    @Override
    public boolean removeAll(Collection<?> c) {
        Objects.requireNonNull(c);
        boolean result = false;
        for (Object t : c) {
            if (remove(t)) result = true;
        }
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void clear() {
        realSize = 0;
        arraySize = INITIAL_SIZE;
        array = (Entry<T>[]) new Entry[INITIAL_SIZE];
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof HashSet) {
            if (((HashSet) obj).size() != size()) return false;
            for (Object o : ((HashSet) obj)) {
                if (!contains(o)) return false;
            }
            return true;
        } else return false;
    }

    @Override
    public int hashCode() {
        int h = 0;
        for (T obj : this) {
            if (obj != null && obj != deleted) h += obj.hashCode();
        }
        return h;
    }
}
