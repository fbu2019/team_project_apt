package com.example.skillshop.NavigationFragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.skillshop.Adapters.ClassAdapterCardMini;
import com.example.skillshop.Models.Query;
import com.example.skillshop.Models.Workshop;
import com.example.skillshop.NavigationFragments.subCategories.ArtsDisplayFragment;
import com.example.skillshop.NavigationFragments.subCategories.CulinaryDisplayFragment;
import com.example.skillshop.NavigationFragments.subCategories.EducationDisplayFragment;
import com.example.skillshop.NavigationFragments.subCategories.FitnessDisplayFragment;
import com.example.skillshop.NavigationFragments.subCategories.OtherDisplayFragment;
import com.example.skillshop.R;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

public class CategoryChooseFragment extends Fragment {


    CardView cvAll;
    CardView cvEducation;
    CardView cvCulinary;
    CardView cvFitness;
    CardView cvArts;
    CardView cvOther;
    ImageView btnAll;

    private RecyclerView rvPopularClasses;
    protected ArrayList<Workshop> mWorkshops;
    protected ClassAdapterCardMini classAdapter;





    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate((R.layout.fragment_category_choose), container, false);

    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);



        setUpAllCard(view);

        setUpEducationCard(view);
        setUpCulinaryCard(view);
        setUpFitnessCard(view);
        setUpArtsCard(view);
        setUpOtherCard(view);

        connectRecyclerView(view);
        filterFeed();

    }

    private void connectRecyclerView(View view) {
        //find the RecyclerView
        rvPopularClasses = view.findViewById(R.id.rvPopularClasses);
        //init the arraylist (data source)
        mWorkshops = new ArrayList<>();
        //construct the adapter from this datasource
        classAdapter = new ClassAdapterCardMini(mWorkshops,getContext());

        final GridLayoutManager layout = new GridLayoutManager(getContext(), 1, GridLayoutManager.HORIZONTAL, false);

        //RecyclerView setup (layout manager, use adapter)
        rvPopularClasses.setLayoutManager(layout);
        //set the adapter
        rvPopularClasses.setAdapter(classAdapter);

    }

    private void filterFeed() {
        mWorkshops.clear();
        classAdapter.notifyDataSetChanged();
        Query parseQuery = new Query();
        // query add all classes with all data and sort by time of class and only show new classes
        parseQuery.getAllClasses().withItems().getClassesNotTaking().setLimit(10).orderByDescending("students");


        parseQuery.findInBackground(new FindCallback<Workshop>() {
            @Override
            public void done(List<Workshop> objects, ParseException e) {
                //
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {
                        Workshop workshopItem = objects.get(i);
                        mWorkshops.add(workshopItem);
                        classAdapter.notifyItemInserted(mWorkshops.size() - 1);
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }



    public void setUpOtherCard(View v)
    {
        cvOther = v.findViewById(R.id.cvOther);
        cvOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryTransition("Other");
            }
        });
    }
    public void setUpArtsCard(View v)
    {
        cvArts = v.findViewById(R.id.cvArts);

        cvArts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryTransition("Arts/Crafts");

            }
        });
    }
    public void setUpFitnessCard(View v)
    {
        cvFitness = v.findViewById(R.id.cvFitness);

        cvFitness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryTransition("Fitness");
            }
        });
    }


    public void setUpCulinaryCard(View v)
    {
        cvCulinary = v.findViewById(R.id.cvCulinary);

        cvCulinary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryTransition("Culinary");

            }
        });
    }


    public void setUpEducationCard(View v)
    {
        cvEducation = v.findViewById(R.id.cvEducation);

        cvEducation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryTransition("Education");

            }
        });
    }


    public void setUpAllCard(View v)
    {
        cvAll = v.findViewById(R.id.cvAll);

        cvAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allCategoryTransition();
            }
        });

        btnAll = v.findViewById(R.id.btnNavAll);
        btnAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allCategoryTransition();
            }
        });
    }

    public void allCategoryTransition()
    {
        // create new fragment to use
        Fragment home = new HomeFragment();
        // transaction on current activity
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

        transaction.setCustomAnimations(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);

        transaction.replace(R.id.flContainer, home);
        transaction.addToBackStack(null);
        // Commit the transaction
        transaction.commit();

    }

    public void categoryTransition(String category)
    {
        Fragment fragment = new EducationDisplayFragment();
        switch (category){
            case "Education":
                fragment = new EducationDisplayFragment();
                break;
            case "Culinary":
                fragment = new CulinaryDisplayFragment();
                break;
            case "Fitness":
                fragment = new FitnessDisplayFragment();
                break;
            case "Arts/Crafts":
                fragment = new ArtsDisplayFragment();
                break;
            case "Other":
                fragment = new OtherDisplayFragment();
                break;



        }

        Bundle bundle = new Bundle();
        bundle.putString("Category", category);
        fragment.setArguments(bundle);

        // transaction on current activity
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
        transaction.replace(R.id.flContainer, fragment);

        transaction.addToBackStack(null);
        // Commit the transaction
        transaction.commit();

    }



}