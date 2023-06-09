package com.mindtoheart.licenta.jurnal;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.mindtoheart.licenta.R;

public class Jurnal extends AppCompatActivity {


    FloatingActionButton mcreatenotefap;
    private FirebaseAuth firebaseAuth;

    RecyclerView mrecycleview;
    StaggeredGridLayoutManager staggeredGridLayoutManager;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    Button btnajutor;

    FirestoreRecyclerAdapter<firebasemodel, NoteViewHolder > noteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jurnal);
        getSupportActionBar().setTitle("Jurnalul emoțiilor");
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#006600"));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mcreatenotefap=findViewById(R.id.createnotefap);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore=FirebaseFirestore.getInstance();
        btnajutor=findViewById(R.id.infobtn);
        btnajutor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Jurnal.this, jurnalbtn.class);
                startActivity(intent);
            }
        });


        mcreatenotefap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Jurnal.this, Createnote.class));
            }
        });
        Query query=firebaseFirestore.collection("notes")
                .document(firebaseUser.getUid()).collection("mynotes").orderBy("dateAdded", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<firebasemodel> allusernotes= new FirestoreRecyclerOptions.Builder<firebasemodel>()
                .setQuery(query, firebasemodel.class).build();

        noteAdapter= new FirestoreRecyclerAdapter<firebasemodel, NoteViewHolder>(allusernotes) {

            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder holder, int position, @NonNull firebasemodel model) {

                // pop-up pentru delete si pentru actualizare
                ImageView popup=holder.itemView.findViewById(R.id.menupopbutton);

                holder.notetitle.setText(model.getTitle());
                holder.notecontent.setText(model.getContent());
                holder.noteDate.setText(model.getDateAdded());

                // luam id-ul de la fiecare note in parte ( pentru a edita / sterge )
                String docId=noteAdapter.getSnapshots().getSnapshot(position).getId();

               // pentru apasarea pop-upului
               popup.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       PopupMenu popupMenu= new PopupMenu(v.getContext(), v);
                       popupMenu.setGravity(Gravity.END);
                       popupMenu.getMenu().add("Actualizare").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                           @Override
                           public boolean onMenuItemClick(@NonNull MenuItem item) {
                               Intent intent=new Intent(v.getContext(), editenoteactivity.class);
                               //pentru a arata ce contine notita in layout-ul de edit
                               intent.putExtra("title", model.getTitle());
                               intent.putExtra("content", model.getTitle());
                               intent.putExtra("noteId", docId);
                               v.getContext().startActivity(intent);
                               return false;
                           }
                       });
                       popupMenu.getMenu().add("Ștergere").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                           @Override
                           public boolean onMenuItemClick(@NonNull MenuItem item) {
                               DocumentReference documentReference=firebaseFirestore.collection("notes")
                                       .document(firebaseUser.getUid()).collection("mynotes").document(docId);
                               documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                   @Override
                                   public void onSuccess(Void unused) {
                                       Toast.makeText(v.getContext(), "Ștergerea s-a realizat cu succes!", Toast.LENGTH_SHORT).show();
                                   }
                               }).addOnFailureListener(new OnFailureListener() {
                                   @Override
                                   public void onFailure(@NonNull Exception e) {
                                       Toast.makeText(v.getContext(), "Ștergerea nu s-a realizat cu succes!", Toast.LENGTH_SHORT).show();
                                   }
                               });

                               return false;
                           }
                       });
                    popupMenu.show();
                   }
               });

            }

            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_layout,parent,false);
                return new NoteViewHolder(view);
            }
        };

        mrecycleview=findViewById(R.id.recyclerview);
        mrecycleview.setHasFixedSize(true);
        staggeredGridLayoutManager=new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        mrecycleview.setLayoutManager(staggeredGridLayoutManager);
        mrecycleview.setAdapter(noteAdapter);
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    public class NoteViewHolder extends RecyclerView.ViewHolder
    {
        private TextView notetitle;
        private TextView notecontent;
        private TextView noteDate;
        LinearLayout mnote;
        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            notetitle=itemView.findViewById(R.id.notetitle);
            notecontent=itemView.findViewById(R.id.notecontent);
            mnote=itemView.findViewById(R.id.note);
            noteDate=itemView.findViewById(R.id.currentDate);

        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(noteAdapter!=null){
            noteAdapter.stopListening();
        }
    }
}