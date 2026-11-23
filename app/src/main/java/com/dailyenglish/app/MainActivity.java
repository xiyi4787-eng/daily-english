package com.dailyenglish.app;

import android.app.Activity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.content.SharedPreferences;
import java.util.Locale;

public class MainActivity extends Activity {
    private final String[] english = {
        "Hello!", "Good morning!", "Good afternoon!", "Good evening!", "How are you?",
        "I'm fine, thank you.", "What's your name?", "My name is Alex.", "Nice to meet you.", "Where are you from?",
        "I'm from China.", "Do you speak English?", "A little bit.", "Please speak slowly.", "Could you say that again?",
        "I don't understand.", "Thank you very much.", "You're welcome.", "See you tomorrow.", "Have a nice day!"
    };
    private final String[] chinese = {
        "你好！", "早上好！", "下午好！", "晚上好！", "你好吗？",
        "我很好，谢谢。", "你叫什么名字？", "我叫亚历克斯。", "很高兴认识你。", "你来自哪里？",
        "我来自中国。", "你会说英语吗？", "会一点点。", "请说慢一点。", "你能再说一遍吗？",
        "我不明白。", "非常感谢。", "不客气。", "明天见。", "祝你今天愉快！"
    };
    private int index = 0;
    private TextView progress, sentence, translation, status;
    private TextToSpeech tts;
    private boolean speechReady = false;
    private SharedPreferences prefs;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences("daily_english", MODE_PRIVATE);
        String today = new java.text.SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new java.util.Date());
        if (!today.equals(prefs.getString("date", ""))) prefs.edit().putString("date", today).putInt("index", 0).apply();
        index = Math.min(19, Math.max(0, prefs.getInt("index", 0)));
        tts = new TextToSpeech(this, result -> {
            speechReady = result == TextToSpeech.SUCCESS && tts.setLanguage(Locale.US) >= TextToSpeech.LANG_AVAILABLE;
            if (status != null) status.setText(speechReady ? "点击朗读，练习英语发音" : "语音暂不可用，请检查手机英语语音设置");
        });
        ScrollView scroll = new ScrollView(this);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(32, 54, 32, 36);
        root.setBackgroundColor(Color.rgb(247, 249, 253));
        scroll.addView(root);
        TextView title = label("每日英语口语", 28, Color.rgb(22, 48, 89));
        title.setGravity(Gravity.CENTER);
        root.addView(title);
        TextView subtitle = label("DAY 01 · 日常打招呼与自我介绍", 16, Color.DKGRAY);
        subtitle.setGravity(Gravity.CENTER);
        root.addView(subtitle);
        progress = label("", 17, Color.rgb(40, 80, 140));
        progress.setPadding(0, 56, 0, 30);
        root.addView(progress);
        sentence = label("", 30, Color.BLACK);
        sentence.setGravity(Gravity.CENTER);
        sentence.setPadding(10, 45, 10, 30);
        root.addView(sentence);
        translation = label("", 22, Color.DKGRAY);
        translation.setGravity(Gravity.CENTER);
        translation.setPadding(10, 0, 10, 55);
        root.addView(translation);
        Button speak = button("🔊 点击朗读英语");
        speak.setOnClickListener(v -> {
            if (speechReady) tts.speak(english[index], TextToSpeech.QUEUE_FLUSH, null, "sentence");
            else status.setText("语音暂不可用，请在手机设置中启用英语文字转语音");
        });
        root.addView(speak);
        LinearLayout nav = new LinearLayout(this);
        nav.setOrientation(LinearLayout.HORIZONTAL);
        Button previous = button("上一句");
        Button next = button("下一句");
        nav.addView(previous, new LinearLayout.LayoutParams(0, -2, 1));
        nav.addView(next, new LinearLayout.LayoutParams(0, -2, 1));
        root.addView(nav);
        previous.setOnClickListener(v -> { if (index > 0) { index--; update(); } });
        next.setOnClickListener(v -> { if (index < 19) { index++; update(); } else status.setText("🎉 今天的20句已学习完成！"); });
        status = label("正在准备英语语音...", 15, Color.GRAY);
        status.setGravity(Gravity.CENTER);
        status.setPadding(0, 35, 0, 0);
        root.addView(status);
        setContentView(scroll);
        update();
    }
    private TextView label(String s, int size, int color) {
        TextView v = new TextView(this); v.setText(s); v.setTextSize(size); v.setTextColor(color); return v;
    }
    private Button button(String s) {
        Button b = new Button(this); b.setText(s); b.setAllCaps(false); b.setTextSize(17); return b;
    }
    private void update() {
        progress.setText("学习进度：" + (index + 1) + " / 20");
        sentence.setText(english[index]);
        translation.setText(chinese[index]);
        prefs.edit().putInt("index", index).apply();
        if (status != null && index == 19) status.setText("最后一句！学完后点击下一句完成打卡");
    }
    @Override protected void onDestroy() {
        if (tts != null) { tts.stop(); tts.shutdown(); }
        super.onDestroy();
    }
}
