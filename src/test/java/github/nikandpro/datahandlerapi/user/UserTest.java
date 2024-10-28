package github.nikandpro.datahandlerapi.user;

import github.nikandpro.datahandlerapi.entity.User;
import github.nikandpro.datahandlerapi.repository.UserRepository;
import github.nikandpro.datahandlerapi.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.stream.IntStream;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class UserTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    public final int size = 100000;
    public final int time_wait = 1000;


    @Test
    public void fillingUsers() {
        List<User> users = IntStream.range(0, size)
                .mapToObj(i -> new User("user" + i))
                .toList();
        userRepository.saveAll(users);
    }


    @Test
    public void getUsers() throws ExecutionException, InterruptedException, TimeoutException {
        List<Long> array = new ArrayList<>();
        List<CompletableFuture<Long>> futures = new ArrayList<>();

        for (int i = 0; i < size; i++) {

            CompletableFuture<Long> f = CompletableFuture.supplyAsync(() -> {
                long start = System.currentTimeMillis();

                long randomId = ThreadLocalRandom.current().nextLong(size);
                userService.findById(randomId);

                long end = System.currentTimeMillis();
                return end - start;
            });
            futures.add(f);
        }

        for (Future<Long> f : futures) {
            array.add(f.get(time_wait, TimeUnit.MILLISECONDS));
        }


        Optional<Long> sum = array.stream().reduce(Long::sum);
        Long avg = null;
        if (sum.isPresent()) {
            avg = sum.get() / array.size();
        }

        Long median = percentile(array, 0.5);
        Long p95 = percentile(array, 0.95);
        Long p99 = percentile(array, 0.99);

        Assertions.assertTrue(median < 50L);
        Assertions.assertTrue(p95 < 100L);
        Assertions.assertTrue(p99 < 200L);
        Assertions.assertNotNull(avg);

    }

    private Long percentile(List<Long> array, double n) {
        array.sort(Long::compareTo);
        double k = (array.size() - 1) * n;
        double f = Math.floor(k);
        double c = Math.ceil(k);
        if (f == c) {
            return array.get((int) k);
        }
        Long d0 = (long) (array.get((int) f) * (c - k));
        Long d1 = (long) (array.get((int) c) * (k - f));
        return d0 + d1;
    }
}