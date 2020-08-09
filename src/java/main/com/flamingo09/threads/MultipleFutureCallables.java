package java.main.com.flamingo09.threads;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

public class MultipleFutureCallables {

    public static void main(String[] args) throws InterruptedException, ExecutionException, TimeoutException {
        new MultipleFutureCallables().runThreadsWithCallable();
    }

    private static class MyObject {
        String name;
        String surname;
        int age;

        MyObject(String name, String surname, int age) {
            this.name = name;
            this.surname = surname;
            this.age = age;
        }

        public static int addAge(int age) {
            return age + 12;
        }

    }

    public void runThreadsWithCallable() throws InterruptedException, ExecutionException, TimeoutException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        try {
            List<MyObject> myObjectList = new ArrayList<>();
            for(int i=0; i < 10; i++)
                myObjectList.add(new MyObject("Akanksha", "Singh", (int) (Math.random() * (26 - 0 + 1) +1)));

            Collection<Callable<MyObject>> callables = new ArrayList<>();

            for(MyObject myObject : myObjectList) {
                createCallable(myObject);
            }

            /* invoke all supplied Callables */
            List<Future<MyObject>> taskFutureList = executorService.invokeAll(callables);

            /* call get on Futures to retrieve result when it becomes available.
             * If specified period elapses before result is returned a TimeoutException
             * is thrown
             */
            for (Future<MyObject> future : taskFutureList) {

                /* get Double result from Future when it becomes available */
                MyObject value = future.get(1, TimeUnit.SECONDS);
                System.out.println(String.format("TaskFuture returned value %s", value.toString()));
            }
        }
        finally {
            executorService.shutdown();
        }
    }

    protected Callable<MyObject> createCallable(MyObject myObject) {
        return new Callable<MyObject>() {
            @Override
            public MyObject call() throws Exception {
                System.out.println(String.format("I got age as : %d", myObject.age));
                myObject.age = MyObject.addAge(myObject.age);
                System.out.println(String.format("And I changed it to : %d", myObject.age));
                return myObject;
            }
        };
    }
}
