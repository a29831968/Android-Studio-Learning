package tw.com.flag.diaryya;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.SingleLineTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.Set;

public class MainActivity extends AppCompatActivity {
    /* always check
    if(Auth.getCurrentUser()!=null)
    which always make mistakes.
    */
    private RecyclerView mBloglist; // 動態牆用的 view

    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseUsers;
    private DatabaseReference mDatabaseLike;
    private DatabaseReference mDatabaseCurrentUser;
    private Query mQuery;

    private FirebaseAuth mAuth;  // 授權
    private FirebaseAuth.AuthStateListener mAuthListener;


    //for like button
    private boolean mProcessLike=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(firebaseAuth.getCurrentUser()==null){
                    Intent loginIntent=new Intent(MainActivity.this,LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                }
            }
        };

        mDatabase=FirebaseDatabase.getInstance().getReference().child("Blog");
        mDatabaseUsers=FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseLike=FirebaseDatabase.getInstance().getReference().child("Likes");

        //can only see the post which is create by current user
        mDatabaseCurrentUser=FirebaseDatabase.getInstance().getReference().child("Blog");
        String mCurrentUser=null;
        if (mAuth.getCurrentUser()!=null){
            mCurrentUser=mAuth.getCurrentUser().getUid();
        }

        //sort and comparison, see if it is a blog created by current user.
        mQuery=mDatabaseCurrentUser.orderByChild("uid").equalTo(mCurrentUser);

        mDatabaseUsers.keepSynced(true); // 數據存取不被刪除
        mDatabase.keepSynced(true); // 詳細解釋需要查詢
        mDatabaseLike.keepSynced(true);

        mBloglist = (RecyclerView) findViewById(R.id.blog_list);
        mBloglist.setHasFixedSize(true);
        mBloglist.setLayoutManager(new LinearLayoutManager(this));

        checkUserExist(); //原本放在onStart()裡面

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

        FirebaseRecyclerAdapter<Blog,BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(Blog.class,
                R.layout.blog_row,
                BlogViewHolder.class,
                mQuery)
        {
            @Override
            protected void populateViewHolder(BlogViewHolder viewHolder, Blog model, int position) {
                // get the key of that view
                final String post_key=getRef(position).getKey();

                viewHolder.setTitle(model.getTitle());
                viewHolder.setContent(model.getContent());
                viewHolder.setImage(getApplicationContext(),model.getImage());
                viewHolder.setUsername(model.getUsername());
                viewHolder.setLikeBtn(post_key);


                // set onclick over recyclerView in Firebase
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //bring the user to the SingBlogActivity
                        Intent singleBlog=new Intent(MainActivity.this, BlogSingleActivity.class);
                        singleBlog.putExtra("blog_id",post_key); // record which blog you have pressed.
                        startActivity(singleBlog);

                        //Toast.makeText(MainActivity.this, "Click on : "+post_key, Toast.LENGTH_LONG).show();
                    }
                });
                // set onclick on like button
                viewHolder.btn_like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mProcessLike=true;
                            mDatabaseLike.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    // check if the blog's like has already pressed or not.
                                    if (mProcessLike) {
                                        if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {
                                            mDatabaseLike.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();

                                            mProcessLike = false;

                                        } else {
                                            mDatabaseLike.child(post_key).child(mAuth.getCurrentUser().getUid()).setValue("1");
                                            mProcessLike = false;
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }

                    });

                }
        };
        mBloglist.setAdapter(firebaseRecyclerAdapter);
    }

    private void checkUserExist() {
        if (mAuth.getCurrentUser()!=null) {
            final String user_id = mAuth.getCurrentUser().getUid();

            mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.hasChild(user_id)) {

                        Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);
                        setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(setupIntent);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder{

        View mView;
        TextView post_title;
        ImageButton btn_like;
        DatabaseReference mDatabaseLikeBtn;
        FirebaseAuth mAuth;

        public BlogViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            btn_like=(ImageButton) mView.findViewById(R.id.btn_like);

            mDatabaseLikeBtn=FirebaseDatabase.getInstance().getReference().child("Likes");
            mAuth=FirebaseAuth.getInstance();
            mDatabaseLikeBtn.keepSynced(true);

            // set onclick on individual view or button
            post_title = (TextView) mView.findViewById(R.id.post_title);
            post_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.v("MainActivity", "Some text"); //show message on monitor
                }
            });

        }
        // like function
        public void setLikeBtn(final String post_key){
            // real time data, so using addValueEventListener.
            mDatabaseLikeBtn.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // change the like color.
                    if(dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())){
                        btn_like.setImageResource(R.mipmap.ic_favorite_black_24dp);
                    }else{
                        btn_like.setImageResource(R.mipmap.ic_favorite_border_black_24dp);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        public void setTitle(String title){
            //TextView post_title = (TextView) mView.findViewById(R.id.post_title);
            post_title.setText(title);
        }
        public void setContent(String content){
            TextView post_content = (TextView) mView.findViewById(R.id.post_content);
            post_content.setText(content);
        }
        public void setImage(Context ctx, String image){
            ImageView post_image=(ImageView)mView.findViewById(R.id.post_image);
            Picasso.with(ctx).load(image).into(post_image);
        }
        public void setUsername(String username){
            TextView post_username=(TextView) mView.findViewById(R.id.post_username);
            post_username.setText(username);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.action_add){
            startActivity(new Intent(MainActivity.this,PostActivity.class));
        }
        if(item.getItemId()==R.id.action_logout){
            logout();

        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        mAuth.signOut();
    }
}
