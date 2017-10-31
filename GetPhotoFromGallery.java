package tw.com.flag.diaryya;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class PostActivity extends AppCompatActivity {

    private ImageButton img_select;

    private EditText txv_title;
    private EditText txv_content;

    private Button btn_post;

    private ProgressDialog mProgress;

    private StorageReference mStorage;
    private DatabaseReference mDatabase;

    private Uri imageUri=null;

    private static final int GALLERY_REQUEST=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mStorage= FirebaseStorage.getInstance().getReference();
        mDatabase=FirebaseDatabase.getInstance().getReference().child("Blog");

        mProgress=new ProgressDialog(this);

        txv_content=(EditText) findViewById(R.id.txv_content);
        txv_title=(EditText) findViewById(R.id.txv_title);

        btn_post=(Button) findViewById(R.id.btn_post);

        img_select=(ImageButton) findViewById(R.id.img_select);
        
        //使用intent調用系統提供的相冊功能，使用startActivityForResult是為了獲取用戶選擇的圖片
        
        img_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GALLERY_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
                                           // resultCode 系統自定義的
        if(requestCode==GALLERY_REQUEST && resultCode==RESULT_OK){
            imageUri=data.getData();  //得到照片的Uri
            img_select.setImageURI(imageUri);   
        }
    }
}
