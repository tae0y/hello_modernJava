package CHAPTER08;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import static java.util.Map.entry;

public class MapProcessor {
    public MessageDigest messageDigest;

    public void _test(){
        //forEachEntry();
        //sortEntry();
        //getOrDefaultEntry();
        //computePattern();
        //removePattern();
        //replacePattern();
        //mergePattern();
        concurrentHashMapExercise();
    }

    private void forEachEntry() {
        Map<String, Integer> ageOfFriends = new HashMap<>();
        ageOfFriends.put("김이름", 30);
        ageOfFriends.put("이익명", 40);
        ageOfFriends.put("박닉네임", 50);

        for(Map.Entry<String, Integer> entry : ageOfFriends.entrySet()){
            String name = entry.getKey();
            Integer age = entry.getValue();
            System.out.println(name + " is " + age + " years old.");
        }

        ageOfFriends.forEach((name, age)->System.out.println(name+" is "+age+" years old."));
    }

    private void sortEntry() {
        Map<String, String> favouriteMovies = Map.ofEntries(
                entry("Rahpael", "Star Wars"),
                entry("Cristina", "Matrix"),
                entry("Olivia", "James bond"));

        favouriteMovies
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                //.forEach(System.out::println);
                .forEachOrdered(System.out::println);

    }

    private void getOrDefaultEntry() {
        Map<String, String> favouriteMovies = Map.ofEntries(
                entry("Rahpael", "Star Wars"),
                entry("Cristina", "Matrix"));
        System.out.println(favouriteMovies.getOrDefault("Olivia", "Matrix"));
        System.out.println(favouriteMovies.getOrDefault("Timothy", "Dune"));
    }

    private void computePattern() {
        //파일을 읽어와 해시값을 구하는 코드
        try (Stream<String> lines = Files.lines(Paths.get("D:\\DailyLogs\\README.md"), Charset.defaultCharset())){
            Map<String, byte[]> dataToHash = new HashMap<>();
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            this.messageDigest = messageDigest;

            lines.forEach(line -> dataToHash.computeIfAbsent(line, this::calculateDigest));

        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }

        //친구에게 추천해줄 영화 목록을 만들기
        Map<String, List<String>> friendsToMovies = new HashMap<>();
        String friend = "Raphael";
        List<String> movies = friendsToMovies.get(friend);
        if(movies==null){
            movies = new ArrayList<>();
            friendsToMovies.put(friend, movies);
        }
        movies.add("Star Wars");

        Map<String, List<String>> friendsToMovies2 = new HashMap<>();
        friendsToMovies2.computeIfAbsent("Raphael", name -> new ArrayList<>())
                        .add("Star Wars");
        System.out.println(friendsToMovies2);

        friendsToMovies2.get("Raphael");
        friendsToMovies.computeIfPresent("Raphael", (k, v) -> null);
        System.out.println(friendsToMovies2);

    }

    private byte[] calculateDigest(String key) {
        return messageDigest.digest(key.getBytes(StandardCharsets.UTF_8));
    }

    private void removePattern() {
        Map<String, String> favouriteMovies = new HashMap<>(Map.ofEntries(
                entry("Rahpael", "Star Wars"),
                entry("Cristina", "Matrix"),
                entry("Olivia", "James bond")));

        String key = "Raphael";
        String value = "Jack Reacher 2";

        if(favouriteMovies.containsKey(key) && Objects.equals(favouriteMovies.get(key), value)){
            favouriteMovies.remove(key);
        }

        favouriteMovies.remove(key, value);

    }

    private void replacePattern() {
        Map<String, String> favouriteMovies = new HashMap<>(Map.ofEntries(
                entry("Rahpael", "Star Wars"),
                entry("Cristina", "Matrix"),
                entry("Olivia", "James bond")));
        favouriteMovies.replaceAll((friend, movie) -> movie.toUpperCase());
        System.out.println(favouriteMovies);
    }

    private void mergePattern() {
        Map<String, String> family = Map.ofEntries(entry("Teo", "Star Wars"), entry("Cristina", "James Bond"));
        Map<String, String> friends = Map.ofEntries(entry("Raphael", "Star Wars"), entry("Cristina", "Matrix"));

        Map<String, String> everyone = new HashMap<>(family);
        everyone.putAll(friends); //키값이 충돌해 의도하지 않은 오류가 발생할 수 있다.
        System.out.println(everyone);

        Map<String, String> everyone2 = new HashMap<>(family);
        friends.forEach((k,v)->everyone2.merge(k,v,(movie1, movie2)->movie1+" & "+movie2));
        System.out.println(everyone2);

        Map<String, Long> moviesToCount = new HashMap<>();
        String movieName = "JamesBond";
        Long count = moviesToCount.get(movieName);
        if(count == null){
            moviesToCount.put(movieName, 1L);
        }else{
            moviesToCount.put(movieName, count+1L);
        }

        moviesToCount.merge(movieName, 1L, (k, v)->v+1L);

        Map<String, Integer> movies = new HashMap<>();
        movies.put("JamesBond", 20);
        movies.put("Matrix", 15);
        movies.put("Harry Potter", 5);
        /*Iterator<Map.Entry<String, Integer>> iterator = movies.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String, Integer> entry = iterator.next();
            if(entry.getValue() < 10){
                iterator.remove();
            }
        }*/
        movies.entrySet().removeIf(e -> e.getValue()<10);

    }

    private void concurrentHashMapExercise() {
        ConcurrentHashMap<String, Long> map = new ConcurrentHashMap<>();
        map.put("A",1L);
        map.put("B",2L);
        map.put("C",3L);
        map.put("D",4L);
        map.put("E",5L);
        map.put("F",16L);

        long parallelismThreshod = 1;
        Optional<Long> maxValue = Optional.ofNullable(map.reduceValues(parallelismThreshod, Long::max));
        System.out.println(maxValue);

        System.out.println(map.mappingCount());
        System.out.println(map.size());

        System.out.println("#1 ==========================================");
        ConcurrentHashMap.KeySetView<String, Long> set = map.keySet(0L);
        System.out.println(map);
        System.out.println(set);
        System.out.println(set.getMappedValue());

        System.out.println("#2 ==========================================");
        ConcurrentHashMap.KeySetView<String, Long> kset = map.keySet();
        System.out.println(kset.getMappedValue());

        System.out.println("#3 ==========================================");
        set.add("BYE");
        System.out.println(map);
        System.out.println(set);

        System.out.println("#4 ==========================================");
        map.replace("A", 100L);
        map.put("HELLO", 1000L);
        System.out.println(map);
        System.out.println(set);

        System.out.println("#5 ==========================================");
        ConcurrentHashMap.KeySetView<String, Boolean> keySet = ConcurrentHashMap.newKeySet();
        keySet.add("HELLO");
        System.out.println(keySet);

        System.out.println("#6 ==========================================");
        ConcurrentHashMap.KeySetView<String, Boolean> keySet2 = ConcurrentHashMap.newKeySet(10);
        keySet.add("HELLO");
        System.out.println(keySet);
        System.out.println(keySet.size());


    }


}
