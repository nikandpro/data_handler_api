package github.nikandpro.datahandlerapi.user;

import github.nikandpro.datahandlerapi.dto.UserDto;
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

    @Test
    public void saveUsers() {
        List<User> users = IntStream.range(0, 100)
                .mapToObj(i -> new User("user" + i))
                .toList();
        userRepository.saveAll(users);
    }

    @Test
    public void getUsers() throws ExecutionException, InterruptedException, TimeoutException {
        List<Long> array = new ArrayList<>();
        List<CompletableFuture<Long>> futures = new ArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        for (int i = 0; i < 100; i++) {

            CompletableFuture<Long> f = CompletableFuture.supplyAsync(() -> {
                        long start = System.currentTimeMillis();

                        long randomId = ThreadLocalRandom.current().nextLong(100000);
                        userRepository.findById(randomId);

                        long end = System.currentTimeMillis();
                        return end - start;
                    });
            futures.add(f);
        }

        for (Future<Long> f : futures) {
            array.add(f.get(500, TimeUnit.MILLISECONDS));
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