/*
 *  Copyright 2018 Soojeong Shin
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.example.android.popularmovies.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.model.Review;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    /** Member variable for the list of {@link Review}s */
    private List<Review> mReviews;

    /**
     * Constructor for ReviewAdapter that accepts a list of reviews to display
     *
     * @param reviews List of {@link Review}s
     */
    public ReviewAdapter(List<Review> reviews) {
        mReviews = reviews;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.review_list_item, viewGroup, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = mReviews.get(position);
        holder.bind(review);
    }

    @Override
    public int getItemCount() {
        if (null == mReviews) return 0;
        return mReviews.size();
    }

    public void addAll(List<Review> reviews) {
        mReviews.clear();
        mReviews.addAll(reviews);
        notifyDataSetChanged();
    }

    /**
     * Cache of the children views for a review list item.
     */
    public class ReviewViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_review_author)
        TextView mReviewAuthorTextView;

        @BindView(R.id.tv_review_content)
        TextView mReviewContentTextView;

        /**
         * Constructor for our ViewHolder
         *
         * @param itemView The View that you inflated in {@link ReviewAdapter#onCreateViewHolder(ViewGroup, int)}
         */
        ReviewViewHolder(View itemView) {
            super(itemView);

            // Bind the view using ButterKnife
            ButterKnife.bind(this, itemView);
        }

        void bind(Review review) {
            mReviewAuthorTextView.setText(review.getAuthor());
            mReviewContentTextView.setText(review.getContent());
        }
    }
}
