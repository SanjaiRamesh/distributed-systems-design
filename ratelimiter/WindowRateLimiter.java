package ratelimiter;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class WindowRateLimiter {
    public int count;
    public int windowSize;
    public Map<String, Request> map = new HashMap<>();

    public WindowRateLimiter(int windowSize, int count) {
        this.windowSize = windowSize;
        this.count = count;
    }

    public boolean isUserAllowed(String user){
        long now = System.currentTimeMillis();

        Request request = map.get(user);

        if (request == null) {
            map.put(user, new Request(1,now));
            return true;
        }
        if(now - request.getLastTime() >= windowSize){
            map.put(user, new Request(1,now));
            return true;
        }
        if(request.getCount()+1<= count) {
            request.setCount(request.getCount()+1);
            return true;
        }

        return false;
    }
    public static void main(String[] args) throws InterruptedException {
        WindowRateLimiter rateLimiter = new WindowRateLimiter(5000,3);

        for(int i=1;i<=5;i++){
            System.out.println("request: "+i+":"+(rateLimiter.isUserAllowed("sanjai")?"Allow":"Deny"));
        }

        Thread.sleep(5000);
        for(int i=1;i<=1;i++){
            System.out.println("request: "+i+":"+(rateLimiter.isUserAllowed("sanjai")?"Allow":"Deny"));
        }
    }

}
class Request {
    private int count;
    private long lastTime;

    public Request(int count, long lastTime) {
        this.count = count;
        this.lastTime = lastTime;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public long getLastTime() {
        return lastTime;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }
}