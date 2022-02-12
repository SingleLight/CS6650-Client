package ClientPart1;

import io.swagger.client.ApiException;
import io.swagger.client.api.SkiersApi;
import io.swagger.client.model.LiftRide;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class RequestThread implements Runnable {

  private final AtomicInteger successCounter;
  private final AtomicInteger failureCounter;
  private final String baseURL;
  private final int startSkierID;
  private final int endSkierID;
  private final int startTime;
  private final int endTime;
  private final int numLifts;
  private final SkiersApi skiersApi;
  private final Random random;
  private final CountDownLatch countDownLatch;

  public RequestThread(AtomicInteger successCounter,
      AtomicInteger failureCounter, String baseURL, int startSkierID, int endSkierID, int startTime,
      int endTime, int numLifts, SkiersApi skiersApi, Random random, CountDownLatch countDownLatch) {
    this.successCounter = successCounter;
    this.failureCounter = failureCounter;
    this.baseURL = baseURL;
    this.startSkierID = startSkierID;
    this.endSkierID = endSkierID;
    this.startTime = startTime;
    this.endTime = endTime;
    this.numLifts = numLifts;
    this.skiersApi = skiersApi;
    this.random = random;
    this.countDownLatch = countDownLatch;
  }

  @Override
  public void run() {
    for (int i = 0; i < endSkierID - startSkierID; i++) {
      for (int j = 0; j < 5; j++) {
        try {
          skiersApi.writeNewLiftRide(new LiftRide().liftID(random.nextInt(numLifts))
                  .time(random.nextInt(endTime - startTime) + startTime).waitTime(random.nextInt(10)),
              1,
              "seasonExample", "dayExample",
              random.nextInt(endSkierID - startSkierID) + startSkierID);
          successCounter.incrementAndGet();
          break;
        } catch (ApiException e) {
          e.printStackTrace();
          if (j == 4) {
            failureCounter.incrementAndGet();
            break;
          }
          try {
            this.wait((long) Math.pow(2, j));
          } catch (InterruptedException ex) {
            ex.printStackTrace();
          }
        }
      }
    }
    countDownLatch.countDown();
  }
}
