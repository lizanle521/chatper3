package concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

/**
 * Created by Administrator on 2017/8/27.
 */
public class CountTask extends RecursiveTask<Long> {
    // 阈值，超过这个阈值就要进行fork/join计算
    public static final long THRESHOLD = 10000L;
    private long start;
    private long end;

    public CountTask(long start,long end) {
        this.start = start;
        this.end = end;
    }

    protected Long compute() {
        long sum = 0;
        boolean canCompute = end - start > THRESHOLD;
        if(canCompute){
            for(long i = start;i<=end;i++){
                sum += i;
            }
        }else {
            // 分成100个小任务
            long step = (end + start ) / 100;
            List<CountTask> subTasks = new ArrayList<>();
            long pos = start;
            for(int i = 0;i<100;i++){
                //最后一个任务
                long lastOne = pos + step;

                if(lastOne > end ){
                    lastOne = end;
                }
                CountTask countTask = new CountTask(pos,lastOne);
                pos += step + 1;

                subTasks.add(countTask);
                countTask.fork();
            }
            for (CountTask subTask : subTasks) {
                sum += subTask.join();
            }
        }
        return sum;
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        CountTask countTask = new CountTask(0, 200000L);
        ForkJoinTask<Long> submit = forkJoinPool.submit(countTask);
        System.out.println("result is "+submit.get());
        long sum =  0l;
        for (long i = 0; i <= 200000l; i++) {
            sum += i;
        }
        System.out.println("result is "+ sum);
    }

}

