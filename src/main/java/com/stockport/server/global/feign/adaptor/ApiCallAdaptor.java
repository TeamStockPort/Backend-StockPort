package com.stockport.server.global.feign.adaptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

@Component
@Slf4j
public class ApiCallAdaptor {
    private static final Lock GLOBAL_LOCK = new ReentrantLock(true); // FIFO 공정 락
    private static long lastCallTime = 0L; // 마지막 호출 시각 (전역)
    private static final long INTERVAL_MS = 1000; // 1초

    public <T> T callWithWait(Supplier<T> apiCall) {
        GLOBAL_LOCK.lock();
        try {
            long now = System.currentTimeMillis();
            long elapsed = now - lastCallTime;

            // 아직 1초 안 지났으면 남은 시간만큼 대기
            if (elapsed < INTERVAL_MS) {
                long remain = INTERVAL_MS - elapsed;
                Thread.sleep(remain);
            }

            T result = apiCall.get();
            lastCallTime = System.currentTimeMillis();
            return result;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("API 호출 대기 중 인터럽트 발생", e);
        } catch (Exception e) {
            log.error("API 호출 중 예외 발생: {}", e.getMessage(), e);
            throw e;
        } finally {
            GLOBAL_LOCK.unlock();
        }
    }
}