import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;

public class PythonMonitorBatch {

    // 超时时间（分钟）
    private static final int TIMEOUT_MINUTES = 10;

    // 启动脚本路径列表
    private static final List<String[]> SCRIPT_COMMANDS = List.of(
            new String[]{"bash", System.getProperty("user.home") + "/Desktop/RUN/run_scraper_STL.command"},
            new String[]{"bash", System.getProperty("user.home") + "/Desktop/RUN/run_scraper_UK"},
            new String[]{"bash", System.getProperty("user.home") + "/Desktop/RUN/run_scraper_DE.command"} //,
        //    new String[]{"bash", System.getProperty("user.home") + "/Desktop/RUN/run_ebay_ids.command"},
        //    new String[]{"bash", System.getProperty("user.home") + "/Desktop/RUN/run_ebay_detail.command"}
    );

    public static void main(String[] args) {
        while (true) {
            for (String[] command : SCRIPT_COMMANDS) {
                runAndMonitorScript(command);
            }
        }
    }

    private static void runAndMonitorScript(String[] command) {
        System.out.println("\n🚀 启动脚本: " + String.join(" ", command));
        ProcessBuilder builder = new ProcessBuilder(command);
        builder.redirectErrorStream(true);

        AtomicReference<Process> processRef = new AtomicReference<>();
        final Instant[] lastOutputTime = {Instant.now()};
        Timer watchdog = new Timer(true);

        try {
            Process process = builder.start();
            processRef.set(process);

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            // 启动看门狗定时器：每分钟检查一次是否超时
            watchdog.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    long minutes = Duration.between(lastOutputTime[0], Instant.now()).toMinutes();
                    if (minutes > TIMEOUT_MINUTES) {
                        System.err.println("⏰ 脚本卡死或无输出，尝试终止...");
                        Process p = processRef.get();
                        if (p != null && p.isAlive()) {
                            p.destroyForcibly();
                        }
                    }
                }
            }, 60_000, 60_000); // 每分钟执行一次

            // 持续读取脚本输出
            String line;
            while ((line = reader.readLine()) != null) {
                lastOutputTime[0] = Instant.now();
                System.out.println("📝 输出: " + line);
            }

            int exitCode = process.waitFor();
            System.out.println("✅ 脚本退出，状态码: " + exitCode);

        } catch (IOException | InterruptedException e) {
            System.err.println("❌ 脚本运行出错: " + e.getMessage());
        } finally {
            watchdog.cancel();
            Process p = processRef.get();
            if (p != null && p.isAlive()) {
                p.destroyForcibly();
            }
        }
    }
}