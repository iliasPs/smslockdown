package com.ip.smslockdown1.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.util.CollectionUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.ip.smslockdown1.R;
import com.ip.smslockdown1.databinding.PlacesListItemBinding;
import com.ip.smslockdown1.settings.ParameterLoader;
import com.ip.smslockdown1.settings.ParameterLoaderImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class PlacesAutoCompleteAdapter extends RecyclerView.Adapter<PlacesAutoCompleteAdapter.PredictionHolder> implements Filterable {
    private static final String TAG = "PlacesAutoAdapter";
    private static final String KEY = "my_places_key";
    private String apiKey;
    private final PlacesClient placesClient;
    private ArrayList<AutoCompletePrediction> mResultList = new ArrayList<>();
    private Context mContext;
    private final CharacterStyle STYLE_BOLD;
    private final CharacterStyle STYLE_NORMAL;
    private ClickListener clickListener;


    public PlacesAutoCompleteAdapter(Context context) {
        mContext = context;
        STYLE_BOLD = new StyleSpan(Typeface.BOLD);
        STYLE_NORMAL = new StyleSpan(Typeface.NORMAL);

        if (!Places.isInitialized()) {
            Locale locale = new Locale("el");
            ParameterLoaderImpl parameterLoader = new ParameterLoaderImpl(context);
            loadParameters(parameterLoader);
            Places.initialize(context, apiKey, locale);
        }

        placesClient = Places.createClient(context);
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    /**
     * Returns the filter for the current set of autocomplete results.
     */
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                // Skip the autocomplete query if no constraints are given.
                if (constraint != null) {
                    // Query the autocomplete API for the (constraint) search string.
                    mResultList = getPredictions(constraint);
                    if (mResultList != null) {
                        // The API successfully returned results.
                        results.values = mResultList;
                        results.count = mResultList.size();
                    }
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    // The API returned at least one result, update the data.
                    notifyDataSetChanged();
                } else {
                    // The API did not return any results, invalidate the data set.
                    //notifyDataSetInvalidated();
                }
            }
        };
    }

    private ArrayList<AutoCompletePrediction> getPredictions(CharSequence constraint) {

        final ArrayList<AutoCompletePrediction> resultList = new ArrayList<>();

        // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
        // and once again when the user makes a selection (for example when calling fetchPlace()).
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

        //https://gist.github.com/graydon/11198540
        // Use the builder to create a FindAutocompletePredictionsRequest.
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                // Call either setLocationBias() OR setLocationRestriction().
                //.setLocationBias(bounds)
                //.setCountry("BD")
                //.setTypeFilter(TypeFilter.ADDRESS)
                .setSessionToken(token)
                .setQuery(constraint.toString())
                .build();

        Task<FindAutocompletePredictionsResponse> autocompletePredictions = placesClient.findAutocompletePredictions(request);

        // This method should have been called off the main UI thread. Block and wait for at most
        // 60s for a result from the API.
        try {
            Tasks.await(autocompletePredictions, 60, TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            e.printStackTrace();
        }

        if (autocompletePredictions.isSuccessful()) {
            FindAutocompletePredictionsResponse findAutocompletePredictionsResponse = autocompletePredictions.getResult();
            if (findAutocompletePredictionsResponse != null)
                for (AutocompletePrediction prediction : findAutocompletePredictionsResponse.getAutocompletePredictions()) {
                    Log.i(TAG, prediction.getPlaceId());
                    resultList.add(new AutoCompletePrediction(prediction.getPlaceId(), prediction.getPrimaryText(STYLE_NORMAL).toString(), prediction.getFullText(STYLE_BOLD).toString()));
                }

            return resultList;
        } else {
            return resultList;
        }

    }

    @NonNull
    @Override
    public PredictionHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        PlacesListItemBinding placesListItemBinding = PlacesListItemBinding.inflate(layoutInflater, viewGroup, false);
        return new PredictionHolder(placesListItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull PredictionHolder mPredictionHolder, final int i) {
        mPredictionHolder.address.setText(mResultList.get(i).address);
        mPredictionHolder.area.setText(mResultList.get(i).area);
    }

    @Override
    public int getItemCount() {
        return mResultList.size();
    }

    public AutoCompletePrediction getItem(int position) {
        return mResultList.get(position);
    }

    public interface ClickListener {
        void click(Place place);
    }

    public class PredictionHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView address;
        private final TextView area;
        private LinearLayout mRow;

        PredictionHolder(PlacesListItemBinding binding) {
            super(binding.getRoot());
            area = binding.placeArea;
            address = binding.placeAddress;
            mRow = binding.placeItemView;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (!CollectionUtils.isEmpty(mResultList)) {
                AutoCompletePrediction item = mResultList.get(getAdapterPosition());

                if (v.getId() == R.id.place_item_view) {

                    String placeId = String.valueOf(item.placeId);

                    List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
                    FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields).build();
                    placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                        @Override
                        public void onSuccess(FetchPlaceResponse response) {
                            Place place = response.getPlace();
                            clickListener.click(place);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            if (exception instanceof ApiException) {
                                Toast.makeText(mContext, exception.getMessage() + "", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        }
    }

    /**
     * Holder for Places Geo Data Autocomplete API results.
     */
    public class AutoCompletePrediction {

        public CharSequence placeId;
        public CharSequence address, area;

        AutoCompletePrediction(CharSequence placeId, CharSequence area, CharSequence address) {
            this.placeId = placeId;
            this.area = area;
            this.address = address;
        }

        @Override
        public String toString() {
            return area.toString();
        }
    }

    private void loadParameters(final ParameterLoader parameterLoader) {

        Log.v(TAG, "Starting Places API...");

        try {
            apiKey = parameterLoader.getString(KEY).trim();
        } catch (Exception e) {
            apiKey = null;
        }

    }
}