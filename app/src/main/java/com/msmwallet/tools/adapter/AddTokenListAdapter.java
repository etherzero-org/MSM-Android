package com.msmwallet.tools.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.msmwallet.R;
import com.msmwallet.presenter.customviews.BRText;
import com.msmwallet.presenter.entities.TokenItem;

import java.util.ArrayList;

public class AddTokenListAdapter extends RecyclerView.Adapter<AddTokenListAdapter.TokenItemViewHolder> {

    private Context mContext;
    private ArrayList<TokenItem> mTokens;
    private ArrayList<TokenItem> mBackupTokens;
    private static final String TAG = AddTokenListAdapter.class.getSimpleName();
    private OnTokenAddOrRemovedListener mListener;

    public AddTokenListAdapter(Context context, ArrayList<TokenItem> tokens, OnTokenAddOrRemovedListener listener) {

        this.mContext = context;
        this.mTokens = tokens;
        this.mListener = listener;
        this.mBackupTokens = mTokens;
    }

    public interface OnTokenAddOrRemovedListener {

        void onTokenAdded(TokenItem token);

        void onTokenRemoved(TokenItem token);
    }


    @Override
    public void onBindViewHolder(final @NonNull AddTokenListAdapter.TokenItemViewHolder holder, final int position) {

        TokenItem item = mTokens.get(position);
        String tickerName = item.symbol.toLowerCase();

        if (tickerName.equals("1st")) {
            tickerName = "first";
        }

        String iconResourceName = tickerName;
        int iconResourceId = mContext.getResources().getIdentifier(tickerName, "drawable", mContext.getPackageName());

        holder.name.setText(mTokens.get(position).name);
        holder.symbol.setText(mTokens.get(position).symbol);
        try {
            holder.logo.setBackground(mContext.getDrawable(iconResourceId));
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "Error finding icon for -> " + iconResourceName);
        }

        holder.addRemoveButton.setText(mContext.getString(item.isAdded ? R.string.TokenList_remove : R.string.TokenList_add));
        holder.addRemoveButton.setBackground(mContext.getDrawable(item.isAdded ? R.drawable.remove_wallet_button : R.drawable.add_wallet_button));
        holder.addRemoveButton.setTextColor(mContext.getColor(item.isAdded ? R.color.red : R.color.dialog_button_positive));

        holder.addRemoveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Set button to "Remove"
                if (!mTokens.get(position).isAdded) {
                    mTokens.get(position).isAdded = true;
                    mListener.onTokenAdded(mTokens.get(position));
                }

                // Set button back to "Add"
                else {
                    mTokens.get(position).isAdded = false;
                    mListener.onTokenRemoved(mTokens.get(position));

                }

            }
        });


    }

    @Override
    public int getItemCount() {
        return mTokens.size();
    }

    @NonNull
    @Override
    public AddTokenListAdapter.TokenItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        View convertView = inflater.inflate(R.layout.token_list_item, parent, false);

        TokenItemViewHolder holder = new TokenItemViewHolder(convertView);
        holder.setIsRecyclable(false);

        return holder;
    }

    public class TokenItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView logo;
        private BRText symbol;
        private BRText name;
        private Button addRemoveButton;

        public TokenItemViewHolder(View view) {
            super(view);

            logo = view.findViewById(R.id.token_icon);
            symbol = view.findViewById(R.id.token_ticker);
            name = view.findViewById(R.id.token_name);
            addRemoveButton = view.findViewById(R.id.add_remove_button);

            Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/CircularPro-Book.otf");
            addRemoveButton.setTypeface(typeface);
        }
    }

    public void resetFilter() {
        mTokens = mBackupTokens;
        notifyDataSetChanged();
    }

    public void filter(String query) {
        resetFilter();
        ArrayList<TokenItem> filteredList = new ArrayList<>();

        query = query.toLowerCase();

        for (TokenItem item : mTokens) {
            if (item.name.toLowerCase().contains(query) || item.symbol.toLowerCase().contains(query)) {
                filteredList.add(item);
            }
        }

        mTokens = filteredList;
        notifyDataSetChanged();

    }

}
