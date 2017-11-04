package tw.com.flag.diaryya;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private EditText edt_name;
    private EditText edt_email;
    private EditText edt_password;

    private Button btn_register;

    private FirebaseAuth mAuth;

    private ProgressDialog mProgress;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mProgress = new ProgressDialog(this);

        mAuth=FirebaseAuth.getInstance();
        mDatabase= FirebaseDatabase.getInstance().getReference().child("Users");

        edt_email=(EditText) findViewById(R.id.edt_email);
        edt_name=(EditText) findViewById(R.id.edt_name);
        edt_password=(EditText) findViewById(R.id.edt_password);
        btn_register=(Button) findViewById(R.id.btn_register);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startRegister();
            }
        });
    }

    private void startRegister() {
        final String name = edt_name.getText().toString().trim();
        String email= edt_email.getText().toString().trim();
        String password=edt_password.getText().toString().trim();


        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
            mProgress.setMessage("Signing up...");
            mProgress.show();
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        String user_id=mAuth.getCurrentUser().getUid();

                        DatabaseReference current_user_id_db=mDatabase.child(user_id);

                        current_user_id_db.child("name").setValue(name);
                        current_user_id_db.child("image").setValue("default");
                        mProgress.dismiss();

                        Intent mainIntent=new Intent(RegisterActivity.this,MainActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntent);
                    }
                }
            });

        }
    }
}
