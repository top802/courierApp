package wisehands.me.deliverty;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class MessageActivity extends AppCompatActivity {

    private MessageActivity context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bn_navi_view);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.item_main:

                            case R.id.item_profile:
                                finish();
                                startActivity(new Intent(context, ProfileActivity.class));
                            case R.id.item_message:

                        }
                        return true;
                    }
                });


    }
}
