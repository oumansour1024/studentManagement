package com.example.studentmanagement.ui.matiere;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentmanagement.R;
import com.example.studentmanagement.data.local.entity.Matiere;

import java.util.ArrayList;
import java.util.List;

public class MatiereAdapter extends RecyclerView.Adapter<MatiereAdapter.MatiereViewHolder> {
    private List<Matiere> matieres = new ArrayList<>();
    private OnItemClickListener listener;
    private OnEditClickListener editListener;
    private OnDeleteClickListener deleteListener;

    @NonNull
    @Override
    public MatiereViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_matiere, parent, false);
        return new MatiereViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MatiereViewHolder holder, int position) {
        Matiere currentMatiere = matieres.get(position);
        holder.tvLabel.setText(currentMatiere.getLabel());
        holder.tvCoefficient.setText("Coefficient: " + currentMatiere.getCoefficient());
        
        String weightText;
        if (currentMatiere.getCoefficient() >= 3) {
            weightText = "Weight: High";
        } else if (currentMatiere.getCoefficient() >= 2) {
            weightText = "Weight: Normal";
        } else {
            weightText = "Weight: Low";
        }
        holder.tvWeight.setText(weightText);
        
        // Edit button click
        holder.btnEdit.setOnClickListener(v -> {
            if (editListener != null) {
                editListener.onEditClick(currentMatiere);
            }
        });
        
        // Delete button click
        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDeleteClick(currentMatiere);
            }
        });
        
        // Whole item click (optional)
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(currentMatiere);
            }
        });
    }

    @Override
    public int getItemCount() {
        return matieres != null ? matieres.size() : 0;
    }

    public void setMatieres(List<Matiere> matieres) {
        this.matieres = matieres != null ? matieres : new ArrayList<>();
        notifyDataSetChanged();
    }

    class MatiereViewHolder extends RecyclerView.ViewHolder {
        TextView tvLabel, tvCoefficient, tvWeight;
        Button btnEdit, btnDelete;

        public MatiereViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLabel = itemView.findViewById(R.id.tvLabel);
            tvCoefficient = itemView.findViewById(R.id.tvCoefficient);
            tvWeight = itemView.findViewById(R.id.tvWeight);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Matiere matiere);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
    
    public interface OnEditClickListener {
        void onEditClick(Matiere matiere);
    }
    
    public void setOnEditClickListener(OnEditClickListener listener) {
        this.editListener = listener;
    }
    
    public interface OnDeleteClickListener {
        void onDeleteClick(Matiere matiere);
    }
    
    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.deleteListener = listener;
    }
}