package com.final_project.impresent;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TeacherDAO {
    private DatabaseReference databaseReference;
    public TeacherDAO() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference(Teacher.class.getSimpleName());
    }
    public Task<Void> add(Teacher teacher){
        return databaseReference.push().setValue(teacher);
    }
}
