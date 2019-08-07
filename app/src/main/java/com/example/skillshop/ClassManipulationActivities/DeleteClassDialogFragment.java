package com.example.skillshop.ClassManipulationActivities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.skillshop.R;

public class DeleteClassDialogFragment extends DialogFragment {


    Button btnCancel;
    Button btnDeleteClass;
    public DeleteClassDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static DeleteClassDialogFragment newInstance(String title) {
        DeleteClassDialogFragment frag = new DeleteClassDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog_delete, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        btnCancel = (Button) view.findViewById(R.id.btnCancel);
        btnDeleteClass = (Button) view.findViewById(R.id.btnDeleteClass);
        // Fetch arguments from bundle and set title
       // String title = getArguments().getString("title", "Enter Name");
       // getDialog().setTitle(title);
        // Show soft keyboard automatically and request focus to field
       // mEditText.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

}
