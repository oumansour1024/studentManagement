package com.example.studentmanagement.ui.student;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentmanagement.R;
import com.example.studentmanagement.data.local.entity.Student;

import java.util.ArrayList;
import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {
    private List<Student> students = new ArrayList<>();
    private OnItemClickListener listener;

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student, parent, false);
        return new StudentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Student currentStudent = students.get(position);
        holder.tvName.setText(currentStudent.getFullName());
        holder.tvEmail.setText(currentStudent.getEmail() != null ? currentStudent.getEmail() : "");
        holder.tvPhone.setText(currentStudent.getTelephone() != null ? currentStudent.getTelephone() : "");
        
        String initials = "";
        if (currentStudent.getPrenom() != null && !currentStudent.getPrenom().isEmpty()) {
            initials += currentStudent.getPrenom().charAt(0);
        }
        if (currentStudent.getNom() != null && !currentStudent.getNom().isEmpty()) {
            initials += currentStudent.getNom().charAt(0);
        }
        holder.tvInitials.setText(initials.toUpperCase());
        
        // Set click listener on the entire item
        holder.itemView.setOnClickListener(v -> {
            if (listener != null && position != RecyclerView.NO_POSITION) {
                listener.onItemClick(students.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return students != null ? students.size() : 0;
    }

    public void setStudents(List<Student> students) {
        this.students = students != null ? students : new ArrayList<>();
        notifyDataSetChanged();
    }

    class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail, tvPhone, tvInitials;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvInitials = itemView.findViewById(R.id.tvInitials);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Student student);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}