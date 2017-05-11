package me.donnie.divider;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rv = (RecyclerView) findViewById(R.id.rv);

        new Divider.Builder(this)
                .hideLastDivider()
                .drawable(ContextCompat.getDrawable(this, R.drawable.dash_divider))
                .build().addTo(rv);


        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        rv.setAdapter(new TestAdaper(getData()));
    }

    private List<String> getData() {
        List<String> datas = new ArrayList<>();
        for (int i = 0; i < 40; i++) {
            datas.add("alksdjfkjasdj"+i);
        }
        return datas;
    }
}
