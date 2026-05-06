package com.evently.booking_service.client;

import com.evently.booking_service.dto.NotificationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notification-service")
public interface NotificationServiceClient {

    @PostMapping("/notifications")
    void createNotification(@RequestBody NotificationRequest request);
}
