package com.appbikeroute.ui.salud;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.appbikeroute.AuthActivity;
import com.appbikeroute.R;
import com.appbikeroute.databinding.FragmentSaludBinding;
import com.google.firebase.auth.FirebaseAuth;

public class SaludFragment extends Fragment {

    Button bsalir;
    private FirebaseAuth mAuth;
    View vista;

    private FragmentSaludBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SaludViewModel saludViewModel = new ViewModelProvider(this).get(SaludViewModel.class);

        binding = FragmentSaludBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //final TextView textView = binding.textGallery;
        //saludViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
        //    @Override
        //    public void onChanged(@Nullable String s) {
        //        textView.setText(s);
        //    }
        //});
        vista = root;

        //mAuth = FirebaseAuth.getInstance();





        return vista;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}