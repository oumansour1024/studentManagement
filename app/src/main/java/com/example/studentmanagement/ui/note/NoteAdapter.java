package com.example.studentmanagement.ui.note;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentmanagement.R;
import com.example.studentmanagement.data.local.entity.Matiere;
import com.example.studentmanagement.data.local.entity.Note;
import com.example.studentmanagement.data.local.entity.Student;

import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {
    private List<Note> notes = new ArrayList<>();
    private List<Student> students = new ArrayList<>();
    private List<Matiere> matieres = new ArrayList<>();
    private OnItemClickListener listener;
    private OnEditClickListener editListener;
    private OnDeleteClickListener deleteListener;

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note currentNote = notes.get(position);
        
        String studentName = getStudentName(currentNote.getStudentId());
        String matiereName = getMatiereName(currentNote.getMatiereId());
        
        holder.tvStudentName.setText(studentName);
        holder.tvMatiereLabel.setText(matiereName);
        holder.tvNote.setText(String.format("%.1f", currentNote.getNote()));
        
        // Color code the grade background
        int bgColor;
        int textColor = Color.WHITE;
        
        if (currentNote.getNote() >= 16) {
            bgColor = 0xFF4CAF50; // Green
        } else if (currentNote.getNote() >= 12) {
            bgColor = 0xFFFF9800; // Orange
        } else if (currentNote.getNote() >= 10) {
            bgColor = 0xFFFFEB3B; // Yellow
            textColor = Color.BLACK;
        } else {
            bgColor = 0xFFF44336; // Red
        }
        
        holder.cardGrade.setCardBackgroundColor(bgColor);
        holder.tvNote.setTextColor(textColor);
        
        // Edit button click
        holder.btnEdit.setOnClickListener(v -> {
            if (editListener != null) {
                editListener.onEditClick(currentNote);
            }
        });
        
        // Delete button click
        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDeleteClick(currentNote);
            }
        });
        
        // Whole item click (optional - for view details)
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(currentNote);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notes != null ? notes.size() : 0;
    }

    public void setNotes(List<Note> notes, List<Student> students, List<Matiere> matieres) {
        this.notes = notes != null ? notes : new ArrayList<>();
        this.students = students != null ? students : new ArrayList<>();
        this.matieres = matieres != null ? matieres : new ArrayList<>();
        notifyDataSetChanged();
    }
    
    private String getStudentName(int studentId) {
        for (Student student : students) {
            if (student.getId() == studentId) {
                return student.getFullName();
            }
        }
        return "Unknown Student";
    }
    
    private String getMatiereName(int matiereId) {
        for (Matiere matiere : matieres) {
            if (matiere.getId() == matiereId) {
                return matiere.getLabel();
            }
        }
        return "Unknown Subject";
    }

    class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView tvStudentName, tvMatiereLabel, tvNote;
        CardView cardGrade;
        Button btnEdit, btnDelete;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudentName = itemView.findViewById(R.id.tvStudentName);
            tvMatiereLabel = itemView.findViewById(R.id.tvMatiereLabel);
            tvNote = itemView.findViewById(R.id.tvNote);
            cardGrade = itemView.findViewById(R.id.cardGrade);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Note note);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
    
    public interface OnEditClickListener {
        void onEditClick(Note note);
    }
    
    public void setOnEditClickListener(OnEditClickListener listener) {
        this.editListener = listener;
    }
    
    public interface OnDeleteClickListener {
        void onDeleteClick(Note note);
    }
    
    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.deleteListener = listener;
    }
}