import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notifications")
public class NotificationController {
    @PostMapping
    public String send(@RequestBody Map<String, String> body) {

        System.out.println("Notification: " + body);

        return "Notification Sent!";
    }
}
