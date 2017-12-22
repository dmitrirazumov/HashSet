import java.util.Random;

public class Main {

    public static void main(String[] args) {

        HashSet<String> hashSet1 = new HashSet<String>();
        System.out.println("Список пустой? : " + hashSet1.isEmpty());
        hashSet1.add("-140088382");
        Random random = new Random();
        for (int i = 0; i < 140000; i++) {
            hashSet1.add(String.valueOf(random.nextInt()));
        }
        System.out.println("Размер списка после заполнения: " + hashSet1.size());
        System.out.println("Список пустой? : " + hashSet1.isEmpty());
        System.out.println("Этот элемент там есть " + hashSet1.contains("-140088382"));
        System.out.println("А этого нет " + hashSet1.contains("-sd"));
        Object[] objects = hashSet1.toArray();
        System.out.println("Соответсвует ли размер списка размеру массива? " + (objects.length == hashSet1.size()));

        System.out.println(Integer.MIN_VALUE);
    }
}
