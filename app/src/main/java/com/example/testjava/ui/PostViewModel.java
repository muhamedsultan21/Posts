package com.example.testjava.ui;

import android.annotation.SuppressLint;
import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.testjava.db.PostsDao;
import com.example.testjava.db.PostsDatabase;
import com.example.testjava.models.Post;
import com.example.testjava.repository.PostsRepository;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostViewModel extends ViewModel {
    MutableLiveData<List<Post>> posts = new MutableLiveData<>();
    private final PostsRepository postsRepository;

    public PostViewModel(Application application) {
        PostsDao postsDatabase = PostsDatabase.getInstance(application).postsDao();
        postsRepository = new PostsRepository(postsDatabase);
    }

    public void fetchPosts() {
        postsRepository.get().enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(@NonNull Call<List<Post>> call, @NonNull Response<List<Post>> response) {
                posts.postValue(response.body());
                Log.e("PostViewModel", "ok: " + response.body());

            }

            @Override
            public void onFailure(@NonNull Call<List<Post>> call, @NonNull Throwable t) {
                Log.e("PostViewModel", "Failed to fetch posts: " + t.getMessage());
            }
        });
    }

    @SuppressLint("CheckResult")
    public void addPostToFavourite(Post post) {
        postsRepository.addPostToFavourite(post)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                }, throwable -> {
                });
    }

    public LiveData<List<Post>> getFavouritePosts() {
        return postsRepository.getFavouritePosts();
    }


    @SuppressLint("CheckResult")
    public void deleteFromFavourite(Post post) {
        postsRepository.deleteFromFavourite(post)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(() -> {
                }, throwable -> {
                });
    }

}