import org.junit.After;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class HashSetTest {
    private HashSet<String> hashSet = new HashSet<>();
    private Random random = new Random(1720557871);

    @After
    public void after() {
        hashSet.clear();
    }

    @Test
    public void addTest() {
        assertTrue(hashSet.add("Привет"));
        assertTrue(hashSet.contains("Привет"));
        assertTrue(hashSet.add("Я"));
        assertTrue(hashSet.contains("Я"));
        assertTrue(hashSet.add("Добавился"));
        assertFalse(hashSet.add("Добавился"));
        assertFalse(hashSet.add("Привет"));
        assertTrue(hashSet.size() == 3);
    }

    @Test
    public void bigAddTest() {
        /**
         * Добавляем много элементов и выводим размер
         * чтобы увидеть, что set растет
         */
        for (int i = 0; i < Integer.MAX_VALUE / 1000; i++) {
            hashSet.add(String.valueOf(random.nextLong()));
        }
        System.out.println(hashSet.size());
    }

    @Test
    public void addAllTest() {
        assertTrue(hashSet.addAll(Arrays.asList("Hello", "my", "friend")));
        assertTrue(hashSet.addAll(Arrays.asList("Hello", "my", "boy")));
        assertFalse(hashSet.addAll(Arrays.asList("Hello", "my", "boy")));
        assertTrue(hashSet.contains("Hello"));
        assertTrue(hashSet.contains("my"));
    }

    @Test
    public void removeTest() {
        hashSet.add("Delete me");
        hashSet.add("HJJ");
        hashSet.add("Dpp");
        hashSet.add("lll");
        assertTrue(hashSet.size() == 4);
        assertFalse(hashSet.remove("No"));
        assertTrue(hashSet.size() == 4);
        assertTrue(hashSet.remove("Delete me"));
        assertTrue(hashSet.remove("lll"));
        assertTrue(hashSet.size() == 2);
    }

    @Test
    public void removeAllTest() {
        hashSet.addAll(Arrays.asList("These", "strings", "will", "be", "removed"));
        assertFalse(hashSet.removeAll(Arrays.asList("But", "not", "it")));
        assertTrue(hashSet.removeAll(Arrays.asList("These", "strings", "but", "not", "this")));
        assertFalse(hashSet.contains("These"));
        assertFalse(hashSet.contains("strings"));
        assertTrue(hashSet.contains("removed"));
    }


    @Test
    public void sizeTest() {
        assertTrue(hashSet.size() == 0);
        hashSet.add("word1");
        hashSet.add("word2");
        hashSet.add("word3");
        hashSet.add("word4");
        assertTrue(hashSet.size() == 4);
        hashSet.remove("word1");
        assertTrue(hashSet.size() == 3);
    }

    @Test
    public void containsTest() {
        String first = "The first string";
        String second = "The second string";
        String third = "The third string";
        String fourth = "The fourth string";
        hashSet.addAll(Arrays.asList(first, second, third));
        assertTrue(hashSet.containsAll(Arrays.asList(first, second, third)));
        assertFalse(hashSet.containsAll(Arrays.asList(first, second, fourth)));
        assertTrue(hashSet.contains(first));
        assertFalse(hashSet.contains(fourth));
        hashSet.add(fourth);
        hashSet.remove(first);
        assertTrue(hashSet.contains(fourth));
        assertFalse(hashSet.contains(first));
    }

    @Test
    public void isEmptyTest() {
        assertTrue(hashSet.isEmpty());
        hashSet.add("String");
        assertFalse(hashSet.isEmpty());
        hashSet.remove("String");
        assertTrue(hashSet.isEmpty());
    }

    @Test
    public void toArrayTest() {
        List<String> list = Arrays.asList("Convert", "me", "to", "array");
        hashSet.addAll(list);
        Object[] array = hashSet.toArray();
        assertTrue(list.size() == array.length);
        for (Object o : array) {
            assertTrue(list.contains(o));
        }
    }

    @Test
    public void retainAllTest() {
        hashSet.addAll(Arrays.asList("Hello", "ladies", "and", "gentlemen"));
        hashSet.retainAll(Arrays.asList("ladies", "gentlemen", "children"));
        assertTrue(hashSet.size() == 2);
        assertTrue(hashSet.contains("ladies"));
        assertTrue(hashSet.contains("gentlemen"));
        assertFalse(hashSet.contains("children"));
    }

    @Test
    public void iteratorTest() {
        List<String> list = Arrays.asList("Я", "помню", "чудное", "мгновенье", "передо", "мной", "явилась", "ты");
        hashSet.addAll(list);
        Iterator<String> iterator = hashSet.iterator();
        List<String> secondList = new ArrayList<>();
        while (iterator.hasNext()) {
            secondList.add(iterator.next());
        }
        assertTrue(list.size() == secondList.size());
        Collections.sort(list);
        Collections.sort(secondList);
        assertArrayEquals(list.toArray(), secondList.toArray());
    }

    @Test
    public void equalsTest() {
        List<String> list = new ArrayList<>(Arrays.asList("What", "is", "it", "?"));
        hashSet.addAll(list);
        HashSet<String> secondHashSet = new HashSet<>();
        Collections.reverse(list);
        secondHashSet.addAll(list);
        secondHashSet.addAll(list);
        assertTrue(hashSet.equals(secondHashSet));
        assertTrue(secondHashSet.equals(hashSet));

        HashSet<String> thirdHashSet = new HashSet<>();
        list.add("and");
        thirdHashSet.addAll(list);
        assertFalse(hashSet.equals(thirdHashSet));
        assertFalse(thirdHashSet.equals(hashSet));

        HashSet<Integer> fourthHashSet = new HashSet<>();
        fourthHashSet.add(3);
        assertFalse(hashSet.equals(fourthHashSet));
        assertFalse(fourthHashSet.equals(hashSet));

        list.remove("and");

        Random random = new Random();
        HashSet<String> randomSet = new HashSet<>();
        List<String> randomList = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            randomList.add(String.valueOf(random.nextLong()));
        }
        randomSet.addAll(randomList);
        randomSet.addAll(list);
        randomSet.removeAll(randomList);
        assertTrue(randomSet.equals(hashSet));
        assertTrue(hashSet.equals(randomSet));
    }

    @Test
    public void hashCodeTest() {
        List<String> list = new ArrayList<>(Arrays.asList("4", "1", "fs", "32sd", ",sald"));
        hashSet.addAll(list);

        HashSet<String> secondHashSet = new HashSet<>();
        Collections.reverse(list);
        secondHashSet.addAll(list);

        HashSet<String> thirdHashSet = new HashSet<>();
        Collections.shuffle(list);
        thirdHashSet.addAll(list);

        assertTrue(hashSet.hashCode() == secondHashSet.hashCode());
        assertTrue(hashSet.hashCode() == thirdHashSet.hashCode());
    }

    @Test
    public void nullTest() {
        assertFalse(hashSet.add(null));
        assertTrue(hashSet.isEmpty());
        assertFalse(hashSet.remove(null));
        assertFalse(hashSet.contains(null));
        assertFalse(hashSet.equals(null));
        try {
            hashSet.removeAll(null);
            assertTrue(false);
        } catch (NullPointerException e) {
        //какое-то сообщение об ошибке
        }
        try {
            hashSet.addAll(null);
            assertTrue(false);
        } catch (NullPointerException e) {
        //какое-то сообщение об ошибке
        }
    }
}
