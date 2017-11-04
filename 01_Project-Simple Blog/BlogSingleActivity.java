package tw.com.flag.diaryya;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

// after pressed any of the blog on recycler view, it will bring you here.
public class BlogSingleActivity extends AppCompatActivity {

    private String mPost_key=null;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private TextView txv_title;
    private TextView txv_content;
    private ImageView img_show;
    private Button btn_remove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_single);

        txv_content=(TextView)findViewById(R.id.txv_content);
        txv_title=(TextView)findViewById(R.id.txv_title);
        img_show=(ImageView)findViewById(R.id.img_show);
        btn_remove=(Button) findViewById(R.id.btn_remove);

        mDatabase= FirebaseDatabase.getInstance().getReference().child("Blog");
        mAuth=FirebaseAuth.getInstance();

        mPost_key=getIntent().getExtras().getString("blog_id"); //get singleBlog.putExtra("blog_id",post_key).
        //Toast.makeText(BlogSingleActivity.this,post_key,Toast.LENGTH_LONG).show();

        mDatabase.child(mPost_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String title =(String) dataSnapshot.child("title").getValue();
                String content =(String) dataSnapshot.child("content").getValue();
                String image = (String) dataSnapshot.child("image").getValue();
                String uid = (String) dataSnapshot.child("uid").getValue();
                txv_title.setText(title);
                txv_content.setText(content);

                Picasso.with(BlogSingleActivity.this).load(image).into(img_show);
                // check the if it is the current user create that blog, the remove button visible.
                // note: check the user should not use -> mAuth.getCurrentUser().getUid().toString()==uid
                if(mAuth.getCurrentUser().getUid().equals(uid)){
                    // set visible
                    btn_remove.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // remove blog
                mDatabase.child(mPost_key).removeValue();
                Intent backIntent=new Intent(BlogSingleActivity.this, MainActivity.class);
                startActivity(backIntent);
            }
        });
    }
}
