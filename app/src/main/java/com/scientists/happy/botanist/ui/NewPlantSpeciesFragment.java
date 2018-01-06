package com.scientists.happy.botanist.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.futuremind.recyclerviewfastscroll.FastScroller;
import com.futuremind.recyclerviewfastscroll.SectionTitleProvider;
import com.scientists.happy.botanist.R;
import com.scientists.happy.botanist.data.DatabaseManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NewPlantSpeciesFragment extends Fragment {

    final SpeciesAdapter mSpeciesAdapter = new SpeciesAdapter();
    RecyclerView mSpeciesList;
    TextView mEmptyList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_new_plant_species, container, false);
        mSpeciesList = rootView.findViewById(R.id.plant_recycler_view);
        mSpeciesList.setLayoutManager(new LinearLayoutManager(getContext()));
        mSpeciesList.setAdapter(mSpeciesAdapter);
        mEmptyList = rootView.findViewById(R.id.empty_list_view);
        FastScroller fastScroller = rootView.findViewById(R.id.fast_scroller);
        fastScroller.setRecyclerView(mSpeciesList);
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_species, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mSpeciesAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                mSpeciesAdapter.getFilter().filter(query);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return id == R.id.action_search || super.onOptionsItemSelected(item);
    }

    private class SpeciesAdapter extends RecyclerView.Adapter<SpeciesAdapter.SpeciesHolder>
            implements SectionTitleProvider, Filterable {

        final private List<String> mSpecies = new ArrayList<>(Arrays.asList(DatabaseManager.getInstance().getSpeciesNames()));
        private List<String> mSpeciesFiltered = mSpecies;

        @Override
        public SpeciesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            // Inflate the custom layout
            View speciesView = inflater.inflate(R.layout.list_item_text_button, parent, false);

            return new SpeciesHolder(speciesView);
        }

        @Override
        public void onBindViewHolder(SpeciesHolder holder, int position) {
            holder.setSpecies(mSpeciesFiltered.get(position));
        }

        @Override
        public int getItemCount() {
            return mSpeciesFiltered.size();
        }

        @Override
        public String getSectionTitle(int position) {
            return mSpeciesFiltered.get(position).substring(0,1).toUpperCase();
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    String charString = charSequence.toString();
                    if (charString.isEmpty()) {
                        mSpeciesFiltered = mSpecies;
                    } else {
                        List<String> filteredList = new ArrayList<>();
                        for (String species : mSpecies) {
                            if (species.toLowerCase().contains(charString.toLowerCase())) {
                                filteredList.add(species);
                            }
                        }
                        mSpeciesFiltered = filteredList;
                    }
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = mSpeciesFiltered;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    notifyDataSetChanged();
                    if (mSpeciesFiltered.isEmpty()) {
                        mSpeciesList.setVisibility(View.GONE);
                        mEmptyList.setVisibility(View.VISIBLE);
                    } else {
                        mSpeciesList.setVisibility(View.VISIBLE);
                        mEmptyList.setVisibility(View.GONE);
                    }
                }
            };
        }

        class SpeciesHolder extends RecyclerView.ViewHolder {

            private final TextView mSpeciesTextView;
            private final Button mAddButton;

            SpeciesHolder(View itemView) {
                super(itemView);
                mSpeciesTextView = itemView.findViewById(R.id.text);
                mAddButton = itemView.findViewById(R.id.button);
            }

            void setSpecies(final String species) {
                mSpeciesTextView.setText(species);
                mAddButton.setText(R.string.add);
                mAddButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putString("species", species);
                        NewPlantDetailsFragment plantDetailsFragment = new NewPlantDetailsFragment();
                        plantDetailsFragment.setArguments(bundle);
                        //noinspection ConstantConditions
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        // Replace whatever is in the fragment_container view with this fragment,
                        // and add the transaction to the back stack so the user can navigate back
                        transaction.replace(R.id.root_layout, plantDetailsFragment);
                        transaction.addToBackStack(null);
                        // Commit the transaction
                        transaction.commit();
                    }
                });
            }
        }
    }
}
