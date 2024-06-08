import java.util.HashMap;
import java.util.Map;

public class ProcessStat {
    public static class Color {
        public static final String BLACK = "\033[30m"; // (文字)黒
        public static final String RED = "\033[31m"; // (文字)赤
        public static final String GREEN = "\033[32m"; // (文字)緑
        public static final String YELLOW = "\033[33m"; // (文字)黄
        public static final String BLUE = "\033[34m"; // (文字)青
        public static final String MAGENTA = "\033[35m"; // (文字)マゼンタ
        public static final String CYAN = "\033[36m"; // (文字)シアン
        public static final String WHITE = "\033[37m"; // (文字)白
        public static final String COLOR_DEFAULT = "\033[39m"; // 文字色をデフォルトに戻す
        public static final String BOLD = "\033[1m"; // 太字
        public static final String UNDERLINE = "\033[4m"; // 下線
        public static final String INVISIBLE = "\033[08m"; // 不可視
        public static final String REVERCE = "\033[07m"; // 文字色と背景色を反転
        public static final String BG_BLACK = "\033[40m"; // (背景)黒
        public static final String BG_RED = "\033[41m"; // (背景)赤
        public static final String BG_GREEN = "\033[42m"; // (背景)緑
        public static final String BG_YELLOW = "\033[43m"; // (背景)黄
        public static final String BG_BLUE = "\033[44m"; // (背景)青
        public static final String BG_MAGENTA = "\033[45m"; // (背景)マゼンタ
        public static final String BG_CYAN = "\033[46m"; // (背景)シアン
        public static final String BG_WHITE = "\033[47m"; // (背景)白
        public static final String BG_DEFAULT = "\033[49m"; // 背景色をデフォルトに戻す
        public static final String RESET = "\033[0m"; // 全てリセット
    }

    public static class Timer {
        protected long start;

        public Timer() {
            this.start = System.currentTimeMillis();
        }

        public long show() {
            long t = System.currentTimeMillis() - this.start;
            System.out.println(t);
            return t;
        }
    }

    public static class Process {
        protected String name;
        protected long start;
        protected long sumDurations;
        protected int len;

        public Process(String name) {
            this.name = name;
            this.start = System.currentTimeMillis();
            this.sumDurations = 0;
            this.len = 0;
        }

        public void Start() {
            this.start = System.currentTimeMillis();
        }

        public void End() {
            this.sumDurations += System.currentTimeMillis() - this.start;
            this.len += 1;
        }

        public double GetAVG() {
            if (this.len == 0) {
                return 0;
            }
            return (double) this.sumDurations / this.len;
        }

        public int Len() {
            return this.len;
        }
    }

    private static long startTime = 0;
    private static int count = 0;
    private static Map<String, Process> processes = new HashMap<>();

    public static void TrackStart(String name) {
        if (!processes.containsKey(name)) {
            processes.put(name, new Process(name));
        } else {
            processes.get(name).Start();
        }
    }

    public static void TrackEnd(String name) {
        if (!processes.containsKey(name)) {
            return;
        }
        processes.get(name).End();
    }

    public static void TickStart() {
        startTime = System.currentTimeMillis();
        processes = new HashMap<>();
        count += 1;
    }

    public static String GetColor(double ratio) {
        String[] colors = {
                Color.BG_RED,
                Color.BG_RED,
                Color.BG_MAGENTA,
                Color.BG_YELLOW,
                Color.BG_GREEN,
        };
        if (ratio == 0) {
            return "";
        }
        int idx = (int) Math.min(Math.max(1 / ratio, 0), colors.length - 1);
        return colors[idx];
    }

    public static void TickEnd() {
        double overall = System.currentTimeMillis() - startTime;
        System.out.printf(
                "\n%s%d回目のTick, 総時間:%.8gms%s\n",
                Color.BG_BLACK, count, overall, Color.RESET);
        for (Map.Entry<String, Process> entry : processes.entrySet()) {
            String name = entry.getKey();
            Process obj = entry.getValue();
            int num = obj.Len();
            double delta = obj.GetAVG() * num;
            String color = GetColor(delta / overall);

            System.out.printf(
                    "%s%s%s:samples:%s%d%s\n",
                    Color.BG_WHITE, name, Color.RESET,
                    Color.GREEN, num, Color.RESET);
            System.out.printf(
                    "Total  :%s%.8g%sms, %s%.2f%%%s\n",
                    Color.RED, delta, Color.RESET,
                    color, delta / overall * 100, Color.RESET);
            delta = obj.GetAVG();
            System.out.printf("Average:%.8gms,%.2f%%\n", delta, delta / overall * 100);
        }
    }

    public static void main(String[] args) {
        TickStart();
        TrackStart("A");
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        TrackStart("B");
        TrackEnd("A");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        TrackEnd("B");
        TrackStart("C");
        TrackStart("A");
        TrackEnd("A");
        TickEnd();
    }
}
