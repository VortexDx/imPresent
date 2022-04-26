package com.final_project.impresent;



import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StudentDAO {
    private DatabaseReference databaseReference;
    public StudentDAO(){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference(Student.class.getSimpleName());
    }
    public Task<Void> add(Student student){

        return databaseReference.push().setValue(student);
    }

}
